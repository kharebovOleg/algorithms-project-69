import hexlet.code.SearchEngine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {

    @Test
    public void testAllContains() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";
        var doc3 = "I'm your shooter.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot");

        assertEquals(List.of("doc1", "doc2", "doc3"), result);
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


}
