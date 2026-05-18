package ec.edu.espe.backend.service.impl;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.exception.InvalidItemStateException;
import ec.edu.espe.backend.exception.ItemNotFoundException;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import ec.edu.espe.backend.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        item.setActive(false); // Soft delete al entregar
        return mapToDTO(itemRepository.save(item));
    }

    private LostItem getActiveItem(Long id) {
        return itemRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ItemNotFoundException("Objeto no encontrado o inactivo."));
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
        dto.setReporterName(item.getUser().getName()); // Ajusta según el campo de tu entidad User
        return dto;
    }
}