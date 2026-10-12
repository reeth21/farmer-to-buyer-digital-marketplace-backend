package com.farmermarket;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="produce")
public class Produce {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false,length=2000) public String description;
 @Column(nullable=false) public String category;
 @Column(nullable=false) public double quantity;
 @Column(nullable=false) public String unit;
 @Column(nullable=false) public double price;
 @Column(nullable=false) public String location;
 public String quality;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) public AppUser farmer;
 public boolean available=true;
 public Instant createdAt=Instant.now();
 public Produce() {}
}
