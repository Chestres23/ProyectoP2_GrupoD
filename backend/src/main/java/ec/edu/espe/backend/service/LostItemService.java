package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import java.util.List;

public interface LostItemService {
    LostItemResponseDTO createItem(LostItemRequestDTO request);
    List<LostItemResponseDTO> getAllActiveItems();
    LostItemResponseDTO getItemById(Long id);
    LostItemResponseDTO claimItem(Long id);
    LostItemResponseDTO deliverItem(Long id);
}