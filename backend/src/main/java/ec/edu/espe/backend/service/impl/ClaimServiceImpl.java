package ec.edu.espe.backend.service.impl;

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
import ec.edu.espe.backend.exception.ItemNotFoundException;
import ec.edu.espe.backend.exception.UserNotFoundException;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.ClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            UserRepository userRepository,
                            LostItemRepository lostItemRepository) {
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
    }

    @Override
    public ClaimResponseDTO createClaim(ClaimRequestDTO request) {
        if (claimRepository.existsByUser_IdAndItem_Id(request.getUserId(), request.getItemId())) {
            throw new DuplicateClaimException("El usuario ya registró un reclamo para este objeto.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        LostItem item = lostItemRepository.findByIdAndActiveTrue(request.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Objeto no encontrado o inactivo."));

        if (item.getStatus() != ItemStatus.FOUND) {
            throw new InvalidClaimStateException("Solo se puede reclamar un objeto en estado FOUND.");
        }

        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setObservation(request.getObservation().trim());
        claim.setStatus(ClaimStatus.PENDING);

        return ClaimResponseDTO.from(claimRepository.save(claim));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDTO> findAll() {
        return claimRepository.findAllByActiveTrueOrderByClaimDateDesc()
                .stream()
                .map(ClaimResponseDTO::from)
                .toList();
    }

    @Override
    public ClaimResponseDTO approve(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Reclamo no encontrado."));

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new InvalidClaimStateException("Solo se puede aprobar un reclamo pendiente.");
        }

        LostItem item = claim.getItem();
        if (item.getStatus() != ItemStatus.FOUND) {
            throw new InvalidClaimStateException("El objeto ya fue reclamado o entregado.");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        item.setStatus(ItemStatus.CLAIMED);
        lostItemRepository.save(item);

        return ClaimResponseDTO.from(claimRepository.save(claim));
    }

    @Override
    public ClaimResponseDTO reject(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Reclamo no encontrado."));

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new InvalidClaimStateException("Solo se puede rechazar un reclamo pendiente.");
        }

        claim.setStatus(ClaimStatus.REJECTED);
        return ClaimResponseDTO.from(claimRepository.save(claim));
    }

    @Override
    public void deleteClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Reclamo no encontrado."));
        claim.setActive(false);
        claimRepository.save(claim);
    }
}