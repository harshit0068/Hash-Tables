import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyticsDashboard {
    // 1. HashMap for total page views (URL -> Total Views) [cite: 399]
    private Map<String, Integer> pageViews = new HashMap<>();

    // 2. HashMap for unique visitors (URL -> Set of User IDs) [cite: 400]
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();

    // 3. HashMap for traffic sources (Source -> Count) [cite: 401]
    private Map<String, Integer> trafficSources = new HashMap<>();

    private int totalEvents = 0;

    // Processes an incoming page view event in O(1) time [cite: 390]
    public void processEvent(String url, String userId, String source) {
        // Update total page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Update unique visitors (HashSet automatically ignores duplicates)
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // Update traffic sources
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);

        totalEvents++;
    }

    // Generates the real-time dashboard [cite: 409]
    public void getDashboard() {
        System.out.println("getDashboard() ->"); //
        System.out.println("Top Pages:"); //

        // Sort pages by views to find the top ones [cite: 402]
        List<Map.Entry<String, Integer>> sortedPages = new ArrayList<>(pageViews.entrySet());
        sortedPages.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Print top pages [cite: 410, 411]
        int rank = 1;
        for (int i = 0; i < Math.min(2, sortedPages.size()); i++) {
            String url = sortedPages.get(i).getKey();
            int views = sortedPages.get(i).getValue();
            int unique = uniqueVisitors.get(url).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)"); //
            rank++;
        }

        System.out.println("\nTraffic Sources:"); //
        List<String> sourceStats = new ArrayList<>();

        // Calculate percentages for traffic sources [cite: 414, 415]
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalEvents;
            sourceStats.add(String.format("%s: %.0f%%", entry.getKey(), percentage));
        }
        System.out.println(String.join(", ", sourceStats)); //
    }

    public static void main(String[] args) {
        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        // Simulating the Sample Input from the PDF [cite: 408]
        // Note: I added a few extra events to make the output percentages match the style of the PDF
        dashboard.processEvent("/article/breaking-news", "user_123", "Google");
        dashboard.processEvent("/article/breaking-news", "user_456", "Facebook");
        dashboard.processEvent("/article/breaking-news", "user_123", "Direct"); // User 123 returns! (Not unique)
        dashboard.processEvent("/sports/championship", "user_789", "Google");
        dashboard.processEvent("/sports/championship", "user_999", "Google");

        // Print the dashboard
        dashboard.getDashboard();
    }
}