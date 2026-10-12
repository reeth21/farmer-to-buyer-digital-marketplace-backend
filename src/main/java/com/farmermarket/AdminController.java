package com.farmermarket;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final UserRepository users;private final ProduceRepository produce;private final OrderRepository orders;
 public AdminController(UserRepository users,ProduceRepository produce,OrderRepository orders){this.users=users;this.produce=produce;this.orders=orders;}
 @GetMapping("/summary") public Map<String,Object> summary(){return Map.of("users",users.count(),"farmers",users.findAll().stream().filter(u->u.role==Role.FARMER).count(),"buyers",users.findAll().stream().filter(u->u.role==Role.BUYER).count(),"produceListings",produce.count(),"orders",orders.count());}
}
