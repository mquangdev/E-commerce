package e_commerce.auth_service;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

  public static void main(String[] args) {
    // Fix for PostgreSQL JDBC Asia/Saigon timezone issue
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
