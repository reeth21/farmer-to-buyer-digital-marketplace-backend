package com.farmermarket;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
@Component
public class CurrentUser {
 private final UserRepository users;
 public CurrentUser(UserRepository users){this.users=users;}
 public AppUser get(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new ResponseStatusException(UNAUTHORIZED,"User not found"));}
 public void require(AppUser u,Role role){if(u.role!=role && u.role!=Role.ADMIN) throw new ResponseStatusException(FORBIDDEN,"Insufficient role");}
}
