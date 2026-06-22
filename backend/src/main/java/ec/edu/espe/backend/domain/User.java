package ec.edu.espe.backend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * Entidad de usuario para R2DBC.
 * R2DBC no usa anotaciones JPA; se mapea directamente a la tabla 'users'.
 * El enum Role se almacena como String en la columna 'role'.
 */
@Table("users")
public class User {

    @Id
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;

    // R2DBC no convierte enums automáticamente como JPA, se guarda como String
    private String role;

    private Boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // Enum auxiliar para la lógica de negocio
    public enum Role {
        ADMIN, USER
    }

    public User() {
        this.role = Role.USER.name();
        this.active = true;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Devuelve el rol como enum (conveniencia para la lógica de negocio).
     */
    public Role getRole() {
        return role != null ? Role.valueOf(role) : Role.USER;
    }

    public void setRole(Role role) {
        this.role = role != null ? role.name() : Role.USER.name();
    }

    /**
     * Acceso directo al String almacenado (usado por R2DBC).
     */
    public String getRoleValue() { return role; }
    public void setRoleValue(String role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}