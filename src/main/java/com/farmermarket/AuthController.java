package com.farmermarket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import java.util.Map;
import static org.springframework.http.HttpStatus.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@Size(min=8,max=72) String password,
   @NotNull Role role,String phone,String location) {}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password) {}
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final CurrentUser current;
 public AuthController(UserRepository users,PasswordEncoder encoder,JwtService jwt,CurrentUser current){this.users=users;this.encoder=encoder;this.jwt=jwt;this.current=current;}
 @PostMapping("/register") @ResponseStatus(CREATED)
 public Map<String,Object> register(@Valid @RequestBody RegisterRequest r){
  if(r.role()==Role.ADMIN) throw new ResponseStatusException(FORBIDDEN,"Admin registration is disabled");
  if(users.findByEmailIgnoreCase(r.email()).isPresent()) throw new ResponseStatusException(CONFLICT,"Email already registered");
  AppUser u=new AppUser(); u.name=r.name().trim();u.email=r.email().trim().toLowerCase();u.password=encoder.encode(r.password());u.role=r.role();u.phone=r.phone();u.location=r.location();
  users.save(u); return Map.of("token",jwt.generate(u),"user",safe(u));
 }
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody LoginRequest r){
  AppUser u=users.findByEmailIgnoreCase(r.email()).orElseThrow(()->new ResponseStatusException(UNAUTHORIZED,"Invalid email or password"));
  if(!encoder.matches(r.password(),u.password)) throw new ResponseStatusException(UNAUTHORIZED,"Invalid email or password");
  return Map.of("token",jwt.generate(u),"user",safe(u));
 }
 @GetMapping("/me") public Map<String,Object> me(Authentication a){return safe(current.get(a));}
 static Map<String,Object> safe(AppUser u){return Map.of("id",u.id,"name",u.name,"email",u.email,"role",u.role.name(),"phone",u.phone==null?"":u.phone,"location",u.location==null?"":u.location);}
}
