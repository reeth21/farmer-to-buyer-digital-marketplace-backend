package com.farmermarket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.springframework.http.HttpStatus.*;
@RestController @RequestMapping("/api/produce")
public class ProduceController {
 public record ProduceRequest(@NotBlank String name,@NotBlank String description,@NotBlank String category,
  @Positive double quantity,@NotBlank String unit,@PositiveOrZero double price,@NotBlank String location,String quality) {}
 private final ProduceRepository products; private final CurrentUser current;
 public ProduceController(ProduceRepository products,CurrentUser current){this.products=products;this.current=current;}
 @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) String q,@RequestParam(required=false) String location){
  String query=q==null?"":q.toLowerCase(),loc=location==null?"":location.toLowerCase();
  return products.findByAvailableTrueOrderByCreatedAtDesc().stream().filter(p->
   (query.isBlank()||p.name.toLowerCase().contains(query)||p.category.toLowerCase().contains(query)||p.description.toLowerCase().contains(query)) &&
   (loc.isBlank()||p.location.toLowerCase().contains(loc))).map(ProduceController::view).toList();
 }
 @GetMapping("/mine") public List<Map<String,Object>> mine(Authentication a){AppUser u=current.get(a);current.require(u,Role.FARMER);return products.findByFarmerIdOrderByCreatedAtDesc(u.id).stream().map(ProduceController::view).toList();}
 @PostMapping public Map<String,Object> create(@Valid @RequestBody ProduceRequest r,Authentication a){
  AppUser u=current.get(a);current.require(u,Role.FARMER);Produce p=new Produce();copy(r,p);p.farmer=u;return view(products.save(p));
 }
 @PutMapping("/{id}") public Map<String,Object> update(@PathVariable Long id,@Valid @RequestBody ProduceRequest r,Authentication a){
  AppUser u=current.get(a);Produce p=owned(id,u);copy(r,p);return view(products.save(p));
 }
 @DeleteMapping("/{id}") @ResponseStatus(NO_CONTENT) public void delete(@PathVariable Long id,Authentication a){AppUser u=current.get(a);products.delete(owned(id,u));}
 private Produce owned(Long id,AppUser u){Produce p=products.findById(id).orElseThrow(()->new ResponseStatusException(NOT_FOUND,"Produce not found"));if(!p.farmer.id.equals(u.id)&&u.role!=Role.ADMIN)throw new ResponseStatusException(FORBIDDEN,"Not your listing");return p;}
 private void copy(ProduceRequest r,Produce p){p.name=r.name().trim();p.description=r.description().trim();p.category=r.category().trim();p.quantity=r.quantity();p.unit=r.unit();p.price=r.price();p.location=r.location().trim();p.quality=r.quality();}
 static Map<String,Object> view(Produce p){Map<String,Object> v=new LinkedHashMap<>();v.put("id",p.id);v.put("name",p.name);v.put("description",p.description);v.put("category",p.category);v.put("quantity",p.quantity);v.put("unit",p.unit);v.put("price",p.price);v.put("location",p.location);v.put("quality",p.quality==null?"":p.quality);v.put("farmerId",p.farmer.id);v.put("farmerName",p.farmer.name);v.put("farmerPhone",p.farmer.phone==null?"":p.farmer.phone);v.put("available",p.available);v.put("createdAt",p.createdAt.toString());return v;}
}

