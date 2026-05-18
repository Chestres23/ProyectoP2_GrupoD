package ec.edu.espe.backend.repository;

import ec.edu.espe.backend.domain.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    List<LostItem> findByActiveTrue();
    Optional<LostItem> findByIdAndActiveTrue(Long id);
}