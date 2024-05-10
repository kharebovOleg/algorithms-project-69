package hexlet.code;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {

    // Добавление документа в индекс
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

    public static List<String> invertedSearch(List<Map<String, String>> docs, String word) {

        Map<String, List<String>> index = new HashMap<>();
        Map<String, String> allDocs = new HashMap<>(); // специально завели эту hashMap, она нам понадобится попозже
        List<String> invertedSearchResult = new ArrayList<>();

        // делаем инвертированный индекс
        for (Map<String, String> map : docs) {
            String docText = map.get("text");
            String documentId = map.get("id");
            if (!allDocs.containsKey(documentId)) allDocs.put(documentId, docText); // заполняем allDocs
            addDocument(documentId, docText, index);
        }

        String correctedWord = word.toLowerCase().replaceAll("(\\p{Punct})*", "");
        List<String> piecesOfRequest =  // разбили запрос на слова, если было 1 слово то будет список из 1 слова
                Arrays.stream(correctedWord.split("\\s+")).toList();

        Set<String> docsIdSet = piecesOfRequest.stream() // Set из id тех док-ов, где есть хотя бы часть искомого слова
                .filter(index::containsKey)
                .flatMap(s -> index.get(s).stream())
                .collect(Collectors.toSet());

        if (docsIdSet.isEmpty()) return invertedSearchResult; // если пусто, значит нет ни одного слова из запроса
        List<DocsIdFullAndPartialMatches> matchesList = new ArrayList<>();

        // после того как посчитали полные совпадения надо поискать частичные
        // для этого удалим все полные совпадения и поищем, что осталось
        // если полных совпадений не было, значит текст не изменится

        docsIdSet.forEach(docId -> {
            long countFullMatches;
            long countPartialMatches;
            String correctedDocText = allDocs.get(docId).toLowerCase().replaceAll("(\\p{Punct})*", "");
            countFullMatches = StringUtils.countMatches(correctedDocText, correctedWord);
            String docTextWithoutFullMatches = correctedDocText.replaceAll(correctedWord, "");

            countPartialMatches = Arrays.stream(docTextWithoutFullMatches.split("\\s+"))
                    .filter(piecesOfRequest::contains)
                    .distinct()
                    .count();

            if (countFullMatches + countPartialMatches > 0)
                matchesList.add(new DocsIdFullAndPartialMatches(docId, countFullMatches, countPartialMatches));
        });

        //        matchesList.forEach(System.out::println);

        invertedSearchResult = matchesList.stream()
                .sorted(Comparator.comparing(DocsIdFullAndPartialMatches::getFullMatches).thenComparing(DocsIdFullAndPartialMatches::getPartialMatches))
                .map(DocsIdFullAndPartialMatches::getId)
                .collect(Collectors.toList());
        Collections.reverse(invertedSearchResult);
        return invertedSearchResult;
    }

}
