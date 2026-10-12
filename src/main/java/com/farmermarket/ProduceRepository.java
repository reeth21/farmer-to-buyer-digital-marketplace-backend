package com.farmermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProduceRepository extends JpaRepository<Produce,Long> {
 List<Produce> findByAvailableTrueOrderByCreatedAtDesc();
 List<Produce> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
}
