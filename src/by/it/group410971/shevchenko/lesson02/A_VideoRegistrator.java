package by.it.group410971.shevchenko.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
Даны события events
реализуйте метод calcStartTimes, так, чтобы число включений регистратора на
заданный период времени (1) было минимальным, а все события events
были зарегистрированы.
Алгоритм жадный. Для реализации обдумайте надежный шаг.
*/

public class A_VideoRegistrator {

    public static void main(String[] args) {
        A_VideoRegistrator instance = new A_VideoRegistrator();
        double[] events = new double[]{1, 1.1, 1.6, 2.2, 2.4, 2.7, 3.9, 8.1, 9.1, 5.5, 3.7};
        List<Double> starts = instance.calcStartTimes(events, 1); //рассчитаем моменты старта, с длинной сеанса 1
        System.out.println(starts);                            //покажем моменты старта
    }

    //модификаторы доступа опущены для возможности тестирования
    List<Double> calcStartTimes(double[] events, double workDuration) {
        List<Double> result = new ArrayList<>();
        Arrays.sort(events);  // Сортировка событий по времени

        int i = 0;
        while (i < events.length) {
            // Выбираем самое раннее непокрытое событие
            double start = events[i];
            result.add(start);

            // Вычисляем, до какого момента работает камера
            double end = start + workDuration;

            // Пропускаем все события, попадающие в этот интервал
            while (i < events.length && events[i] <= end) {
                i++;
            }
        }

        return result;
    }
}
