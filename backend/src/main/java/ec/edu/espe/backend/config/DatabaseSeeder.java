package ec.edu.espe.backend.config;

import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.domain.enums.ClaimStatus;
import ec.edu.espe.backend.domain.enums.ItemStatus;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;

@Profile("!test")
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo inserta si la tabla de usuarios está vacía
        if (userRepository.count() == 0) {
            System.out.println("🌱 Ejecutando Seeder: Insertando datos de prueba...");

            // 1. Crear el Usuario Administrador
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setPhone("0999999999");
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);

            // 2. Crear un Usuario Normal de prueba
            User demoUser = new User();
            demoUser.setName("User");
            demoUser.setEmail("user@test.com");
            demoUser.setPassword(passwordEncoder.encode("123"));
            demoUser.setPhone("0988888888");
            demoUser.setRole(User.Role.USER);
            demoUser.setActive(true);
            demoUser.setCreatedAt(LocalDateTime.now());
            demoUser.setUpdatedAt(LocalDateTime.now());

            userRepository.save(demoUser);

            // 3. Crear Objetos Perdidos para probar tus endpoints
            LostItem item1 = new LostItem();
            item1.setName("Calculadora Casio");
            item1.setDescription("Calculadora científica gris con rayones en la tapa");
            item1.setCategory("Electrónicos");
            item1.setLocationFound("Laboratorio de Física");
            item1.setDateFound(LocalDate.now());
            item1.setStatus(ItemStatus.FOUND);
            item1.setActive(true);
            item1.setUser(admin); // Asignado al admin

            LostItem item2 = new LostItem();
            item2.setName("Cuaderno Universitario");
            item2.setDescription("Cuaderno de espiral, tapa azul, apuntes de Cálculo");
            item2.setCategory("Material de Estudio");
            item2.setLocationFound("Biblioteca Central");
            item2.setDateFound(LocalDate.now().minusDays(2));
            item2.setStatus(ItemStatus.FOUND);
            item2.setActive(true);
            item2.setUser(demoUser); // Asignado al usuario normal

            lostItemRepository.save(item1);
            lostItemRepository.save(item2);

            Claim claim = new Claim();
            claim.setUser(admin);
            claim.setItem(item1);
            claim.setObservation("Reclamo de prueba para validar el módulo de claims.");
            claim.setStatus(ClaimStatus.PENDING);
            claimRepository.save(claim);

            System.out.println("✅ Seeder completado: Usuarios y Objetos Perdidos creados exitosamente.");
        }
    }
}