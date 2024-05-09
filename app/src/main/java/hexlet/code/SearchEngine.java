package hexlet.code;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> docs, String word) {
        /*
        1) Проводим четкий поиск - результаты храним в clearSearchResultMap
        2) Если поиск не дал результатов и в искомой строке есть пробелы,
           проводим нечеткий поиск - результаты храним в fuzzySearchResultMap
        3) Создаем два сортированных листа по каждой hashMap и складываем, так чтобы нечеткий поиск был после
        4) Возвращаем итоговый лист
         */
        List<String> clearSearchList;
        List<String> fuzzySearchList;
        Map<String, Long> clearSearchResultMap = new HashMap<>();
        Map<String, Long> fuzzySearchResultMap = new HashMap<>();

        for (Map<String, String> map : docs) {
            String text = map.get("text");
            String key = map.get("id");

            long value = countCoincidence(text, word); // п.1
            if (value > 0) clearSearchResultMap.put(key, value);

            // Если в слове есть пробелы и точных совпадений не найдено
            if (clearSearchResultMap.isEmpty() && (word.matches(".*\\s.*"))) { // п.2
                /*
                1) разбиваем строку по пробелам
                2) для каждой части ищем количество совпадений
                3) суммируем
                 */
                long sum = Arrays.stream(word.split(" "))
                        .mapToLong(s -> countCoincidence(text, s))
                        .sum();
                if (sum > 0) {
                    fuzzySearchResultMap.put(key, sum);
                }
            }
        }

        if (clearSearchResultMap.isEmpty() && fuzzySearchResultMap.isEmpty()) return List.of(); // п.3
        else if (clearSearchResultMap.isEmpty()) return makeSortedList(fuzzySearchResultMap);
        else if (fuzzySearchResultMap.isEmpty()) return makeSortedList(clearSearchResultMap);
        else {
            clearSearchList = makeSortedList(clearSearchResultMap);
            fuzzySearchList = makeSortedList(fuzzySearchResultMap);
            clearSearchList.addAll(fuzzySearchList);
            return clearSearchList;
        }
    }

    private static long countCoincidence(String text, String word) {
        return Arrays.stream(text.split(" "))
                .filter(s -> s.matches("^" + word + "(\\p{Punct})*$"))
                .count();
    }

    private static List<String> makeSortedList(Map<String, Long> map) {
        List<String> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }

    // Inverted search

    private final static Map<String, List<String>> index = new HashMap<>();

    // Добавление документа в индекс
    private static void addDocument(String documentId, String documentText) {
        // очистим слова от знаков препинания
        String clearText = documentText.replaceAll("(\\p{Punct})*", "");
        String[] words = clearText.split("\\s+");
        for (String word : words) {
            // Приводим слово к нижнему регистру
            word = word.toLowerCase();

            // Если слово уже есть в индексе, добавляем документ к списку документов
            // todo Если в тексте было несколько одинаковых слов, то он добавит id документа 2 раза
            if (index.containsKey(word)) {
                List<String> docList = index.get(word);
                docList.add(documentId);
            } else {
                // В противном случае создаем новую запись для слова
                List<String> docList = new ArrayList<>();
                docList.add(documentId);
                index.put(word, docList);
            }
        }
    }

    public static List<String> invertedSearch(List<Map<String, String>> docs, String word) {

        List<String> invertedSearchResult = new ArrayList<>();

        List<String> clearSearchList;
        List<String> fuzzySearchList;
        Map<String, String> allDocs = new HashMap<>(); // специально завели эту hashMap, она нам понадобится попозже

        // делаем инвертированный индекс
        for (Map<String, String> map : docs) {
            String docText = map.get("text");
            String documentId = map.get("id");
            if (!allDocs.containsKey(documentId)) allDocs.put(documentId, docText); // заполняем allDocs
            addDocument(documentId, docText);
        }

        String correctedWord = word.toLowerCase().replaceAll("(\\p{Punct})*", "");
        List<String> results;

        if (correctedWord.contains(" ")) { // если запрос содержит несколько слов
            // todo написать проверку более правильную запрос может содержать 1 слово и пробел

            List<String> piecesOfRequest =
                    Arrays.stream(correctedWord.split("\\s+")).toList(); // разбили запрос на слова

            Set<String> docsIdSet = piecesOfRequest.stream() // Set из id тех док-ов, где есть часть искомого слова
                    .filter(index::containsKey)
                    .flatMap(s -> index.get(s).stream())
                    .collect(Collectors.toSet());

            if (docsIdSet.isEmpty()) return invertedSearchResult; // если пусто, значит нет ни одного слова из запроса


            // теперь надо пройтись по текстам, и в каждом тексте проверить следующее:
            // 1) Есть ли в тексте полное соответствие с запросом?
            // 2) Если нет, то найти те слова, которые встречаются в нем?

            Map<String, Long> documentsWithCompleteCoincidence = new HashMap<>(); // для текстов с полным совпадением
            Map<String, Long> documentsWithFuzzyCoincidence = new HashMap<>();// для текстов с неполным совпадением

            for (String docId : docsIdSet) {
                // todo здесь нужно добиться сложения value при условии, что запрос содержит и полное совпадение и частичные совпадения

                String correctedDocText = allDocs.get(docId).toLowerCase().replaceAll("(\\p{Punct})*", "");
                if (correctedDocText.contains(correctedWord)) { // если найдено полное совпадение
                    long value = StringUtils.countMatches(correctedDocText, correctedWord);
                    if (value > 0) documentsWithCompleteCoincidence.put(docId, value);
                } else {
                    long value = Arrays.stream(correctedDocText.split("\\s+")) // сколько слов из запроса есть в документе
                            .filter(piecesOfRequest::contains)
                            .distinct() // без повторений одних и тех же слов
                            .count();
                    if (value > 0) documentsWithFuzzyCoincidence.put(docId, value);
                }
            }
            clearSearchList = makeSortedList(documentsWithCompleteCoincidence);
            fuzzySearchList = makeSortedList(documentsWithFuzzyCoincidence);

            clearSearchList.addAll(fuzzySearchList);

            invertedSearchResult = clearSearchList.stream()
                    .distinct().collect(Collectors.toList());

            // если искомая строка является одним словом
        } else if (index.containsKey(correctedWord)) { // если в инвертированном индексе есть искомое слово,
            // то достаем список id документов, в которых оно встречается
            // todo в этом месте код работает не правильно
            results = index.get(correctedWord);
            Map<String, Long> searchResultMap = new HashMap<>();

            results
                    .forEach(s -> {
                        String text = allDocs.get(s);
                        long value = countCoincidence(text, word);
                        searchResultMap.put(s, value); // todo здесь ошибка s - это текст документа, а нам нужен его id
                    });

            invertedSearchResult = makeSortedList(searchResultMap);
        }
        return invertedSearchResult;
    }

}
