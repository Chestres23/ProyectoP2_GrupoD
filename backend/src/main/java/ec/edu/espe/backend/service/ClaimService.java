package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.ClaimRequestDTO;
import ec.edu.espe.backend.dto.ClaimResponseDTO;

import java.util.List;

public interface ClaimService {
    ClaimResponseDTO create(ClaimRequestDTO dto, Long userId);
    List<ClaimResponseDTO> findAll();
    void approve(Long id);
    void reject(Long id);
}
