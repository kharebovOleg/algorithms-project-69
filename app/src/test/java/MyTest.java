import hexlet.code.SearchEngine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {

    @Test
    public void testAllContains() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot at me.";
        var doc3 = "I'm your shoot.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot");

        assertEquals(List.of("doc1", "doc2", "doc3").size(), result.size());
    }

    @Test
    public void testNoContains() {
        var doc1 = "I can't straight unless I've had a pint!";
        var doc2 = "Don't that thing at me.";
        var doc3 = "I'm your.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot");

        assertEquals(List.of(), result);
    }

    @Test
    public void testOneContainsWithPunctuation() {
        var doc1 = "I can't shoot straight unless I've had a pint!?&&";
        var doc2 = "I can't shoot straight unless I've had a pint";
        List<Map<String, String>> docs =
                List.of(Map.of("id", "doc1", "text", doc1),
                        Map.of("id", "doc2", "text", doc2));

        List<String> result1 = SearchEngine.search(docs, "pint");
        assertEquals(List.of("doc1", "doc2"), result1);

        List<String> result2 = SearchEngine.search(docs, "pint!");
        assertEquals(List.of("doc1"), result2);
    }

    @Test
    public void testMetricWorks() {
        var doc1 = "I can't shoot shoot straight unless I've had a pint!"; //2
        var doc2 = "Don't shoot shoot! shoot!! that thing at me."; // 3
        var doc3 = "I'm your shooter."; // 0
        var doc4 = "shoot."; // 1
        var doc5 = "shoot, shoot shoot shoot"; // 4

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3),
                Map.of("id", "doc4", "text", doc4),
                Map.of("id", "doc5", "text", doc5)
        );

        List<String> result = SearchEngine.search(docs, "shoot");
        assertEquals(List.of("doc5", "doc2", "doc1", "doc4"), result);

    }

    @Test
    public void testWithFuzzySearch() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";
        var doc3 = "I'm your shooter.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot at me");
        assertEquals(List.of("doc2", "doc1"), result);
    }


}
