package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Busca un usuario por email exacto.
    Optional<User> findByEmail(String email);

    //Verifica si existe un usuario con el email dado.
    boolean existsByEmail(String email);
}