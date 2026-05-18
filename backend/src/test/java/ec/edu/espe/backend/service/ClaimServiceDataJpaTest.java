package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ClaimStatus;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.exception.DuplicateClaimException;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({ClaimServiceImpl.class})
public class ClaimServiceDataJpaTest {

    @Autowired
    private ClaimService service;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Test
    void shouldCreateClaimWhenRequestIsValid() {
        User user = new User();
        user.setName("Usuario Prueba");
        user.setEmail("usuario@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Bolso azul");
        item.setCategory("Accesorios");
        item.setLocationFound("Biblioteca");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        ClaimRequestDTO request = new ClaimRequestDTO();
        request.setUserId(user.getId());
        request.setItemId(item.getId());
        request.setObservation("Reclamo válido");

        ClaimResponseDTO result = service.createClaim(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.PENDING);
        assertThat(result.getItemId()).isEqualTo(item.getId());
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(claimRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldNotAllowDuplicateClaimForSameUserAndItem() {
        User user = new User();
        user.setName("Usuario Duplicado");
        user.setEmail("duplicado@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Llave roja");
        item.setCategory("Accesorios");
        item.setLocationFound("Cafetería");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Primer reclamo");
        claim.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim);

        ClaimRequestDTO request = new ClaimRequestDTO();
        request.setUserId(user.getId());
        request.setItemId(item.getId());
        request.setObservation("Reclamo duplicado");

        assertThatThrownBy(() -> service.createClaim(request))
                .isInstanceOf(DuplicateClaimException.class);
    }
}
