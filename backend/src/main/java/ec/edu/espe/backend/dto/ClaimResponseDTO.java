package ec.edu.espe.backend.dto;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimResponseDTO {
    private Long id;
    private LocalDateTime claimDate;
    private String observation;
    private ClaimStatus status;
    private Long userId;
    private Long itemId;

    public static ClaimResponseDTO from(Claim c){
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.id = c.getId();
        dto.claimDate = c.getClaimDate();
        dto.observation = c.getObservation();
        dto.status = c.getStatus();
        dto.userId = c.getUser() != null ? c.getUser().getId() : null;
        dto.itemId = c.getItemId();
        return dto;
    }

    public Long getId() { return id; }
    public LocalDateTime getClaimDate() { return claimDate; }
    public String getObservation() { return observation; }
    public ClaimStatus getStatus() { return status; }
    public Long getUserId() { return userId; }
    public Long getItemId() { return itemId; }
}
