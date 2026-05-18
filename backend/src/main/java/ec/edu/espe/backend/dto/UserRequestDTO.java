package ec.edu.espe.backend.dto;

import jakarta.validation.constraints.*;

public class UserRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 120)
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Size(max = 20)
    private String phone;

    // Getters y Setters

    public @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 120) String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 120) String name) {
        this.name = name;
    }

    public @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email) {
        this.email = email;
    }

    public @NotBlank(message = "La contraseña es obligatoria") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "La contraseña es obligatoria") String password) {
        this.password = password;
    }

    public @Size(max = 20) String getPhone() {
        return phone;
    }

    public void setPhone(@Size(max = 20) String phone) {
        this.phone = phone;
    }
}