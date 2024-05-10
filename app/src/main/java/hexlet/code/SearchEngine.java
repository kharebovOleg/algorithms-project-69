package hexlet.code;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {

    private static void addDocument(String documentId, String documentText, Map<String, List<String>> index) {
        String clearText = documentText.replaceAll("(\\p{Punct})*", "");
        String[] words = clearText.split("\\s+");
        for (String word : words) {
            word = word.toLowerCase();
            if (index.containsKey(word)) {
                List<String> docList = index.get(word);
                docList.add(documentId);
            } else {
                List<String> docList = new ArrayList<>();
                docList.add(documentId);
                index.put(word, docList);
            }
        }
    }

    private static int countMatches(String text, String word) {
        int count = 0;
        while (text.contains(word)) {
            text = text.replaceFirst(word, "");
            count++;
        }
        return count;
    }

    public static List<String> search(List<Map<String, String>> docs, String word) {

        Map<String, List<String>> index = new HashMap<>();
        Map<String, String> allDocs = new HashMap<>();
        List<String> invertedSearchResult = new ArrayList<>();
        for (Map<String, String> map : docs) {
            String docText = map.get("text");
            String documentId = map.get("id");
            if (!allDocs.containsKey(documentId)) allDocs.put(documentId, docText);
            addDocument(documentId, docText, index);
        }

        String correctedWord = word.toLowerCase().replaceAll("(\\p{Punct})*", "");
        List<String> piecesOfRequest =
                Arrays.stream(correctedWord.split("\\s+")).toList();

        Set<String> docsIdSet = piecesOfRequest.stream()
                .filter(index::containsKey)
                .flatMap(s -> index.get(s).stream())
                .collect(Collectors.toSet());

        if (docsIdSet.isEmpty()) return invertedSearchResult;

        List<DocsIdFullAndPartialMatches> matchesList = new ArrayList<>();
        docsIdSet.forEach(docId -> {
            long countFullMatches;
            long countPartialMatches;
            String correctedDocText = allDocs.get(docId)
                    .toLowerCase()
                    .replaceAll("(\\p{Punct})*", "");

            countFullMatches = countMatches(correctedDocText, correctedWord);
            String docTextWithoutFullMatches = correctedDocText.replaceAll(correctedWord, "");

            countPartialMatches = Arrays.stream(docTextWithoutFullMatches.split("\\s+"))
                    .filter(piecesOfRequest::contains)
                    .distinct()
                    .count();

            if (countFullMatches + countPartialMatches > 0)
                matchesList.add(new DocsIdFullAndPartialMatches(docId, countFullMatches, countPartialMatches));
        });

        invertedSearchResult = matchesList.stream()
                .sorted(Comparator.comparingLong(DocsIdFullAndPartialMatches::getFullMatches)
                        .thenComparingLong(DocsIdFullAndPartialMatches::getPartialMatches))
                .map(DocsIdFullAndPartialMatches::getId)
                .collect(Collectors.toList());
        Collections.reverse(invertedSearchResult);
        return invertedSearchResult;
    }

}
