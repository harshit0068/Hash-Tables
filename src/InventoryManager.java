import java.util.HashMap;
import java.util.LinkedHashMap;

public class InventoryManager {
    // HashMap for instant O(1) stock lookup [cite: 447, 451]
    private HashMap<String, Integer> stock = new HashMap<>();

    // LinkedHashMap to maintain a FIFO waiting list [cite: 453]
    private LinkedHashMap<Integer, String> waitingList = new LinkedHashMap<>();
    private int waitlistCounter = 1;

    public InventoryManager() {
        // Initialize the flash sale with limited stock (100 units) [cite: 439]
        stock.put("IPHONE15_256GB", 100);
    }

    // Check stock availability [cite: 444]
    public String checkStock(String productId) {
        int currentStock = stock.getOrDefault(productId, 0);
        return currentStock + " units available";
    }

    // Process a purchase request in O(1) time [cite: 442]
    public String purchaseItem(String productId, int userId) {
        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock > 0) {
            stock.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        } else {
            // Add to waiting list if stock is 0 [cite: 443]
            waitingList.put(userId, productId);
            return "Added to waiting list, position #" + (waitlistCounter++);
        }
    }

    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        // Simulating the Sample Input/Output from the PDF [cite: 459]
        System.out.println("checkStock(\"IPHONE15_256GB\") -> " + manager.checkStock("IPHONE15_256GB")); // [cite: 460]
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=12345) -> " + manager.purchaseItem("IPHONE15_256GB", 12345)); // [cite: 461]
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=67890) -> " + manager.purchaseItem("IPHONE15_256GB", 67890)); // [cite: 461]

        // Simulate 98 more purchases to completely deplete the stock [cite: 461]
        System.out.println("(after 98 more purchases...)");
        for (int i = 0; i < 98; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        // This user is too late and should be put on the waitlist [cite: 462]
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=99999) -> " + manager.purchaseItem("IPHONE15_256GB", 99999)); // [cite: 462]
    }
}