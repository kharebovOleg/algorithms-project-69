package hexlet.code;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> docs, String word) {

        Map<String, Long> resultMap = new HashMap<>();

        for (Map<String, String> map : docs) {
            String line = map.get("text");
            if (line.contains(word)) {
                String key = map.get("id");
                String[] lines = line.split(" ");
                long value = Arrays.stream(lines)
                        .filter(s -> s.matches("^" + word + "(\\p{Punct})*$"))
                        .count();
                if (value > 0) resultMap.put(key, value);
            }
        }

        List<String> result = resultMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Collections.reverse(result);

        return result;
    }
}
