package by.it.group410971.shevchenko.lesson15;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerC {

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileData> files = new ArrayList<>();

        // --- сбор java-файлов ---
        try {
            Files.walkFileTree(Paths.get(src), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".java")) {
                        try {
                            String content = readFileSafe(file);
                            if (!isTestFile(content)) {
                                String processed = preprocess(content);
                                if (!processed.isEmpty()) {
                                    String relPath = file.toAbsolutePath().toString().substring(src.length());
                                    files.add(new FileData(relPath, processed));
                                }
                            }
                        } catch (Exception ignore) { }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- поиск копий ---
        Map<String, Set<String>> copies = findCopies(files);

        // --- вывод ---
        printResults(copies);
    }

    // ---------- чтение файла с fallback по кодировкам ----------
    private static String readFileSafe(Path file) throws IOException {
        Charset[] charsets = {
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                Charset.forName("Windows-1251"),
                Charset.forName("CP1252")
        };
        for (Charset cs : charsets) {
            try {
                return Files.readString(file, cs);
            } catch (MalformedInputException ignored) { }
        }
        // fallback байтов
        byte[] bytes = Files.readAllBytes(file);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // ---------- определение тестовых файлов ----------
    private static boolean isTestFile(String content) {
        String lower = content.toLowerCase();
        return lower.contains("@test") || lower.contains("org.junit.test");
    }

    // ---------- удаление комментариев, package, import ----------
    private static String preprocess(String text) {
        StringBuilder sb = new StringBuilder();
        boolean inCommentLine = false;
        boolean inCommentBlock = false;
        boolean inString = false;
        char prev = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char next = (i + 1 < text.length()) ? text.charAt(i + 1) : 0;

            if (!inCommentBlock && !inCommentLine && !inString) {
                // начало комментария //
                if (c == '/' && next == '/') {
                    inCommentLine = true;
                    i++;
                    continue;
                }
                // начало комментария /* */
                if (c == '/' && next == '*') {
                    inCommentBlock = true;
                    i++;
                    continue;
                }
                // начало строки
                if (c == '"' || c == '\'') {
                    inString = true;
                    prev = c;
                    continue;
                }
            } else if (inString) {
                if (c == prev && text.charAt(i - 1) != '\\') {
                    inString = false;
                }
                continue;
            } else if (inCommentLine) {
                if (c == '\n') inCommentLine = false;
                continue;
            } else if (inCommentBlock) {
                if (c == '*' && next == '/') {
                    inCommentBlock = false;
                    i++;
                }
                continue;
            }

            sb.append(c);
        }

        // удаляем package, import
        String[] lines = sb.toString().split("\\R");
        sb.setLength(0);
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("package") || line.startsWith("import")) continue;
            sb.append(line).append(' ');
        }

        // заменяем все символы <33 на пробел
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) < 33) sb.setCharAt(i, ' ');
        }

        return sb.toString().trim();
    }

    // ---------- поиск копий ----------
    private static Map<String, Set<String>> findCopies(List<FileData> files) {
        Map<String, Set<String>> result = new TreeMap<>();

        for (int i = 0; i < files.size(); i++) {
            FileData f1 = files.get(i);
            for (int j = i + 1; j < files.size(); j++) {
                FileData f2 = files.get(j);

                // Оптимизация: сравниваем только если длины похожи
                int lenDiff = Math.abs(f1.text.length() - f2.text.length());
                if (lenDiff > 20) continue;

                int dist = levenshtein(f1.text, f2.text, 10);
                if (dist < 10) {
                    result.computeIfAbsent(f1.path, k -> new TreeSet<>()).add(f2.path);
                    result.computeIfAbsent(f2.path, k -> new TreeSet<>()).add(f1.path);
                }
            }
        }
        return result;
    }

    // ---------- ограниченный Левенштейн ----------
    private static int levenshtein(String s1, String s2, int limit) {
        int n = s1.length(), m = s2.length();
        if (Math.abs(n - m) > limit) return limit;
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];

        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            int min = cur[0];
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int cost = (c1 == s2.charAt(j - 1)) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
                if (cur[j] < min) min = cur[j];
            }
            if (min > limit) return limit;
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    // ---------- вывод ----------
    private static void printResults(Map<String, Set<String>> copies) {
        for (String path : copies.keySet()) {
            System.out.println(path);
            for (String copy : copies.get(path)) {
                System.out.println(copy);
            }
        }
    }

    // ---------- вспомогательная структура ----------
    private static class FileData {
        String path;
        String text;
        FileData(String path, String text) {
            this.path = path;
            this.text = text;
        }
    }
}
