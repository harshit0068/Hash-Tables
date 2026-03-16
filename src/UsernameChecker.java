import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class UsernameChecker {
    // Store username -> userId mapping for O(1) lookup [cite: 424, 425]
    private HashMap<String, String> registeredUsers = new HashMap<>();

    // Use separate HashMap for attempt frequency [cite: 426]
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();

    public UsernameChecker() {
        // Pre-populate some taken usernames for testing
        registeredUsers.put("john_doe", "user123");
        registeredUsers.put("admin", "user001");
    }

    // Check if a username exists in O(1) time [cite: 414]
    public boolean checkAvailability(String username) {
        // Track popularity of attempted usernames every time a check is made [cite: 417]
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);

        // Returns true if the username is NOT in the registeredUsers map
        return !registeredUsers.containsKey(username);
    }

    // Suggest similar available usernames if the requested one is taken [cite: 416]
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int counter = 1;

        // For suggestions: append numbers [cite: 427]
        while (suggestions.size() < 3) {
            String candidate = username + counter;
            if (!registeredUsers.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            counter++;
        }
        return suggestions;
    }

    // Returns the most frequently attempted username
    public String getMostAttempted() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (String user : attemptFrequency.keySet()) {
            if (attemptFrequency.get(user) > maxAttempts) {
                maxAttempts = attemptFrequency.get(user);
                mostAttempted = user;
            }
        }
        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    public static void main(String[] args) {
        UsernameChecker system = new UsernameChecker();

        // Simulating the Sample Input/Output from the PDF [cite: 432]
        System.out.println("checkAvailability(\"john_doe\") -> " + system.checkAvailability("john_doe")); // Should be false [cite: 433]
        System.out.println("checkAvailability(\"jane_smith\") -> " + system.checkAvailability("jane_smith")); // Should be true [cite: 434]
        System.out.println("suggestAlternatives(\"john_doe\") -> " + system.suggestAlternatives("john_doe")); // [cite: 435]

        // Simulate thousands of people trying to register as "admin"
        for(int i = 0; i < 10543; i++) {
            system.checkAvailability("admin");
        }
        System.out.println("getMostAttempted() -> " + system.getMostAttempted()); // [cite: 435]
    }
}