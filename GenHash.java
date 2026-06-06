import org.springframework.security.crypto.bcrypt.BCrypt;

public class GenHash {
    public static void main(String[] args) {
        System.out.println("admin123 -> " + BCrypt.hashpw("admin123", BCrypt.gensalt()));
        System.out.println("123456  -> " + BCrypt.hashpw("123456", BCrypt.gensalt()));
    }
}
