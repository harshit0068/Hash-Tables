import java.util.HashMap;
import java.util.Map;

public class APIRateLimiter {

    // Inner class to represent the Token Bucket for each client [cite: 433]
    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;
        long resetWindowMillis;

        TokenBucket(int maxTokens, long resetWindowMillis) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens; // Start with full tokens
            this.resetWindowMillis = resetWindowMillis;
            this.lastRefillTime = System.currentTimeMillis();
        }
    }

    // HashMap mapping a Client ID to their personal Token Bucket [cite: 432]
    private Map<String, TokenBucket> clientBuckets = new HashMap<>();

    // Setting the limit: 1000 requests per hour
    private final int MAX_REQUESTS = 1000;
    private final long WINDOW_MILLIS = 3600 * 1000L; // 1 hour in milliseconds

    // Checks if the request is allowed or denied [cite: 424]
    public String checkRateLimit(String clientId) {
        // If it's a new client, give them a fresh bucket
        clientBuckets.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, WINDOW_MILLIS));
        TokenBucket bucket = clientBuckets.get(clientId);

        long now = System.currentTimeMillis();
        long timePassed = now - bucket.lastRefillTime;

        // Reset counters if an hour has passed [cite: 422]
        if (timePassed >= WINDOW_MILLIS) {
            bucket.tokens = MAX_REQUESTS;
            bucket.lastRefillTime = now;
            timePassed = 0;
        }

        // Allow request if they have tokens [cite: 421]
        if (bucket.tokens > 0) {
            bucket.tokens--;
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            // Deny request if out of tokens, and calculate wait time [cite: 424]
            long retryAfterSeconds = (WINDOW_MILLIS - timePassed) / 1000;
            return "Denied (0 requests remaining, retry after " + Math.max(0, retryAfterSeconds) + "s)";
        }
    }

    // Returns the current status for a client [cite: 441]
    public String getRateLimitStatus(String clientId) {
        if (!clientBuckets.containsKey(clientId)) {
            return "{used: 0, limit: " + MAX_REQUESTS + "}";
        }
        TokenBucket bucket = clientBuckets.get(clientId);
        int used = MAX_REQUESTS - bucket.tokens;
        long resetTimeUnix = (bucket.lastRefillTime + WINDOW_MILLIS) / 1000;

        return "{used: " + used + ", limit: " + MAX_REQUESTS + ", reset: " + resetTimeUnix + "}";
    }

    public static void main(String[] args) {
        APIRateLimiter limiter = new APIRateLimiter();
        String client = "abc123";

        // Simulating the Sample Input from the PDF [cite: 440, 441]
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + limiter.checkRateLimit(client));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + limiter.checkRateLimit(client));

        System.out.println("\n... simulating 998 fast requests ...\n");
        for (int i = 0; i < 998; i++) {
            limiter.checkRateLimit(client);
        }

        // This request should be denied because the bucket is empty [cite: 441]
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + limiter.checkRateLimit(client));

        // Check the final status [cite: 441]
        System.out.println("getRateLimitStatus(\"abc123\") -> " + limiter.getRateLimitStatus(client));
    }
}