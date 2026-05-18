package ec.edu.espe.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClaimRequestDTO {

    @NotNull(message = "El itemId es obligatorio")
    private Long itemId;

    @Size(max = 1000)
    private String observation;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}
