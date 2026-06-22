package ec.edu.espe.backend.config;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.LostItem;
import ec.edu.espe.backend.domain.User;
import ec.edu.espe.backend.repository.ClaimRepository;
import ec.edu.espe.backend.repository.LostItemRepository;
import ec.edu.espe.backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeder reactivo que inserta datos de prueba al iniciar la app.
 * Usa cadenas reactivas con flatMap/then en vez de llamadas bloqueantes.
 * Se ejecuta solo si la tabla users está vacía y fuera del perfil "test".
 */
@Profile("!test")
@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final ClaimRepository claimRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, LostItemRepository lostItemRepository,
                          ClaimRepository claimRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.claimRepository = claimRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        userRepository.count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.empty();
                    }
                    System.out.println("🌱 Ejecutando Seeder: Insertando datos de prueba...");
                    return seedData();
                })
                .subscribe(
                        unused -> {},
                        error -> System.err.println("❌ Error en seeder: " + error.getMessage()),
                        () -> System.out.println("✅ Seeder completado.")
                );
    }

    private Mono<Void> seedData() {
        LocalDateTime now = LocalDateTime.now();

        // Crear admin
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setPhone("0999999999");
        admin.setRole(User.Role.ADMIN);
        admin.setActive(true);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        // Crear usuario normal
        User demoUser = new User();
        demoUser.setName("User");
        demoUser.setEmail("user@test.com");
        demoUser.setPassword(passwordEncoder.encode("123"));
        demoUser.setPhone("0988888888");
        demoUser.setRole(User.Role.USER);
        demoUser.setActive(true);
        demoUser.setCreatedAt(now);
        demoUser.setUpdatedAt(now);

        return userRepository.save(admin)
                .flatMap(savedAdmin -> userRepository.save(demoUser)
                        .flatMap(savedUser -> {
                            // Crear objetos perdidos
                            LostItem item1 = new LostItem();
                            item1.setName("Calculadora Casio");
                            item1.setDescription("Calculadora científica gris con rayones en la tapa");
                            item1.setCategory("Electrónicos");
                            item1.setLocationFound("Laboratorio de Física");
                            item1.setDateFound(LocalDate.now());
                            item1.setStatus("FOUND");
                            item1.setActive(true);
                            item1.setUserId(savedAdmin.getId());
                            item1.setCreatedAt(now);
                            item1.setUpdatedAt(now);

                            LostItem item2 = new LostItem();
                            item2.setName("Cuaderno Universitario");
                            item2.setDescription("Cuaderno de espiral, tapa azul, apuntes de Cálculo");
                            item2.setCategory("Material de Estudio");
                            item2.setLocationFound("Biblioteca Central");
                            item2.setDateFound(LocalDate.now().minusDays(2));
                            item2.setStatus("FOUND");
                            item2.setActive(true);
                            item2.setUserId(savedUser.getId());
                            item2.setCreatedAt(now);
                            item2.setUpdatedAt(now);

                            return lostItemRepository.save(item1)
                                    .flatMap(savedItem1 -> lostItemRepository.save(item2)
                                            .flatMap(savedItem2 -> {
                                                Claim claim = new Claim();
                                                claim.setUserId(savedAdmin.getId());
                                                claim.setItemId(savedItem1.getId());
                                                claim.setObservation("Reclamo de prueba para validar el módulo de claims.");
                                                claim.setStatus("PENDING");
                                                claim.setClaimDate(now);
                                                claim.setCreatedAt(now);
                                                claim.setUpdatedAt(now);
                                                return claimRepository.save(claim);
                                            }));
                        }))
                .then();
    }
}