package by.it.group410971.shevchenko.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
http://planetcalc.ru/1721/

Дано:
    Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
    Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
    Рекурсивно вычислить расстояние редактирования двух данных непустых строк

    Sample Input 1:
    ab
    ab
    Sample Output 1:
    0

    Sample Input 2:
    short
    ports
    Sample Output 2:
    3

    Sample Input 3:
    distance
    editing
    Sample Output 3:
    5
*/

public class A_EditDist {

    // Мапа для хранения результатов уже вычисленных подзадач
    private Map<String, Integer> memo = new HashMap<>();

    // Основной метод (возможно с опечаткой "Edinting", можно оставить так для совместимости)
    int getDistanceEdinting(String one, String two) {
        memo.clear(); // Обязательно очищаем кэш перед каждым новым вычислением
        return calculateDistance(one, two, one.length(), two.length());
    }

    // Рекурсивный метод для вычисления расстояния Левенштейна с мемоизацией
    private int calculateDistance(String one, String two, int lenOne, int lenTwo) {
        if (lenOne == 0) return lenTwo;
        if (lenTwo == 0) return lenOne;

        String key = lenOne + "-" + lenTwo;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        if (one.charAt(lenOne - 1) == two.charAt(lenTwo - 1)) {
            int distance = calculateDistance(one, two, lenOne - 1, lenTwo - 1);
            memo.put(key, distance);
            return distance;
        }

        int insert = calculateDistance(one, two, lenOne, lenTwo - 1);
        int delete = calculateDistance(one, two, lenOne - 1, lenTwo);
        int replace = calculateDistance(one, two, lenOne - 1, lenTwo - 1);

        int distance = 1 + Math.min(insert, Math.min(delete, replace));
        memo.put(key, distance);
        return distance;
    }

    // Тестирование
    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_EditDist.class.getResourceAsStream("dataABC.txt");
        A_EditDist instance = new A_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine())); // должно быть 0
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine())); // должно быть 3
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine())); // должно быть 5
    }
}
