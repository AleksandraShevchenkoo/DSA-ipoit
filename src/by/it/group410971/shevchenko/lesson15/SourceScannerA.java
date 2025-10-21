package by.it.group410971.shevchenko.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerA {

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(src), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".java")) {
                        processJavaFile(file, fileInfos);
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

    private static void processJavaFile(Path file, List<FileInfo> fileInfos) {
        try {
            String content = readFile(file);

            // Проверка на тесты
            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                return;
            }

            // Обработка содержимого
            String processedContent = processContent(content);

            // Получение относительного пути
            String srcPath = System.getProperty("user.dir") + File.separator + "src" + File.separator;
            String absolutePath = file.toAbsolutePath().toString();
            String relativePath = absolutePath.substring(srcPath.length());

            // Расчет размера в байтах
            int size = processedContent.getBytes(StandardCharsets.UTF_8).length;

            fileInfos.add(new FileInfo(size, relativePath));

        } catch (IOException e) {
            // Игнорируем ошибки чтения
        }
    }

    private static String readFile(Path file) throws IOException {
        // Пробуем разные кодировки для обработки MalformedInputException
        Charset[] charsets = {StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, Charset.forName("Windows-1251")};

        for (Charset charset : charsets) {
            try {
                return Files.readString(file, charset);
            } catch (MalformedInputException e) {
                // Пробуем следующую кодировку
                continue;
            }
        }

        // Если все кодировки не подошли, используем UTF-8 с обработкой ошибок
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            return ""; // Возвращаем пустую строку в случае ошибки
        }
    }

    private static String processContent(String content) {
        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Пропускаем package и import
            if (trimmedLine.startsWith("package ") || trimmedLine.startsWith("import ")) {
                continue;
            }

            result.append(line).append("\n");
        }

        // Удаляем символы с кодом <33 в начале и конце
        String processed = result.toString();
        processed = removeLowCharsFromStart(processed);
        processed = removeLowCharsFromEnd(processed);

        return processed;
    }

    private static String removeLowCharsFromStart(String str) {
        int start = 0;
        while (start < str.length() && str.charAt(start) < 33) {
            start++;
        }
        return str.substring(start);
    }

    private static String removeLowCharsFromEnd(String str) {
        int end = str.length();
        while (end > 0 && str.charAt(end - 1) < 33) {
            end--;
        }
        return str.substring(0, end);
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