package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.ClaimStatus;
import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.service.ClaimService;
import ec.edu.espe.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final UserService userService;

    public ClaimServiceImpl(ClaimRepository claimRepository, UserService userService) {
        this.claimRepository = claimRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ClaimResponseDTO create(ClaimRequestDTO dto, Long userId) {
        boolean exists = claimRepository.existsByItemIdAndUser_IdAndStatus(dto.getItemId(), userId, ClaimStatus.PENDING);
        if (exists) throw new IllegalStateException("Ya existe un reclamo pendiente para este item por el usuario");

        Claim c = new Claim();
        c.setItemId(dto.getItemId());
        c.setObservation(dto.getObservation());
        c.setClaimDate(LocalDateTime.now());
        c.setStatus(ClaimStatus.PENDING);
        userService.findById(userId).ifPresent(c::setUser);
        Claim saved = claimRepository.save(c);
        return ClaimResponseDTO.from(saved);
    }

    @Override
    public List<ClaimResponseDTO> findAll() {
        return claimRepository.findAll().stream().map(ClaimResponseDTO::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approve(Long id) {
        Claim c = claimRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        c.setStatus(ClaimStatus.APPROVED);
        claimRepository.save(c);
    }

    @Override
    @Transactional
    public void reject(Long id) {
        Claim c = claimRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        c.setStatus(ClaimStatus.REJECTED);
        claimRepository.save(c);
    }
}
