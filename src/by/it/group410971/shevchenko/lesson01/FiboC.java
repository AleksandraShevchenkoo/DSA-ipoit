package by.it.group410971.shevchenko.lesson01;

/*
 * Даны целые числа 1<=n<=1E18 и 2<=m<=1E5,
 * необходимо найти остаток от деления n-го числа Фибоначчи на m
 * время расчета должно быть не более 2 секунд
 */

public class FiboC {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        int n = 55555;
        int m = 1000;
        System.out.printf("fasterC(%d)=%d \n\t time=%d \n\n", n, fibo.fasterC(n, m), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    long fasterC(long n, int m) {
        // 1. Найдём период Пизано
        int period = getPisanoPeriod(m);

        // 2. Сократим n по модулю периода
        int newN = (int)(n % period);

        // 3. Вычислим n-е число Фибоначчи по модулю m
        return getFibonacciMod(newN, m);
    }

    // Функция для поиска периода Пизано
    private int getPisanoPeriod(int m) {
        int prev = 0, curr = 1;
        for (int i = 0; i < m * 6; i++) { // Период максимум 6*m
            int next = (prev + curr) % m;
            prev = curr;
            curr = next;

            if (prev == 0 && curr == 1) return i + 1;
        }
        return 0; // не должно случиться
    }

    // Функция для вычисления F(n) % m
    private int getFibonacciMod(int n, int m) {
        if (n == 0) return 0;
        if (n == 1) return 1;

        int prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            int next = (prev + curr) % m;
            prev = curr;
            curr = next;
        }
        return curr;
    }


}

