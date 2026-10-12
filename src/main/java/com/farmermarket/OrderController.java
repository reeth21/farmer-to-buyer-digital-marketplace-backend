package com.farmermarket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.springframework.http.HttpStatus.*;
@RestController @RequestMapping("/api/orders")
public class OrderController {
 public record OrderRequest(@Positive double quantity) {}
 private final OrderRepository orders;private final ProduceRepository products;private final CurrentUser current;
 public OrderController(OrderRepository orders,ProduceRepository products,CurrentUser current){this.orders=orders;this.products=products;this.current=current;}
 @PostMapping("/produce/{id}") @ResponseStatus(CREATED)
 public Map<String,Object> place(@PathVariable Long id,@Valid @RequestBody OrderRequest r,Authentication a){
  AppUser buyer=current.get(a);current.require(buyer,Role.BUYER);
  Produce p=products.findById(id).orElseThrow(()->new ResponseStatusException(NOT_FOUND,"Produce not found"));
  if(!p.available)throw new ResponseStatusException(CONFLICT,"Listing unavailable");
  if(p.farmer.id.equals(buyer.id))throw new ResponseStatusException(BAD_REQUEST,"Cannot order your own produce");
  if(r.quantity()>p.quantity)throw new ResponseStatusException(BAD_REQUEST,"Requested quantity exceeds available stock");
  MarketOrder o=new MarketOrder();o.produce=p;o.buyer=buyer;o.quantity=r.quantity();o.totalPrice=p.price*r.quantity();
  p.quantity-=r.quantity();if(p.quantity<=0)p.available=false;products.save(p);return view(orders.save(o));
 }
 @GetMapping("/mine") public List<Map<String,Object>> mine(Authentication a){AppUser u=current.get(a);if(u.role==Role.BUYER)return orders.findByBuyerIdOrderByCreatedAtDesc(u.id).stream().map(OrderController::view).toList();current.require(u,Role.FARMER);return orders.findByProduceFarmerIdOrderByCreatedAtDesc(u.id).stream().map(OrderController::view).toList();}
 @PatchMapping("/{id}/status") public Map<String,Object> status(@PathVariable Long id,@RequestParam String value,Authentication a){
  AppUser u=current.get(a);MarketOrder o=orders.findById(id).orElseThrow(()->new ResponseStatusException(NOT_FOUND,"Order not found"));
  if(u.role==Role.BUYER){if(!o.buyer.id.equals(u.id)||!value.equals("CANCELLED")||!o.status.equals("PENDING"))throw new ResponseStatusException(FORBIDDEN,"Cannot change this order");}
  else {current.require(u,Role.FARMER);if(!o.produce.farmer.id.equals(u.id))throw new ResponseStatusException(FORBIDDEN,"Not your order");if(!List.of("CONFIRMED","READY","COMPLETED","CANCELLED").contains(value))throw new ResponseStatusException(BAD_REQUEST,"Invalid status");}
  if(value.equals("CANCELLED")&&o.status.equals("PENDING")){Produce p=o.produce;p.quantity+=o.quantity;p.available=true;products.save(p);}
  o.status=value;return view(orders.save(o));
 }
 static Map<String,Object> view(MarketOrder o){Map<String,Object> v=new LinkedHashMap<>();v.put("id",o.id);v.put("produceId",o.produce.id);v.put("produceName",o.produce.name);v.put("buyerId",o.buyer.id);v.put("buyerName",o.buyer.name);v.put("farmerId",o.produce.farmer.id);v.put("farmerName",o.produce.farmer.name);v.put("quantity",o.quantity);v.put("unit",o.produce.unit);v.put("totalPrice",o.totalPrice);v.put("status",o.status);v.put("createdAt",o.createdAt.toString());return v;}
}

