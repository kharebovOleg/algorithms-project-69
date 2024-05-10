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

    public static List<String> search(List<Map<String, String>> docs, String word) {
        /*
            1. Создаем инвертированный индекс
            2. Делаем Set из id документов где есть хотя бы часть искомого слова
            3. Считаем полные совпадения.
            4. Считаем частичные совпадения.
            5. Включаем объект с информацией (id, полные совпадения, неполные совпадения) в итоговый список.
            6. Сортируем сперва по полным совпадениям, затем по неполным, по убыванию.
         */
        Map<String, List<String>> index = new HashMap<>();                                                  // |
        Map<String, String> allDocs = new HashMap<>();                                                      // |
        List<String> invertedSearchResult = new ArrayList<>();                                              // |
        for (Map<String, String> map : docs) {                                                              // | - п. 1.
            String docText = map.get("text");                                                               // |
            String documentId = map.get("id");                                                              // |
            if (!allDocs.containsKey(documentId)) allDocs.put(documentId, docText);                         // |
            addDocument(documentId, docText, index);                                                        // |
        }

        String correctedWord = word.toLowerCase().replaceAll("(\\p{Punct})*", "");
        List<String> piecesOfRequest =  // разбили запрос на слова, если было 1 слово то
                Arrays.stream(correctedWord.split("\\s+")).toList(); // будет список из 1 слова

        Set<String> docsIdSet = piecesOfRequest.stream()                                                    // |
                .filter(index::containsKey)                                                                 // | - п. 2.
                .flatMap(s -> index.get(s).stream())                                                        // |
                .collect(Collectors.toSet());                                                               // |

        if (docsIdSet.isEmpty()) return invertedSearchResult; // Если пусто, значит нет ни одного слова из запроса

        List<DocsIdFullAndPartialMatches> matchesList = new ArrayList<>(); // Итоговый список
        docsIdSet.forEach(docId -> {
            long countFullMatches;
            long countPartialMatches;
            String correctedDocText = allDocs.get(docId)
                    .toLowerCase()
                    .replaceAll("(\\p{Punct})*", "");

            countFullMatches = StringUtils.countMatches(correctedDocText, correctedWord);                        // п.3.
            String docTextWithoutFullMatches = correctedDocText.replaceAll(correctedWord, "");

            countPartialMatches = Arrays.stream(docTextWithoutFullMatches.split("\\s+"))                   // п.4.
                    .filter(piecesOfRequest::contains)
                    .distinct()
                    .count();

            // если никаких совпадений не найдено, то в результирующий список не включаем
            if (countFullMatches + countPartialMatches > 0)
                matchesList.add(new DocsIdFullAndPartialMatches(docId, countFullMatches, countPartialMatches));  // п.5.
        });

        //        matchesList.forEach(System.out::println);

        invertedSearchResult = matchesList.stream()
                .sorted(Comparator.comparingLong(DocsIdFullAndPartialMatches::getFullMatches)               // |
                        .thenComparingLong(DocsIdFullAndPartialMatches::getPartialMatches))                 // |
                .map(DocsIdFullAndPartialMatches::getId)                                                    // | - п. 6.
                .collect(Collectors.toList());                                                              // |
        Collections.reverse(invertedSearchResult);                                                          // |
        return invertedSearchResult;
    }

}
