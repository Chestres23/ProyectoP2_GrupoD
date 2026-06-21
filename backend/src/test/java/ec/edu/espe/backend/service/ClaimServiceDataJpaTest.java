package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ClaimStatus;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.exception.ClaimNotFoundException;
import ec.edu.espe.backend.exception.DuplicateClaimException;
import ec.edu.espe.backend.exception.InvalidClaimStateException;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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

    @Test
    void shouldReturnAllActiveClaims() {
        User user = new User();
        user.setName("Usuario FindAll");
        user.setEmail("findall@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Celular");
        item.setCategory("Tecnología");
        item.setLocationFound("Aula 101");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo activo");
        claim.setStatus(ClaimStatus.PENDING);
        claimRepository.save(claim);

        List<ClaimResponseDTO> result = service.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getObservation()).isEqualTo("Reclamo activo");
    }

    @Test
    void shouldApproveClaimAndChangeItemStatus() {
        User user = new User();
        user.setName("Usuario Aprobar");
        user.setEmail("aprobar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Reloj");
        item.setCategory("Accesorios");
        item.setLocationFound("Laboratorio");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo para aprobar");
        claim.setStatus(ClaimStatus.PENDING);
        claim = claimRepository.save(claim);

        ClaimResponseDTO result = service.approve(claim.getId());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.APPROVED);

        LostItem updatedItem = lostItemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getStatus()).isEqualTo(ItemStatus.CLAIMED);
    }

    @Test
    void shouldNotApproveNonPendingClaim() {
        User user = new User();
        user.setName("Usuario No Aprobar");
        user.setEmail("noaprobar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Cuaderno");
        item.setCategory("Papelería");
        item.setLocationFound("Aula 202");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo ya aprobado");
        claim.setStatus(ClaimStatus.APPROVED);
        claim = claimRepository.save(claim);

        Long claimId = claim.getId();

        assertThatThrownBy(() -> service.approve(claimId))
                .isInstanceOf(InvalidClaimStateException.class);
    }

    @Test
    void shouldRejectPendingClaim() {
        User user = new User();
        user.setName("Usuario Rechazar");
        user.setEmail("rechazar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Paraguas");
        item.setCategory("Accesorios");
        item.setLocationFound("Entrada");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo para rechazar");
        claim.setStatus(ClaimStatus.PENDING);
        claim = claimRepository.save(claim);

        ClaimResponseDTO result = service.reject(claim.getId());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.REJECTED);
    }

    @Test
    void shouldNotRejectNonPendingClaim() {
        User user = new User();
        user.setName("Usuario No Rechazar");
        user.setEmail("norechazar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Gafas");
        item.setCategory("Accesorios");
        item.setLocationFound("Parqueadero");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo ya rechazado");
        claim.setStatus(ClaimStatus.REJECTED);
        claim = claimRepository.save(claim);

        Long claimId = claim.getId();

        assertThatThrownBy(() -> service.reject(claimId))
                .isInstanceOf(InvalidClaimStateException.class);
    }

    @Test
    void shouldSoftDeleteClaim() {
        User user = new User();
        user.setName("Usuario Eliminar");
        user.setEmail("eliminar@espe.edu.ec");
        user.setPassword("password");
        user.setActive(true);
        user = userRepository.save(user);

        LostItem item = new LostItem();
        item.setName("Calculadora");
        item.setCategory("Tecnología");
        item.setLocationFound("Laboratorio");
        item.setStatus(ItemStatus.FOUND);
        item.setActive(true);
        item.setUser(user);
        item = lostItemRepository.save(item);

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation("Reclamo a eliminar");
        claim.setStatus(ClaimStatus.PENDING);
        claim = claimRepository.save(claim);

        service.deleteClaim(claim.getId());

        Claim deleted = claimRepository.findById(claim.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();
    }

    @Test
    void shouldThrowWhenDeletingNonExistentClaim() {
        assertThatThrownBy(() -> service.deleteClaim(999L))
                .isInstanceOf(ClaimNotFoundException.class);
    }
}
