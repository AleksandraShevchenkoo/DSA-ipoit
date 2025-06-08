package by.it.group410971.shevchenko.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/*
Видеорегистраторы и площадь.
На площади установлена одна или несколько камер.
Известны данные о том, когда каждая из них включалась и выключалась (отрезки работы)
Известен список событий на площади (время начала каждого события).
Вам необходимо определить для каждого события сколько камер его записали.

В первой строке задано два целых числа:
    число включений камер (отрезки) 1<=n<=50000
    число событий (точки) 1<=m<=50000.

Следующие n строк содержат по два целых числа ai и bi (ai<=bi) -
координаты концов отрезков (время работы одной какой-то камеры).
Последняя строка содержит m целых чисел - координаты точек.
Все координаты не превышают 10E8 по модулю (!).

Точка считается принадлежащей отрезку, если она находится внутри него или на границе.

Для каждой точки в порядке их появления во вводе выведите,
скольким отрезкам она принадлежит.
    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/

public class A_QSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = by.it.group410971.shevchenko.lesson05.A_QSort.class.getResourceAsStream("dataA.txt");
        by.it.group410971.shevchenko.lesson05.A_QSort instance = new by.it.group410971.shevchenko.lesson05.A_QSort();
        int[] result = instance.getAccessory(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory(InputStream stream) throws FileNotFoundException {
        // Подготовка к чтению данных
        Scanner scanner = new Scanner(stream);
        // Число отрезков
        int n = scanner.nextInt();
        by.it.group410971.shevchenko.lesson05.A_QSort.Segment[] segments = new by.it.group410971.shevchenko.lesson05.A_QSort.Segment[n];
        // Число точек
        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        // Читаем сами отрезки
        for (int i = 0; i < n; i++) {
            segments[i] = new by.it.group410971.shevchenko.lesson05.A_QSort.Segment(scanner.nextInt(), scanner.nextInt());
        }
        // Читаем точки
        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        // Сортируем отрезки
        Arrays.sort(segments, Comparator.comparingInt(s -> s.start));

        // Создаем массив пар (точка, индекс)
        PointWithIndex[] indexedPoints = new PointWithIndex[m];
        for (int i = 0; i < m; i++) {
            indexedPoints[i] = new PointWithIndex(points[i], i);
        }

        // Сортируем точки
        Arrays.sort(indexedPoints, Comparator.comparingInt(p -> p.value));

        // Подсчет охвата
        int segmentIndex = 0;
        for (PointWithIndex point : indexedPoints) {
            while (segmentIndex < n && segments[segmentIndex].start <= point.value) {
                segmentIndex++;
            }
            // Теперь segmentIndex указывает на количество отрезков, которые начинаются до или в точке
            int count = 0;
            for (int j = 0; j < segmentIndex; j++) {
                if (segments[j].stop >= point.value) {
                    count++;
                }
            }
            result[point.index] = count;
        }

        return result;
    }

    // Отрезок
    private class Segment {
        int start;
        int stop;

        Segment(int start, int stop) {
            if (start > stop) {
                this.start = stop;
                this.stop = start;
            } else {
                this.start = start;
                this.stop = stop;
            }
        }
    }

    // Класс для хранения точки с индексом
    private class PointWithIndex {
        int value;
        int index;

        PointWithIndex(int value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}
