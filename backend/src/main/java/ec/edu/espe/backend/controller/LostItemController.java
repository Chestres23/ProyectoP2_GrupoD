package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.dto.LostItemRequestDTO;
import ec.edu.espe.backend.dto.LostItemResponseDTO;
import ec.edu.espe.backend.service.LostItemService;
import ec.edu.espe.backend.service.impl.LostItemAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
public class LostItemController {

    @Autowired private LostItemService service;
    @Autowired private LostItemAuditService auditService;

    // ── CRUD base ──────────────────────────────────────────────────────────

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

    // ── Admin: editar y eliminar ───────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<LostItemResponseDTO> updateItem(@PathVariable Long id,
                                                          @RequestBody LostItemRequestDTO request) {
        return ResponseEntity.ok(service.updateItem(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // ── Imágenes ──────────────────────────────────────────────────────────

    /** Sube una imagen local al objeto (cualquier usuario autenticado dueño del objeto) */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        service.uploadImage(id, file);
        return ResponseEntity.noContent().build();
    }

    /** Sirve la imagen almacenada en MySQL como bytes */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] data = service.getImageBytes(id);
        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = service.getImageContentType(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "image/jpeg")
                .body(data);
    }

    // ── Auditoría reactiva (backpressure + resiliencia) ─────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/auditoria")
    public ResponseEntity<String> ejecutarAuditoria() {
        auditService.ejecutarAuditoria();
        return ResponseEntity.ok("Auditoría iniciada. Revisar logs del servidor.");
    }

}