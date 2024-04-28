package hexlet.code;

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
            if (clearSearchResultMap.isEmpty() && (word.matches(".*\\w.*"))) { // п.2
                /*
                1. разбиваем строку по пробелам
                2. для каждой части ищем количество совпадений
                3. суммируем
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

    // Инвертированный индекс

    private static Map<String, List<String>> index = new HashMap<>();

    // Добавление документа в индекс
    private static void addDocument(String documentId, String documentText) {
        String[] words = documentText.split("\\s+");
        for (String word : words) {
            // Приводим слово к нижнему регистру
            word = word.toLowerCase();

            // Если слово уже есть в индексе, добавляем документ к списку документов
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

    // инвертированный поиск
    public static List<String> invertedSearch(List<Map<String, String>> docs, String word) {

        List<String> clearSearchList;
        List<String> fuzzySearchList;
        Map<String, Long> clearSearchResultMap = new HashMap<>();
        Map<String, Long> fuzzySearchResultMap = new HashMap<>();

        for (Map<String, String> map : docs) {
            String text = map.get("text");
            String documentId = map.get("id");

            addDocument(documentId, text);
        }

        // todo что делать если искомое слово является фразой?

        word = word.toLowerCase();
        List<String> results = new ArrayList<>();

        for (String str : index.keySet()) {
            if (str.matches("^" + word + "(\\p{Punct})*$")) {
                // создали список всех документов, где нашли совпадения
                // так как в тексте искомая строка может содержаться несколько раз
                // то в списке одни и те же документы могут повторяться
                // todo надо будет вернуть список из документов где на первом месте
                //  будут те, которые чаще встречаются (попробовать через лямбды - rangingBy)

                results.addAll(index.get(str));
            }
        }

        return null;

    }

}
