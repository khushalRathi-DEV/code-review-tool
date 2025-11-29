package com.codereview.output;

import com.codereview.model.Finding;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ReportPrinter {

    public static void print(List<Finding> findings) {
        System.out.print(buildReport(findings));
    }

    public static void save(List<Finding> findings, String outputPath) throws Exception {
        Path path = Paths.get(outputPath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, buildReport(findings).getBytes(StandardCharsets.UTF_8));
    }

    private static String buildReport(List<Finding> findings) {
        if (findings.isEmpty()) {
            return "No findings!!" + System.lineSeparator();
        }

        Map<String, List<Finding>> byFile = findings.stream().collect(Collectors.groupingBy(Finding::getFilePath));
        StringBuilder report = new StringBuilder();

        for (String file : new TreeSet<>(byFile.keySet())) {
            report.append("File: ").append(file).append(System.lineSeparator());
            List<Finding> list = byFile.get(file);
            list.sort(Comparator.comparingInt(Finding::getLine));
            for (Finding f : list) {
                String line = f.getLine() > 0 ? "Line " + f.getLine() + ":" : "";
                report.append("  [")
                        .append(f.getSeverity())
                        .append("] ")
                        .append(line)
                        .append(" ")
                        .append(f.getTool())
                        .append(" - ")
                        .append(f.getMessage())
                        .append(" (")
                        .append(f.getTool())
                        .append(")")
                        .append(System.lineSeparator());
            }
            report.append(System.lineSeparator());
        }
        return report.toString();
    }
}
