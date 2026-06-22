package ec.edu.espe.backend.reactive.model;

import java.time.LocalDateTime;

/**
 * DTO para estadísticas calculadas de forma asíncrona (no bloqueante).
 * Demuestra el uso de Mono para cómputo asíncrono similar al laboratorio.
 */
public class ReactiveStatsDTO {

    private long totalItems;
    private long totalClaims;
    private long pendingClaims;
    private long approvedClaims;
    private long rejectedClaims;
    private long deliveredItems;
    private double approvalRate;
    private LocalDateTime timestamp;

    // Getters y Setters
    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public long getTotalClaims() { return totalClaims; }
    public void setTotalClaims(long totalClaims) { this.totalClaims = totalClaims; }

    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }

    public long getApprovedClaims() { return approvedClaims; }
    public void setApprovedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; }

    public long getRejectedClaims() { return rejectedClaims; }
    public void setRejectedClaims(long rejectedClaims) { this.rejectedClaims = rejectedClaims; }

    public long getDeliveredItems() { return deliveredItems; }
    public void setDeliveredItems(long deliveredItems) { this.deliveredItems = deliveredItems; }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
