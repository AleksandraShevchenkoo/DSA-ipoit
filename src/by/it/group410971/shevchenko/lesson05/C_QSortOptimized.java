package by.it.group410971.shevchenko.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/*
Видеорегистраторы и площадь 2.
Условие то же что и в задаче А.

        По сравнению с задачей A доработайте алгоритм так, чтобы
        1) он оптимально использовал время и память:
            - за стек отвечает элиминация хвостовой рекурсии
            - за сам массив отрезков - сортировка на месте
            - рекурсивные вызовы должны проводиться на основе 3-разбиения

        2) при поиске подходящих отрезков для точки реализуйте метод бинарного поиска
        для первого отрезка решения, а затем найдите оставшуюся часть решения
        (т.е. отрезков, подходящих для точки, может быть много)

    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/


public class C_QSortOptimized {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory2(InputStream stream) throws FileNotFoundException {
        // Подготовка к чтению данных
        Scanner scanner = new Scanner(stream);
        // Число отрезков
        int n = scanner.nextInt();
        by.it.group410971.shevchenko.lesson05.C_QSortOptimized.Segment[] segments = new by.it.group410971.shevchenko.lesson05.C_QSortOptimized.Segment[n];
        // Число точек
        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        // Читаем сами отрезки
        for (int i = 0; i < n; i++) {
            segments[i] = new by.it.group410971.shevchenko.lesson05.C_QSortOptimized.Segment(scanner.nextInt(), scanner.nextInt());
        }
        // Читаем точки
        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        // Сортируем отрезки
        Arrays.sort(segments, Comparator.comparingInt(s -> s.start));

        // Для каждой точки находим количество отрезков, которые её охватывают
        for (int i = 0; i < m; i++) {
            result[i] = countSegmentsCoveringPoint(segments, points[i]);
        }

        return result;
    }
    // Отрезок
    private class Segment {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }
    }
    private int countSegmentsCoveringPoint(by.it.group410971.shevchenko.lesson05.C_QSortOptimized.Segment[] segments, int point) {
        // Используем бинарный поиск для нахождения первого отрезка, который может охватывать точку
        int left = 0;
        int right = segments.length - 1;
        int count = 0;

        // Бинарный поиск для нахождения первого отрезка, который начинается после или в точке
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (segments[mid].start <= point) {
                left = mid + 1; // Ищем дальше вправо
            } else {
                right = mid - 1; // Ищем влево
            }
        }

        // Теперь left указывает на первый отрезок, который начинается после точки
        // Проверяем все отрезки, которые начинаются до или в точке
        for (int j = 0; j < left; j++) {
            if (segments[j].stop >= point) {
                count++;
            }
        }

        return count;
    }



}
