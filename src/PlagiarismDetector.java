import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlagiarismDetector {

    // Maps an n-gram to a Set of Document IDs that contain it
    private HashMap<String, Set<String>> ngramDatabase = new HashMap<>();

    // Helper method to break text into n-grams [cite: 360]
    private List<String> extractNGrams(String text, int n) {
        List<String> ngrams = new ArrayList<>();
        // Normalize text: lower case and split by whitespace
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                ngram.append(words[i + j]).append(j < n - 1 ? " " : "");
            }
            ngrams.add(ngram.toString());
        }
        return ngrams;
    }

    // Add a document to the database
    public void indexDocument(String docId, String text, int n) {
        List<String> ngrams = extractNGrams(text, n);

        for (String ngram : ngrams) {
            // If the n-gram isn't in the database yet, add it with an empty Set
            ngramDatabase.putIfAbsent(ngram, new HashSet<>());
            // Add this docId to the Set for this n-gram
            ngramDatabase.get(ngram).add(docId);
        }
    }

    // Analyze a new document against the database
    public void analyzeDocument(String newDocId, String text, int n) {
        List<String> newNGrams = extractNGrams(text, n);
        System.out.println("analyzeDocument(\"" + newDocId + "\")");
        System.out.println(" -> Extracted " + newNGrams.size() + " n-grams"); // [cite: 379]

        // Track how many matches we find with each existing document
        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String ngram : newNGrams) {
            if (ngramDatabase.containsKey(ngram)) {
                // For every document that shares this n-gram, increment its match count
                for (String matchedDocId : ngramDatabase.get(ngram)) {
                    matchCounts.put(matchedDocId, matchCounts.getOrDefault(matchedDocId, 0) + 1);
                }
            }
        }

        // Calculate similarities and print results [cite: 361]
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String existingDocId = entry.getKey();
            int matches = entry.getValue();

            // Calculate similarity percentage (matches / total n-grams in the new doc)
            double similarity = ((double) matches / newNGrams.size()) * 100;

            String status = similarity > 50.0 ? "(PLAGIARISM DETECTED)" : "(suspicious)"; // [cite: 381, 383]

            System.out.println(" -> Found " + matches + " matching n-grams with \"" + existingDocId + "\""); // [cite: 380, 382]
            System.out.printf(" -> Similarity: %.1f%% %s\n", similarity, status); // [cite: 381, 383]
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();
        int n = 3; // Using 3-grams for this example

        // Existing documents in the university database
        String essay089 = "The quick brown fox jumps over the lazy dog in the park.";
        String essay092 = "Data structures and algorithms are essential for software engineering interviews.";

        detector.indexDocument("essay_089.txt", essay089, n);
        detector.indexDocument("essay_092.txt", essay092, n);

        // New student submission (Notice how it is heavily copied from essay092)
        String essay123 = "Data structures and algorithms are essential for software engineering and computer science.";

        detector.analyzeDocument("essay_123.txt", essay123, n);
    }
}