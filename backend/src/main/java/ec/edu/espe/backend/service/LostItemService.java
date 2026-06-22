package ec.edu.espe.backend.service;

import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio reactivo de objetos perdidos.
 * FilePart reemplaza a MultipartFile (WebFlux no usa Servlet).
 */
public interface LostItemService {
    Mono<LostItemResponseDTO> createItem(LostItemRequestDTO request);
    Flux<LostItemResponseDTO> getAllActiveItems();
    Mono<LostItemResponseDTO> getItemById(Long id);
    Mono<LostItemResponseDTO> claimItem(Long id);
    Mono<LostItemResponseDTO> deliverItem(Long id);
    Mono<LostItemResponseDTO> updateItem(Long id, LostItemRequestDTO request);
    Mono<Void> deleteItem(Long id);
    Mono<Void> uploadImage(Long id, FilePart filePart);
    Mono<byte[]> getImageBytes(Long id);
    Mono<String> getImageContentType(Long id);
}