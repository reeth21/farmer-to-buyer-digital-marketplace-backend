package com.farmermarket;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false, unique=true) public String email;
 @Column(nullable=false) public String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role;
 public String phone;
 public String location;
 public Instant createdAt=Instant.now();
 public AppUser() {}
}
