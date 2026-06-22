package ec.edu.espe.backend.reactive.model;

import java.time.LocalDateTime;

/**
 * Evento de reclamo emitido al stream SSE (Server-Sent Events).
 * No es una entidad de BD, es un DTO de evento para el hot stream.
 */
public class ReactiveClaimEvent {

    private String eventId;
    private ClaimEventType type;
    private Long claimId;
    private String itemName;
    private String userName;
    private String status;
    private LocalDateTime timestamp;

    public ReactiveClaimEvent() {}

    public ReactiveClaimEvent(String eventId, ClaimEventType type, Long claimId,
                               String itemName, String userName, String status) {
        this.eventId = eventId;
        this.type = type;
        this.claimId = claimId;
        this.itemName = itemName;
        this.userName = userName;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public ClaimEventType getType() { return type; }
    public void setType(ClaimEventType type) { this.type = type; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
