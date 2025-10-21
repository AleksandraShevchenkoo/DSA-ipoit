package by.it.group410971.shevchenko.lesson13;

import java.util.*;

public class GraphA {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Парсинг входной строки
        Map<String, List<String>> graph = parseInput(input);

        // Топологическая сортировка
        List<String> result = topologicalSort(graph);

        // Вывод результата
        for (String node : result) {
            System.out.print(node + " ");
        }
    }

    private static Map<String, List<String>> parseInput(String input) {
        Map<String, List<String>> graph = new HashMap<>();

        // Разделяем по запятым, но учитываем, что внутри стрелок тоже могут быть пробелы
        String[] edges = input.split(",\\s*");

        for (String edge : edges) {
            // Разделяем по стрелке
            String[] parts = edge.split("\\s*->\\s*");
            if (parts.length == 2) {
                String from = parts[0].trim();
                String to = parts[1].trim();

                graph.putIfAbsent(from, new ArrayList<>());
                graph.putIfAbsent(to, new ArrayList<>());

                graph.get(from).add(to);
            }
        }

        return graph;
    }

    private static List<String> topologicalSort(Map<String, List<String>> graph) {
        List<String> result = new ArrayList<>();

        // Вычисляем полустепени захода для всех вершин
        Map<String, Integer> inDegree = new HashMap<>();

        // Инициализируем все вершины
        for (String node : graph.keySet()) {
            inDegree.put(node, 0);
        }

        // Вычисляем полустепени захода
        for (String node : graph.keySet()) {
            for (String neighbor : graph.get(node)) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        // Очередь с приоритетом для лексикографического порядка
        PriorityQueue<String> queue = new PriorityQueue<>();

        // Добавляем вершины с нулевой полустепенью захода
        for (String node : inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        // Алгоритм Кана
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Уменьшаем полустепени захода для соседей
            for (String neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return result;
    }
}