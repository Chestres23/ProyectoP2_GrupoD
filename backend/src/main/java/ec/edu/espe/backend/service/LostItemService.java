package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LostItemService {
    LostItemResponseDTO createItem(LostItemRequestDTO request);
    List<LostItemResponseDTO> getAllActiveItems();
    LostItemResponseDTO getItemById(Long id);
    LostItemResponseDTO claimItem(Long id);
    LostItemResponseDTO deliverItem(Long id);
    LostItemResponseDTO updateItem(Long id, LostItemRequestDTO request);
    void deleteItem(Long id);
    void uploadImage(Long id, MultipartFile file) throws IOException;
    byte[] getImageBytes(Long id);
    String getImageContentType(Long id);
}