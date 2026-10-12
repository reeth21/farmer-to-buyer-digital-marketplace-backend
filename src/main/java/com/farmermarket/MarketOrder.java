package com.farmermarket;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="market_orders")
public class MarketOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) public Produce produce;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) public AppUser buyer;
 @Column(nullable=false) public double quantity;
 @Column(nullable=false) public double totalPrice;
 @Column(nullable=false) public String status="PENDING";
 public Instant createdAt=Instant.now();
 public MarketOrder() {}
}
