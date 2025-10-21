package by.it.group410971.shevchenko.lesson14;

import java.util.*;

/**
 * Задача B — кластеризация сайтов по связям через DSU.
 *
 * Ввод:
 * каждая строка содержит пару сайтов, связанных через "+",
 * ввод завершается строкой "end".
 *
 * Неориентированные связи (A+B == B+A),
 * допускается связь сайта с самим собой.
 *
 * Вывод:
 * размеры всех кластеров сайтов (в порядке возрастания).
 */
public class SitesB {

    // Класс DSU с двумя эвристиками: по размеру и по сжатию пути
    static class DSU {
        private final Map<String, String> parent = new HashMap<>();
        private final Map<String, Integer> size = new HashMap<>();

        // найти корень с сжатием пути
        String find(String x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                size.put(x, 1);
            }
            if (!parent.get(x).equals(x))
                parent.put(x, find(parent.get(x)));
            return parent.get(x);
        }

        // объединить множества по размеру
        void union(String a, String b) {
            String rootA = find(a);
            String rootB = find(b);
            if (rootA.equals(rootB))
                return;
            int sizeA = size.get(rootA);
            int sizeB = size.get(rootB);

            if (sizeA < sizeB) {
                String tmp = rootA;
                rootA = rootB;
                rootB = tmp;
            }
            parent.put(rootB, rootA);
            size.put(rootA, sizeA + sizeB);
        }

        // получить размеры всех кластеров
        Collection<Integer> getClusterSizes() {
            Set<String> roots = new HashSet<>();
            for (String x : parent.keySet()) {
                roots.add(find(x));
            }
            List<Integer> result = new ArrayList<>();
            for (String r : roots) {
                result.add(size.get(find(r)));
            }
            return result;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DSU dsu = new DSU();

        // читаем пары до "end"
        while (true) {
            String line = sc.nextLine().trim();
            if (line.equals("end")) break;
            String[] sites = line.split("\\+");
            if (sites.length != 2) continue;
            String a = sites[0];
            String b = sites[1];
            dsu.union(a, b);
        }

        // получаем размеры кластеров
        List<Integer> sizes = new ArrayList<>(dsu.getClusterSizes());
        Collections.sort(sizes);

        // вывод
        for (int i = 0; i < sizes.size(); i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(sizes.get(i));
        }
        System.out.println();
    }
}
