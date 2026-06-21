package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LostItemRepositoryTest {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindOnlyActiveItems() {
        User user = new User();
        user.setName("Test User Items");
        user.setEmail("test.items@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem activeItem = new LostItem();
        activeItem.setName("Celular activo");
        activeItem.setCategory("Tecnología");
        activeItem.setLocationFound("Biblioteca");
        activeItem.setStatus(ItemStatus.FOUND);
        activeItem.setActive(true);
        activeItem.setUser(user);
        lostItemRepository.save(activeItem);

        LostItem inactiveItem = new LostItem();
        inactiveItem.setName("Celular inactivo");
        inactiveItem.setCategory("Tecnología");
        inactiveItem.setLocationFound("Cafetería");
        inactiveItem.setStatus(ItemStatus.DELIVERED);
        inactiveItem.setActive(false);
        inactiveItem.setUser(user);
        lostItemRepository.save(inactiveItem);

        List<LostItem> activeItems = lostItemRepository.findByActiveTrue();

        assertThat(activeItems).hasSize(1);
        assertThat(activeItems.get(0).getName()).isEqualTo("Celular activo");
        assertThat(activeItems.get(0).getActive()).isTrue();
    }

    @Test
    void shouldFindActiveItemById() {
        User user = new User();
        user.setName("Test User FindById");
        user.setEmail("test.findbyid.item@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Mochila roja");
        item.setCategory("Bolsos");
        item.setLocationFound("Aula 201");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Optional<LostItem> found = lostItemRepository.findByIdAndActiveTrue(item.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mochila roja");
        assertThat(found.get().getCategory()).isEqualTo("Bolsos");
    }

    @Test
    void shouldNotFindInactiveItemById() {
        User user = new User();
        user.setName("Test User Inactive");
        user.setEmail("test.inactive.item@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Objeto eliminado");
        item.setCategory("Otros");
        item.setLocationFound("Parque");
        item.setStatus(ItemStatus.DELIVERED);
        item.setActive(false);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Optional<LostItem> found = lostItemRepository.findByIdAndActiveTrue(item.getId());

        assertThat(found).isEmpty();
    }
}
