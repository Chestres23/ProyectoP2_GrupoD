package ec.edu.espe.backend.dto;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimResponseDTO {

    private Long id;
    private LocalDateTime claimDate;
    private String observation;
    private ClaimStatus status;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long itemId;
    private String itemName;
    private String itemStatus;
    private String itemCategory;
    private String itemLocation;

    public static ClaimResponseDTO from(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setId(claim.getId());
        dto.setClaimDate(claim.getClaimDate());
        dto.setObservation(claim.getObservation());
        dto.setStatus(claim.getStatus());
        dto.setUserId(claim.getUser().getId());
        dto.setUserName(claim.getUser().getName());
        dto.setUserEmail(claim.getUser().getEmail());
        dto.setItemId(claim.getItem().getId());
        dto.setItemName(claim.getItem().getName());
        dto.setItemStatus(String.valueOf(claim.getItem().getStatus()));
        dto.setItemCategory(claim.getItem().getCategory());
        dto.setItemLocation(claim.getItem().getLocationFound());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }
}