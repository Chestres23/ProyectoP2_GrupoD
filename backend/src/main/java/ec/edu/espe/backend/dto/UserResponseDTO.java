package ec.edu.espe.backend.dto;

import ec.edu.espe.backend.domain.User;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Boolean active;

    // Factory method adaptado a R2DBC (User ya no tiene enum directo)
    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.phone = user.getPhone();
        dto.role = user.getRoleValue();
        dto.active = user.getActive();
        return dto;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public Boolean getActive() { return active; }
}