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

    }

    @Test
    public void testMetricWorks() {
        var doc1 = "I can't shoot shoot straight unless I've had a pint!"; //3
        var doc2 = "Don't shoot shoot! shoot!! that thing at me."; // 2
        var doc3 = "I'm your shooter.";
        var doc4 = "shoot."; // 4
        var doc5 = "shoot, shoot shoot shoot"; // 1

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
    public void testWithFewWords() {
        var doc1 = "Hello World this is a table"; // 5
        var doc2 = "Don't shoot shoot shoot this thing at me."; //6
        var doc3 = "I'm your me shooter.";
        var doc4 = "This is a test text. This is a test text? This is a test text, This is a test text"; // 2
        var doc5 = "This is a test text. This is a test text? a is This? test text, is a test This text"; // 3
        var doc6 = "This is a test text. a is This? test text, is a test This text"; // 4
        var doc7 = "This is a test text. This is a test text? This is a test text, This is a test text is the sorry"; // 1

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3),
                Map.of("id", "doc4", "text", doc4),
                Map.of("id", "doc5", "text", doc5),
                Map.of("id", "doc6", "text", doc6),
                Map.of("id", "doc7", "text", doc7)
        );

        List<String> result = SearchEngine.search(docs, "This is a test text");
        assertEquals(List.of("doc7", "doc4", "doc5", "doc6", "doc1", "doc2"), result);
    }

}
