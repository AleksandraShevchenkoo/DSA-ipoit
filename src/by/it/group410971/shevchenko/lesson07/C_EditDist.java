package by.it.group410971.shevchenko.lesson07;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
    https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
    http://planetcalc.ru/1721/

Дано:
    Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
    Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
    Итерационно вычислить алгоритм преобразования двух данных непустых строк
    Вывести через запятую редакционное предписание в формате:
     операция("+" вставка, "-" удаление, "~" замена, "#" копирование)
     символ замены или вставки

    Sample Input 1:
    ab
    ab
    Sample Output 1:
    #,#,

    Sample Input 2:
    short
    ports
    Sample Output 2:
    -s,~p,#,#,#,+s,

    Sample Input 3:
    distance
    editing
    Sample Output 2:
    +e,#,#,-s,#,~i,#,-c,~g,


    P.S. В литературе обычно действия редакционных предписаний обозначаются так:
    - D (англ. delete) — удалить,
    + I (англ. insert) — вставить,
    ~ R (replace) — заменить,
    # M (match) — совпадение.
*/


public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int lenOne = one.length();
        int lenTwo = two.length();

        // Создаем двумерный массив для хранения расстояний
        int[][] dp = new int[lenOne + 1][lenTwo + 1];
        String[][] operations = new String[lenOne + 1][lenTwo + 1];

        // Инициализация первой строки и первого столбца
        for (int i = 0; i <= lenOne; i++) {
            dp[i][0] = i; // Расстояние от строки к пустой строке
            operations[i][0] = "-"; // Удаление
        }
        for (int j = 0; j <= lenTwo; j++) {
            dp[0][j] = j; // Расстояние от пустой строки к строке
            operations[0][j] = "+"; // Вставка
        }

        // Заполнение массива
        for (int i = 1; i <= lenOne; i++) {
            for (int j = 1; j <= lenTwo; j++) {
                if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                    operations[i][j] = "#"; // Совпадение
                } else {
                    int delete = dp[i - 1][j] + 1;
                    int insert = dp[i][j - 1] + 1;
                    int replace = dp[i - 1][j - 1] + 1;

                    // Находим минимальное значение и соответствующую операцию
                    if (delete <= insert && delete <= replace) {
                        dp[i][j] = delete;
                        operations[i][j] = "-"+one.charAt(i - 1); // Удаление
                    } else if (insert <= delete && insert <= replace) {
                        dp[i][j] = insert;
                        operations[i][j] = "+"+two.charAt(j - 1); // Вставка
                    } else {
                        dp[i][j] = replace;
                        operations[i][j] = "~"+two.charAt(j - 1); // Замена
                    }
                }
            }
        }

        // Формируем результат
        StringBuilder result = new StringBuilder();
        int i = lenOne, j = lenTwo;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && operations[i][j].equals("#")) {
                result.append("#,");
                i--;
                j--;
            } else if (i > 0 && operations[i][j].startsWith("-")) {
                result.append(operations[i][j]).append(",");
                i--;
            } else if (j > 0 && operations[i][j].startsWith("+")) {
                result.append(operations[i][j]).append(",");
                j--;
            } else if (i > 0 && j > 0 && operations[i][j].startsWith("~")) {
                result.append(operations[i][j]).append(",");
                i--;
                j--;
            }
        }

        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
    }

}