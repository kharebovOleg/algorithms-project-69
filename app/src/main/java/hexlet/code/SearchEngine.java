package hexlet.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> docs, String word) {

        List<String> result = new ArrayList<>();

        for (Map<String,String> map : docs) {
            String doc = map.get("text");
            if (doc.contains(word)) {
                result.add(map.get("id"));
            }
        }
        return result;
    }
}
