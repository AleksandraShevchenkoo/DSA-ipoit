package by.it.group410971.shevchenko.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerB {

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(src), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".java")) {
                        processJavaFile(file, fileInfos, src);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Сортировка по размеру, затем по пути
        fileInfos.sort((f1, f2) -> {
            int sizeCompare = Integer.compare(f1.size, f2.size);
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            return f1.relativePath.compareTo(f2.relativePath);
        });

        // Вывод результатов
        for (FileInfo info : fileInfos) {
            System.out.println(info.size + " " + info.relativePath);
        }
    }

    private static void processJavaFile(Path file, List<FileInfo> fileInfos, String src) {
        try {
            String content = readFileWithEncodingFallback(file);

            // Проверка на тесты
            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                return;
            }

            // Обработка содержимого
            String processedContent = processContent(content);

            // Получение относительного пути
            String absolutePath = file.toAbsolutePath().toString();
            String relativePath = absolutePath.substring(src.length());

            // Расчет размера в байтах
            int size = processedContent.getBytes(StandardCharsets.UTF_8).length;

            fileInfos.add(new FileInfo(size, relativePath));

        } catch (Exception e) {
            // Игнорируем файлы с ошибками
        }
    }

    private static String readFileWithEncodingFallback(Path file) throws IOException {
        Charset[] charsets = {
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                Charset.forName("Windows-1251"),
                Charset.forName("CP1252")
        };

        for (Charset charset : charsets) {
            try {
                return Files.readString(file, charset);
            } catch (MalformedInputException e) {
                continue;
            }
        }

        // Если все кодировки не подошли, читаем как байты и конвертируем с заменой невалидных символов
        byte[] bytes = Files.readAllBytes(file);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String processContent(String content) {
        StringBuilder result = new StringBuilder();
        boolean inMultiLineComment = false;
        boolean inString = false;
        char stringDelimiter = '"';
        char prevChar = '\0';

        int i = 0;
        int n = content.length();

        while (i < n) {
            char currentChar = content.charAt(i);

            if (!inMultiLineComment && !inString) {
                // Проверка начала однострочного комментария
                if (currentChar == '/' && i + 1 < n && content.charAt(i + 1) == '/') {
                    // Пропускаем до конца строки
                    while (i < n && content.charAt(i) != '\n') {
                        i++;
                    }
                    if (i < n) {
                        result.append('\n');
                    }
                    i++;
                    continue;
                }

                // Проверка начала многострочного комментария
                if (currentChar == '/' && i + 1 < n && content.charAt(i + 1) == '*') {
                    inMultiLineComment = true;
                    i += 2;
                    continue;
                }

                // Проверка начала строки
                if (currentChar == '"' || currentChar == '\'') {
                    inString = true;
                    stringDelimiter = currentChar;
                    result.append(currentChar);
                    i++;
                    continue;
                }

                // Проверка package и import
                if (i == 0 || content.charAt(i - 1) == '\n') {
                    if (isPackageOrImport(content, i)) {
                        // Пропускаем всю строку
                        while (i < n && content.charAt(i) != '\n') {
                            i++;
                        }
                        if (i < n) {
                            i++; // Пропускаем \n
                        }
                        continue;
                    }
                }
            } else if (inMultiLineComment) {
                // Проверка конца многострочного комментария
                if (currentChar == '*' && i + 1 < n && content.charAt(i + 1) == '/') {
                    inMultiLineComment = false;
                    i += 2;
                    continue;
                }
                i++;
                continue;
            } else if (inString) {
                // Проверка конца строки
                if (currentChar == stringDelimiter && prevChar != '\\') {
                    inString = false;
                }
                // Экранирование в строках
                if (currentChar == '\\' && prevChar != '\\') {
                    prevChar = currentChar;
                    result.append(currentChar);
                    i++;
                    continue;
                }
            }

            if (!inMultiLineComment) {
                result.append(currentChar);
            }

            prevChar = currentChar;
            i++;
        }

        String processed = result.toString();

        // Удаляем символы с кодом <33 в начале и конце
        processed = trimLowChars(processed);

        // Удаляем пустые строки
        processed = removeEmptyLines(processed);

        return processed;
    }

    private static boolean isPackageOrImport(String content, int start) {
        if (start + 6 < content.length()) {
            String nextChars = content.substring(start, start + 7);
            if (nextChars.startsWith("package") &&
                    (start + 7 >= content.length() || Character.isWhitespace(content.charAt(start + 7)))) {
                return true;
            }
        }
        if (start + 5 < content.length()) {
            String nextChars = content.substring(start, start + 6);
            if (nextChars.startsWith("import") &&
                    (start + 6 >= content.length() || Character.isWhitespace(content.charAt(start + 6)))) {
                return true;
            }
        }
        return false;
    }

    private static String trimLowChars(String str) {
        if (str.isEmpty()) return str;

        int start = 0;
        int end = str.length();

        // Удаляем символы с кодом <33 в начале
        while (start < end && str.charAt(start) < 33) {
            start++;
        }

        // Удаляем символы с кодом <33 в конце
        while (end > start && str.charAt(end - 1) < 33) {
            end--;
        }

        return str.substring(start, end);
    }

    private static String removeEmptyLines(String str) {
        String[] lines = str.split("\r?\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                result.append(line).append("\n");
            }
        }

        // Удаляем последний \n если он есть
        if (result.length() > 0 && result.charAt(result.length() - 1) == '\n') {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    static class FileInfo {
        int size;
        String relativePath;

        FileInfo(int size, String relativePath) {
            this.size = size;
            this.relativePath = relativePath;
        }
    }
}