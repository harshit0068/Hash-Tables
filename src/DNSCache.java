import java.util.HashMap;

public class DNSCache {

    // Internal class to represent a DNS Entry with expiration [cite: 483]
    static class DNSEntry {
        String ipAddress;
        long expiryTime;

        DNSEntry(String ipAddress, int ttlInSeconds) {
            this.ipAddress = ipAddress;
            // Current time + TTL converted to milliseconds
            this.expiryTime = System.currentTimeMillis() + (ttlInSeconds * 1000L);
        }
    }

    // HashMap to store Domain -> DNSEntry [cite: 484]
    private HashMap<String, DNSEntry> cache = new HashMap<>();

    // Tracking statistics [cite: 486]
    private int hits = 0;
    private int misses = 0;
    private long totalLookupTimeNs = 0;

    // Resolves the domain to an IP address
    public String resolve(String domain) {
        long startTime = System.nanoTime(); // Starting the timer!
        String resultIp;
        String status;

        if (!cache.containsKey(domain)) {
            misses++;
            status = "Cache MISS -> Query upstream"; // [cite: 492]
            resultIp = queryUpstream(domain);
            cache.put(domain, new DNSEntry(resultIp, 3)); // Storing with a 3-second TTL
        } else {
            DNSEntry entry = cache.get(domain);
            // Check if the current time has passed the expiry time [cite: 471]
            if (System.currentTimeMillis() > entry.expiryTime) {
                misses++;
                status = "Cache EXPIRED -> Query upstream"; // [cite: 495]
                cache.remove(domain);
                resultIp = queryUpstream(domain);
                cache.put(domain, new DNSEntry(resultIp, 3));
            } else {
                hits++;
                status = "Cache HIT"; // [cite: 492]
                resultIp = entry.ipAddress;
            }
        }

        long durationNs = System.nanoTime() - startTime;
        totalLookupTimeNs += durationNs;
        double durationMs = durationNs / 1_000_000.0;

        return status + " -> " + resultIp + " (retrieved in " + String.format("%.2f", durationMs) + "ms)"; // [cite: 494]
    }

    // Simulates a slow network request to a real DNS server
    private String queryUpstream(String domain) {
        try {
            Thread.sleep(100); // Simulate 100ms delay
        } catch (InterruptedException e) {}

        // Return a fake IP address that changes slightly to simulate updates
        return "172.217.14." + (206 + (int)(Math.random() * 5));
    }

    // Calculates and returns cache statistics [cite: 474]
    public String getCacheStats() {
        int totalRequests = hits + misses;
        double hitRate = totalRequests == 0 ? 0 : ((double) hits / totalRequests) * 100;
        double avgTimeMs = totalRequests == 0 ? 0 : (totalLookupTimeNs / 1_000_000.0) / totalRequests;

        return String.format("Hit Rate: %.1f%%, Avg Lookup Time: %.2fms", hitRate, avgTimeMs); // [cite: 495]
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache();

        // Simulating the Sample Input/Output from the PDF
        System.out.println("resolve(\"google.com\") -> " + dns.resolve("google.com")); // First time: MISS
        System.out.println("resolve(\"google.com\") -> " + dns.resolve("google.com")); // Immediately after: HIT

        System.out.println("\n... waiting 4 seconds for TTL to expire ...\n");
        Thread.sleep(4000);

        System.out.println("resolve(\"google.com\") -> " + dns.resolve("google.com")); // After wait: EXPIRED

        System.out.println("\ngetCacheStats() -> " + dns.getCacheStats());
    }
}