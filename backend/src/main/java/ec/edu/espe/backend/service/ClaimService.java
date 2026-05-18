package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;

import java.util.List;

public interface ClaimService {
    ClaimResponseDTO createClaim(ClaimRequestDTO request);

    List<ClaimResponseDTO> findAll();

    ClaimResponseDTO approve(Long id);

    ClaimResponseDTO reject(Long id);

    void deleteClaim(Long id);
}