import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Admin@123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // 验证现有哈希
        String existingHash = "$2a$10$eP5IXuH8Z3zz7KqO8fqZvON/IbRzLYJmVHFv.Yv1QZ5YGCqLqzDRK";
        System.out.println("\n验证现有哈希:");
        System.out.println("Admin@123 matches: " + encoder.matches("Admin@123", existingHash));
        System.out.println("admin123 matches: " + encoder.matches("admin123", existingHash));
    }
}
