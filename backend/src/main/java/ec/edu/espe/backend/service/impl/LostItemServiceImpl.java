package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.exception.InvalidItemStateException;
import ec.edu.espe.backend.exception.ItemNotFoundException;
import ec.edu.espe.backend.exception.UnauthorizedOperationException;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.security.UserPrincipal;
import ec.edu.espe.backend.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostItemServiceImpl implements LostItemService {

    @Autowired private LostItemRepository itemRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public LostItemResponseDTO createItem(LostItemRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LostItem item = new LostItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocationFound(request.getLocationFound());
        item.setDateFound(request.getDateFound());
        item.setImageUrl(request.getImageUrl());
        item.setUser(user);

        return mapToDTO(itemRepository.save(item));
    }

    @Override
    public List<LostItemResponseDTO> getAllActiveItems() {
        return itemRepository.findByActiveTrue().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public LostItemResponseDTO getItemById(Long id) {
        return mapToDTO(getActiveItem(id));
    }

    @Override
    public LostItemResponseDTO claimItem(Long id) {
        LostItem item = getActiveItem(id);
        if (item.getStatus() != ItemStatus.FOUND) {
            throw new InvalidItemStateException("El objeto ya fue reclamado o entregado.");
        }
        item.setStatus(ItemStatus.CLAIMED);
        return mapToDTO(itemRepository.save(item));
    }

    @Override
    public LostItemResponseDTO deliverItem(Long id) {
        LostItem item = getActiveItem(id);
        if (item.getStatus() != ItemStatus.CLAIMED) {
            throw new InvalidItemStateException("El objeto debe ser reclamado antes de entregarse.");
        }
        item.setStatus(ItemStatus.DELIVERED);
        item.setActive(false);
        return mapToDTO(itemRepository.save(item));
    }

    @Override
    public LostItemResponseDTO updateItem(Long id, LostItemRequestDTO request) {
        LostItem item = getActiveItem(id);

        // Verificar propiedad: solo el dueño o un ADMIN pueden editar
        User currentUser = getAuthenticatedUser();
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isAdmin && !currentUser.getId().equals(item.getUser().getId())) {
            throw new UnauthorizedOperationException("Solo el creador del objeto o un administrador puede editarlo.");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            item.setCategory(request.getCategory());
        }
        if (request.getLocationFound() != null) {
            item.setLocationFound(request.getLocationFound());
        }
        if (request.getDateFound() != null) {
            item.setDateFound(request.getDateFound());
        }
        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        return mapToDTO(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long id) {
        LostItem item = getActiveItem(id);
        item.setActive(false);
        itemRepository.save(item);
    }

    @Override
    public void uploadImage(Long id, MultipartFile file) throws IOException {
        LostItem item = getActiveItem(id);
        item.setImageData(file.getBytes());
        item.setImageType(file.getContentType());
        itemRepository.save(item);
    }

    @Override
    public byte[] getImageBytes(Long id) {
        return itemRepository.findById(id)
                .map(LostItem::getImageData)
                .orElseThrow(() -> new ItemNotFoundException("Objeto no encontrado."));
    }

    @Override
    public String getImageContentType(Long id) {
        return itemRepository.findById(id)
                .map(LostItem::getImageType)
                .orElse("image/jpeg");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private LostItem getActiveItem(Long id) {
        return itemRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ItemNotFoundException("Objeto no encontrado o inactivo."));
    }

    /** Obtiene el usuario autenticado desde el JWT (SecurityContextHolder) */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getUser();
    }

    private LostItemResponseDTO mapToDTO(LostItem item) {
        LostItemResponseDTO dto = new LostItemResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setLocationFound(item.getLocationFound());
        dto.setDateFound(item.getDateFound());
        dto.setStatus(item.getStatus());
        dto.setImageUrl(item.getImageUrl());
        dto.setHasImage(item.getImageData() != null && item.getImageData().length > 0);
        dto.setReporterName(item.getUser().getName());
        dto.setReporterId(item.getUser().getId());
        return dto;
    }
}