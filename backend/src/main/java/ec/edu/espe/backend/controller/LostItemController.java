package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class LostItemController {

    @Autowired private LostItemService service;

    @PostMapping
    public ResponseEntity<LostItemResponseDTO> createItem(@RequestBody LostItemRequestDTO request) {
        return ResponseEntity.ok(service.createItem(request));
    }

    @GetMapping
    public ResponseEntity<List<LostItemResponseDTO>> getAllItems() {
        return ResponseEntity.ok(service.getAllActiveItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponseDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<LostItemResponseDTO> claimItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.claimItem(id));
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<LostItemResponseDTO> deliverItem(@PathVariable Long id) {
        return ResponseEntity.ok(service.deliverItem(id));
    }
}