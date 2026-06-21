package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserServiceImpl.class})
public class UserServiceDataJpaTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveNewUser() {
        User user = new User();
        user.setName("Nuevo Usuario");
        user.setEmail("nuevo@espe.edu.ec");
        user.setPassword("password123");
        user.setActive(true);

        User saved = service.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Nuevo Usuario");
        assertThat(saved.getEmail()).isEqualTo("nuevo@espe.edu.ec");
    }

    @Test
    void shouldNotSaveUserWithDuplicateEmail() {
        User user1 = new User();
        user1.setName("Usuario 1");
        user1.setEmail("duplicado@espe.edu.ec");
        user1.setPassword("password");
        user1.setActive(true);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Usuario 2");
        user2.setEmail("duplicado@espe.edu.ec");
        user2.setPassword("password");
        user2.setActive(true);

        assertThatThrownBy(() -> service.save(user2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email ya registrado");
    }

    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setName("Usuario FindById");
        user.setEmail("findbyid@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        Optional<User> found = service.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Usuario FindById");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        Optional<User> found = service.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = new User();
        user1.setName("Usuario 1");
        user1.setEmail("user1@espe.edu.ec");
        user1.setPassword("password");
        user1.setActive(true);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Usuario 2");
        user2.setEmail("user2@espe.edu.ec");
        user2.setPassword("password");
        user2.setActive(true);
        userRepository.save(user2);

        List<User> users = service.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setName("Usuario Email");
        user.setEmail("email@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        userRepository.save(user);

        Optional<User> found = service.findByEmail("email@espe.edu.ec");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Usuario Email");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = service.findByEmail("noexiste@espe.edu.ec");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldDeactivateUser() {
        User user = new User();
        user.setName("Usuario Desactivar");
        user.setEmail("desactivar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        service.deactivate(user.getId());

        User deactivated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(deactivated.getActive()).isFalse();
    }

    @Test
    void shouldThrowWhenDeactivatingNonExistentUser() {
        assertThatThrownBy(() -> service.deactivate(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
