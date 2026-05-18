package ec.edu.espe.backend.service;

import ec.edu.espe.backend.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // Registrar nuevo usuario
    User save(User user);

    // Buscar usuario por ID
    Optional<User> findById(Long id);

    // Listar todos los usuarios
    List<User> findAll();

    // Buscar por email
    Optional<User> findByEmail(String email);

    // Desactivar usuario
    void deactivate(Long id);
}