package com.farmermarket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<Map<String,String>> status(ResponseStatusException e){
  String reason=e.getReason()==null?"Request failed":e.getReason();
  return ResponseEntity.status(e.getStatusCode()).body(Map.of("message",reason));
 }
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException e){
  String message=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Invalid request");
  return ResponseEntity.badRequest().body(Map.of("message",message));
 }
 @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
 public ResponseEntity<Map<String,String>> conflict(Exception e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","A record with these details already exists or violates a database constraint"));}
}
