import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("harry: " + encoder.encode("harry"));
        System.out.println("charlie: " + encoder.encode("charlie"));
        System.out.println("oliver: " + encoder.encode("oliver"));
        System.out.println("jerald: " + encoder.encode("jerald"));
        System.out.println("charlotte: " + encoder.encode("charlotte"));
        System.out.println("mia: " + encoder.encode("mia"));
        System.out.println("tod: " + encoder.encode("tod"));
        System.out.println("sophia: " + encoder.encode("sophia"));
        System.out.println("sam: " + encoder.encode("sam"));
        System.out.println("william: " + encoder.encode("william"));
        System.out.println("sandy: " + encoder.encode("sandy"));
        System.out.println("david: " + encoder.encode("david"));
        System.out.println("richard: " + encoder.encode("richard"));
        System.out.println("emma: " + encoder.encode("emma"));
        System.out.println("tom: " + encoder.encode("tom"));
    }
}
