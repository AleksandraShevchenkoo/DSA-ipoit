package by.it.group410971.shevchenko.lesson08;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Даны число 1<=n<=100 ступенек лестницы и
целые числа −10000<=a[1],…,a[n]<=10000, которыми помечены ступеньки.
Найдите максимальную сумму, которую можно получить, идя по лестнице
снизу вверх (от нулевой до n-й ступеньки), каждый раз поднимаясь на
одну или на две ступеньки.

Sample Input 1:
2
1 2
Sample Output 1:
3

Sample Input 2:
2
2 -1
Sample Output 2:
1

Sample Input 3:
3
-1 2 1
Sample Output 3:
3

*/

public class C_Stairs {

    int getMaxSum(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt(); // Число ступенек
        int[] stairs = new int[n]; // Массив, содержащий значения на ступеньках

        for (int i = 0; i < n; i++) {
            stairs[i] = scanner.nextInt(); // Чтение значений на ступеньках
        }

        // Массив для хранения максимальной суммы до каждой ступеньки
        int[] dp = new int[n];

        // Инициализация первой ступеньки
        dp[0] = stairs[0];

        // Если есть вторая ступенька, инициализируем её
        if (n > 1) {
            dp[1] = Math.max(stairs[0] + stairs[1], stairs[1]);
        }

        // Динамическое программирование
        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1] + stairs[i], dp[i - 2] + stairs[i]);
        }

        // Максимальная сумма, которую можно получить, достигнув последней ступеньки
        return dp[n - 1];
    }


    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_Stairs.class.getResourceAsStream("dataC.txt");
        C_Stairs instance = new C_Stairs();
        int res=instance.getMaxSum(stream);
        System.out.println(res);
    }

}
