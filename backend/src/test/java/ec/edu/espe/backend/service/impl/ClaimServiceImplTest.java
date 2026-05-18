package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.ClaimStatus;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    ClaimRepository claimRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ClaimServiceImpl claimService;

    @Test
    void create_whenDuplicatePending_thenThrows() {
        ClaimRequestDTO dto = new ClaimRequestDTO();
        dto.setItemId(1L);
        dto.setObservation("obs");

        when(claimRepository.existsByItemIdAndUser_IdAndStatus(1L, 1L, ClaimStatus.PENDING)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> claimService.create(dto, 1L));
    }

    @Test
    void create_success() {
        ClaimRequestDTO dto = new ClaimRequestDTO();
        dto.setItemId(2L);
        dto.setObservation("ok");

        when(claimRepository.existsByItemIdAndUser_IdAndStatus(2L, 1L, ClaimStatus.PENDING)).thenReturn(false);

        User user = new User();
        user.setId(1L);
        user.setEmail("u@u.com");
        user.setName("User");

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        Claim saved = new Claim();
        saved.setId(10L);
        saved.setItemId(2L);
        saved.setObservation("ok");
        saved.setClaimDate(LocalDateTime.now());
        saved.setStatus(ClaimStatus.PENDING);
        saved.setUser(user);

        when(claimRepository.save(any(Claim.class))).thenReturn(saved);

        ClaimResponseDTO res = claimService.create(dto, 1L);

        assertNotNull(res);
        assertEquals(10L, res.getId());
        assertEquals(2L, res.getItemId());
        assertEquals(ClaimStatus.PENDING, res.getStatus());
    }

    @Test
    void approve_setsApproved() {
        Claim c = new Claim();
        c.setId(5L);
        c.setStatus(ClaimStatus.PENDING);

        when(claimRepository.findById(5L)).thenReturn(Optional.of(c));
        when(claimRepository.save(any(Claim.class))).thenReturn(c);

        claimService.approve(5L);

        assertEquals(ClaimStatus.APPROVED, c.getStatus());
    }

    @Test
    void reject_setsRejected() {
        Claim c = new Claim();
        c.setId(6L);
        c.setStatus(ClaimStatus.PENDING);

        when(claimRepository.findById(6L)).thenReturn(Optional.of(c));
        when(claimRepository.save(any(Claim.class))).thenReturn(c);

        claimService.reject(6L);

        assertEquals(ClaimStatus.REJECTED, c.getStatus());
    }
}
