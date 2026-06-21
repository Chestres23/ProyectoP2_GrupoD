package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ClaimStatus;
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
public class ClaimRepositoryTest {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Test
    void shouldSaveAndFindActiveClaims() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.user@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Llave Azul");
        item.setCategory("Accesorios");
        item.setLocationFound("Biblioteca");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo por llave encontrada");
        claim.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim);

        List<Claim> claims = claimRepository.findAllByActiveTrueOrderByClaimDateDesc();

        assertThat(claims).isNotEmpty();
        assertThat(claims).hasSize(1);
        assertThat(claims.get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldReturnTrueWhenClaimExistsForUserAndItem() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.user2@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Tarjeta USB");
        item.setCategory("Tecnología");
        item.setLocationFound("Parque");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo existente");
        claim.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim);

        boolean exists = claimRepository.existsByUser_IdAndItem_Id(user.getId(), item.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindAllClaimsOrderedByClaimDateDesc() {
        User user = new User();
        user.setName("Test User Order");
        user.setEmail("test.order@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item1 = new LostItem();
        item1.setName("Objeto 1");
        item1.setCategory("Accesorios");
        item1.setLocationFound("Aula 1");
        item1.setStatus(ItemStatus.FOUND);
        item1.setActive(true);
        item1.setUser(user);
        item1 = lostItemRepository.save(item1);

        LostItem item2 = new LostItem();
        item2.setName("Objeto 2");
        item2.setCategory("Tecnología");
        item2.setLocationFound("Aula 2");
        item2.setStatus(ItemStatus.FOUND);
        item2.setActive(true);
        item2.setUser(user);
        item2 = lostItemRepository.save(item2);

        Claim claim1 = new Claim();
        claim1.setUser(user);
        claim1.setItem(item1);
        claim1.setObservation("Primer reclamo");
        claim1.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim1);

        Claim claim2 = new Claim();
        claim2.setUser(user);
        claim2.setItem(item2);
        claim2.setObservation("Segundo reclamo");
        claim2.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim2);

        List<Claim> claims = claimRepository.findAllByOrderByClaimDateDesc();

        assertThat(claims).hasSize(2);
    }

    @Test
    void shouldFindClaimById() {
        User user = new User();
        user.setName("Test User FindById");
        user.setEmail("test.findbyid@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Mochila negra");
        item.setCategory("Bolsos");
        item.setLocationFound("Comedor");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo para buscar por ID");
        claim.setStatus(ClaimStatus.PENDING);
        claim = claimRepository.save(claim);

        Optional<Claim> found = claimRepository.findById(claim.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getObservation()).isEqualTo("Reclamo para buscar por ID");
        assertThat(found.get().getStatus()).isEqualTo(ClaimStatus.PENDING);
    }
}
