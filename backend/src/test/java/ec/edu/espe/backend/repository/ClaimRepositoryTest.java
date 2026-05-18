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
}
