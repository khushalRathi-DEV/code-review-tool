package com.codereview.output;

import com.codereview.model.Finding;

import java.util.*;
import java.util.stream.Collectors;

public class ReportPrinter {

    public static void print(List<Finding> findings) {
        if (findings.isEmpty()) {
            System.out.println("No findings!!");
            return;
        }

        Map<String, List<Finding>> byFile = findings.stream().collect(Collectors.groupingBy(Finding::getFilePath));

        for (String file : new TreeSet<>(byFile.keySet())) {
            System.out.println("File: " + file);
            List<Finding> list = byFile.get(file);
            list.sort(Comparator.comparingInt(Finding::getLine));
            for (Finding f : list) {
                String line = f.getLine() > 0 ? "Line " + f.getLine() + ":" : "";
                System.out.printf("  [%s] %s %s - %s (%s)%n",
                        f.getSeverity(), line, f.getTool(), f.getMessage(), f.getTool());
            }
            System.out.println();
        }
    }
}
