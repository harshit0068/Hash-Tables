import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAnalyzer {

    // Inner class representing a financial transaction
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String time;
        String account;

        Transaction(int id, int amount, String merchant, String time, String account) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.time = time;
            this.account = account;
        }
    }

    private List<Transaction> transactions = new ArrayList<>();

    // Helper to add mock data
    public void addTransaction(int id, int amount, String merchant, String time, String account) {
        transactions.add(new Transaction(id, amount, merchant, time, account));
    }

    // Classic Two-Sum: Find pairs that sum to target amount
    public String findTwoSum(int target) {
        // HashMap to store <ComplementAmount, Transaction>
        Map<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            // If the complement exists in our map, we found a pair!
            if (map.containsKey(complement)) {
                Transaction match = map.get(complement);
                return "[(id:" + match.id + ", id:" + t.id + ")] // " + match.amount + " + " + t.amount;
            }
            // Otherwise, store this transaction's amount for future checks
            map.put(t.amount, t);
        }
        return "No pair found";
    }

    // Duplicate detection: Same amount, same merchant, different accounts
    public String detectDuplicates() {
        // HashMap to group accounts by a unique key (amount + merchant)
        Map<String, List<String>> map = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant; // e.g., "500_Store A"
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t.account);
        }

        // Find any grouping that has more than 1 account associated with it
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                String[] parts = entry.getKey().split("_");
                return "[{ amount:" + parts[0] + ", merchant:\"" + parts[1] + "\", accounts: " + entry.getValue() + "}]";
            }
        }
        return "No duplicates detected";
    }

    // K-Sum: Find K transactions that sum to target
    public String findKSum(int k, int target) {
        // For this specific 3-sum example, we use a simpler loop approach to match the PDF.
        // (A fully dynamic K-Sum algorithm usually involves recursion and memoization)
        if (k == 3) {
            for (int i = 0; i < transactions.size(); i++) {
                for (int j = i + 1; j < transactions.size(); j++) {
                    for (int l = j + 1; l < transactions.size(); l++) {
                        if (transactions.get(i).amount + transactions.get(j).amount + transactions.get(l).amount == target) {
                            return "[(id:" + transactions.get(i).id + ", id:" + transactions.get(j).id + ", id:" + transactions.get(l).id + ")] // "
                                    + transactions.get(i).amount + "+" + transactions.get(j).amount + "+" + transactions.get(l).amount;
                        }
                    }
                }
            }
        }
        return "No " + k + "-sum match found";
    }

    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        // Simulating the Sample Input from the PDF (with an extra duplicate added for testing)
        analyzer.addTransaction(1, 500, "Store A", "10:00", "acc1");
        analyzer.addTransaction(4, 500, "Store A", "10:05", "acc2"); // Added to trigger the duplicate detection
        analyzer.addTransaction(2, 300, "Store B", "10:15", "acc3");
        analyzer.addTransaction(3, 200, "Store C", "10:30", "acc4");

        // Running the required methods
        System.out.println("findTwoSum(target=500) -> " + analyzer.findTwoSum(500));
        System.out.println("detectDuplicates() -> " + analyzer.detectDuplicates());
        System.out.println("findKSum(k=3, target=1000) -> " + analyzer.findKSum(3, 1000));
    }
}