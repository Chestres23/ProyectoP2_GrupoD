package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.Claim;
import ec.edu.espe.backend.domain.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByItemIdAndUser_IdAndStatus(Long itemId, Long userId, ClaimStatus status);
    List<Claim> findByStatus(ClaimStatus status);
}
