package by.it.group410971.shevchenko.lesson13;

import java.util.*;

public class GraphC {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = parseInput(input);
        List<List<String>> scc = kosarajuSCC(graph);

        // Вывод компонент сильной связности
        for (List<String> component : scc) {
            // Сортируем вершины в компоненте лексикографически
            Collections.sort(component);
            // Выводим без пробелов
            for (String node : component) {
                System.out.print(node);
            }
            System.out.println();
        }
    }

    private static Map<String, List<String>> parseInput(String input) {
        Map<String, List<String>> graph = new HashMap<>();
        String[] edges = input.split(",\\s*");

        for (String edge : edges) {
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

    private static List<List<String>> kosarajuSCC(Map<String, List<String>> graph) {
        // Шаг 1: Первый обход в глубину для порядка завершения
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsFirstPass(node, graph, visited, stack);
            }
        }

        // Шаг 2: Транспонирование графа
        Map<String, List<String>> transposedGraph = transposeGraph(graph);

        // Шаг 3: Второй обход в глубину в порядке убывания времени завершения
        visited.clear();
        List<List<String>> scc = new ArrayList<>();

        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (!visited.contains(node)) {
                List<String> component = new ArrayList<>();
                dfsSecondPass(node, transposedGraph, visited, component);
                scc.add(component);
            }
        }

        return scc;
    }

    private static void dfsFirstPass(String node, Map<String, List<String>> graph,
                                     Set<String> visited, Stack<String> stack) {
        visited.add(node);

        for (String neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                dfsFirstPass(neighbor, graph, visited, stack);
            }
        }

        stack.push(node);
    }

    private static Map<String, List<String>> transposeGraph(Map<String, List<String>> graph) {
        Map<String, List<String>> transposed = new HashMap<>();

        // Инициализируем все вершины в транспонированном графе
        for (String node : graph.keySet()) {
            transposed.put(node, new ArrayList<>());
        }

        // Транспонируем ребра
        for (String node : graph.keySet()) {
            for (String neighbor : graph.get(node)) {
                transposed.get(neighbor).add(node);
            }
        }

        return transposed;
    }

    private static void dfsSecondPass(String node, Map<String, List<String>> transposedGraph,
                                      Set<String> visited, List<String> component) {
        visited.add(node);
        component.add(node);

        for (String neighbor : transposedGraph.get(node)) {
            if (!visited.contains(neighbor)) {
                dfsSecondPass(neighbor, transposedGraph, visited, component);
            }
        }
    }
}