import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteSystem {

    // HashMap to store query -> frequency for global stats
    private Map<String, Integer> queryFrequency = new HashMap<>();

    public AutocompleteSystem() {
        // Pre-populate with the Sample Input from the PDF [cite: 467, 468, 469]
        queryFrequency.put("java tutorial", 1234567);
        queryFrequency.put("javascript", 987654);
        queryFrequency.put("java download", 456789);
        queryFrequency.put("java 21 features", 2); // A trending new search [cite: 470]
    }

    // Updates frequencies based on new searches [cite: 449]
    public void updateFrequency(String query) {
        int newCount = queryFrequency.getOrDefault(query, 0) + 1;
        queryFrequency.put(query, newCount);
        System.out.println("updateFrequency(\"" + query + "\") -> Frequency updated to " + newCount); // [cite: 470]
    }

    // Returns top suggestions for any prefix [cite: 448]
    public void search(String prefix) {
        System.out.println("search(\"" + prefix + "\") ->"); // [cite: 466]

        // Find all queries that start with the prefix
        List<Map.Entry<String, Integer>> matches = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : queryFrequency.entrySet()) {
            if (entry.getKey().startsWith(prefix.toLowerCase())) {
                matches.add(entry);
            }
        }

        // Sort the matches by frequency (highest to lowest)
        matches.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Print up to the top 10 suggestions [cite: 448]
        int limit = Math.min(10, matches.size());
        for (int i = 0; i < limit; i++) {
            String fullQuery = matches.get(i).getKey();
            int searches = matches.get(i).getValue();

            // To match the PDF, we show what comes *after* the prefix [cite: 467]
            String suffix = fullQuery.substring(prefix.length());
            System.out.println((i + 1) + ". \"" + suffix + "\" (" + searches + " searches)");
        }
    }

    public static void main(String[] args) {
        AutocompleteSystem autocomplete = new AutocompleteSystem();

        // 1. Search for a prefix [cite: 466]
        autocomplete.search("jav");

        System.out.println();

        // 2. Simulate a user searching for something new, increasing its popularity [cite: 449, 470]
        autocomplete.updateFrequency("java 21 features");
    }
}