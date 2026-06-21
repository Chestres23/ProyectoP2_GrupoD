package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setName("Test FindByEmail");
        user.setEmail("findbyemail@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("findbyemail@espe.edu.ec");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test FindByEmail");
        assertThat(found.get().getEmail()).isEqualTo("findbyemail@espe.edu.ec");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("noexiste@espe.edu.ec");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = new User();
        user.setName("Test ExistsByEmail");
        user.setEmail("exists@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@espe.edu.ec");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("noexiste2@espe.edu.ec");

        assertThat(exists).isFalse();
    }
}
