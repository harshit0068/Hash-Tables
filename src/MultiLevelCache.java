import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultiLevelCache {

    // Helper class to create an LRU (Least Recently Used) Cache using LinkedHashMap
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        // The 'true' flag enables access-order (instead of insertion-order)
        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        // Automatically removes the oldest/least used entry when capacity is reached
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    // L1 Cache: Fast Memory (Simulated capacity: 2 items for testing)
    private LRUCache<String, String> l1Cache = new LRUCache<>(2);

    // L2 Cache: SSD (Simulated capacity: 5 items for testing)
    private LRUCache<String, String> l2Cache = new LRUCache<>(5);

    // Tracks how many times a video is accessed across the whole system
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Promotion threshold: If accessed more than 1 time, move to L1
    private final int PROMOTION_THRESHOLD = 1;

    // Statistics tracking
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;
    private double totalTimeMs = 0;

    public void getVideo(String videoId) {
        System.out.println("getVideo(\"" + videoId + "\")");
        totalRequests++;
        double requestTime = 0;

        // Increment global access count
        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);

        // 1. Check L1 Cache
        if (l1Cache.containsKey(videoId)) {
            System.out.println(" -> L1 Cache HIT (0.5ms)"); //
            l1Hits++;
            requestTime += 0.5;
        }
        // 2. Check L2 Cache
        else {
            System.out.println(" -> L1 Cache MISS (0.5ms)"); //
            requestTime += 0.5;

            if (l2Cache.containsKey(videoId)) {
                System.out.println(" -> L2 Cache HIT (5.0ms)"); //
                l2Hits++;
                requestTime += 5.0;

                // Promote to L1 if popular enough
                if (count > PROMOTION_THRESHOLD) {
                    l1Cache.put(videoId, l2Cache.get(videoId));
                    System.out.println(" -> Promoted to L1"); //
                }
            }
            // 3. Fallback to L3 Database
            else {
                System.out.println(" -> L2 Cache MISS"); //
                System.out.println(" -> L3 Database HIT (150.0ms)"); //
                l3Hits++;
                requestTime += 150.0;

                // Add to L2 for next time
                l2Cache.put(videoId, "VideoData FilePath");
                System.out.println(" -> Added to L2 (access count: " + count + ")"); //
            }
        }

        totalTimeMs += requestTime;
        System.out.println(" -> Total: " + requestTime + "ms\n"); //
    }

    public void getStatistics() {
        System.out.println("getStatistics() ->"); //
        System.out.printf("L1: Hit Rate %.0f%%, Avg Time: 0.5ms\n", (l1Hits / (double) totalRequests) * 100); //
        System.out.printf("L2: Hit Rate %.0f%%, Avg Time: 5.0ms\n", (l2Hits / (double) totalRequests) * 100); //
        System.out.printf("L3: Hit Rate %.0f%%, Avg Time: 150.0ms\n", (l3Hits / (double) totalRequests) * 100); //
        System.out.printf("Overall: Hit Rate 100%%, Avg Time: %.1fms\n", (totalTimeMs / totalRequests)); //
    }

    public static void main(String[] args) {
        MultiLevelCache system = new MultiLevelCache();

        // Simulating the Sample Input/Output from the PDF

        // Setup: Put video_123 in L2 initially
        system.l2Cache.put("video_123", "VideoData");

        // 1. Fetch video_123 (In L2, will be promoted to L1)
        system.getVideo("video_123");

        // 2. Fetch video_123 again (Now it should be an L1 HIT)
        system.getVideo("video_123");

        // 3. Fetch a brand new video (Will fall back to L3 Database)
        system.getVideo("video_999");

        // 4. Print final stats
        system.getStatistics();
    }
}