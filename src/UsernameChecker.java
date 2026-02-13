
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {
    // username -> userId
    private static ConcurrentHashMap<String, Integer> userMap = new ConcurrentHashMap<>();
    // username -> attempt count
    private static ConcurrentHashMap<String, Integer> attemptMap = new ConcurrentHashMap<>();

    // ✅ Method definitions
    public static boolean checkAvailability(String username) {
        attemptMap.merge(username, 1, Integer::sum); // track attempts
        return !userMap.containsKey(username);
    }

    public static void registerUser(String username, int userId) {
        userMap.put(username, userId);
    }

    public static List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add(username + "1");
        suggestions.add(username + "2");
        suggestions.add(username.replace("_", "."));
        return suggestions;
    }

    public static String getMostAttempted() {
        return attemptMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    // ✅ Your main method
    public static void main(String[] args) {
        // Register some taken usernames
        registerUser("john_doe", 1);
        registerUser("admin", 2);

        // Create a list of usernames to test
        List<String> testUsernames = Arrays.asList(
                "john_doe", "jane_smith", "admin", "guest", "john_doe", "admin"
        );

        // Simulate 1000 concurrent checks using threads
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String name = testUsernames.get(i % testUsernames.size()); // cycle through names
            Thread t = new Thread(() -> {
                boolean available = checkAvailability(name);
                System.out.println("checkAvailability(\"" + name + "\") → " + available);
            });
            threads.add(t);
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Show suggestions and most attempted
        System.out.println("suggestAlternatives(\"john_doe\") → " + suggestAlternatives("john_doe"));
        System.out.println("getMostAttempted() → " + getMostAttempted());
    }
}
