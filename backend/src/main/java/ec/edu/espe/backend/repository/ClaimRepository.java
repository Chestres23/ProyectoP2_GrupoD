package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    boolean existsByUser_IdAndItem_Id(Long userId, Long itemId);

    List<Claim> findAllByOrderByClaimDateDesc();

    List<Claim> findAllByActiveTrueOrderByClaimDateDesc();

    Optional<Claim> findById(Long id);
}