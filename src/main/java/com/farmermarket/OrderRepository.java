package com.farmermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<MarketOrder,Long> {
 List<MarketOrder> findByBuyerIdOrderByCreatedAtDesc(Long id);
 List<MarketOrder> findByProduceFarmerIdOrderByCreatedAtDesc(Long id);
}
