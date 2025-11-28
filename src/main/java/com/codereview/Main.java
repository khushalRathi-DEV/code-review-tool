package com.codereview;

import com.codereview.checks.SecretScanner;
import com.codereview.checks.TestFileChecker;
import com.codereview.model.Finding;
import com.codereview.output.ReportPrinter;
import com.codereview.runners.CheckstyleRunner;
import com.codereview.runners.FileCollector;
import com.codereview.runners.PMDRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception{
        // if(args.length == 0){
        //     System.out.println("Usage: java -jar tool.jar <project-path>");
        //     return;
        // }
        // String projectPath = args[0];
        // System.out.println("Scanning for .java files in: " + projectPath);
        // File root = new File(projectPath);
        // if (!root.exists() || !root.isDirectory()) {
        //     System.out.println("Error: Path does not exist or is not a directory.");
        //     return;
        // }

        // List<File> files = FileCollector.collectJavaFiles(root);
        // System.out.println("Found " + files.size() + " .java files");
        // for(File file : files) {
        //     System.out.println(file.getAbsolutePath());
        // }

        String projectPath = "D:\\SpringBoot_Tutorials\\ecom-proj";
        String configPath = "src/main/resources/google_checks.xml";
        String pmdRuleset = "category/java/bestpractices.xml";

//        System.out.println("=== Running Checkstyle ===");
//        try {
//            List<Finding> findings = CheckstyleRunner.run(projectPath, configPath);
//
//            for (Finding f : findings) {
//                System.out.println(
//                        f.getSeverity() + " | " +
//                                f.getFilePath() + ":" + f.getLine() + " -> " +
//                                f.getMessage()
//                );
//            }
//        } catch (Exception e) {
//            System.err.println("Error running Checkstyle: " + e.getMessage());
//            e.printStackTrace();
//        }
//        System.out.println("\n=== Running PMD ===");
//        try{
//            List<Finding> pmdFindings = PMDRunner.run(projectPath, pmdRuleset);
//            System.out.println("\n=== PMD Findings ===");
//            pmdFindings.forEach(System.out::println);
//
//        }catch (Exception e) {
//            System.err.println("Error running PMD: " + e.getMessage());
//            e.printStackTrace();
//        }
        List<Finding> findings = new ArrayList<>();
        System.out.println("=== Running Checkstyle ===");
        try {
            findings.addAll(CheckstyleRunner.run(projectPath, configPath));
        } catch (Exception e) {
            System.err.println("Checkstyle failed: " + e.getMessage());
        }


        System.out.println("=== Running PMD ===");
        try {
            findings.addAll(PMDRunner.run(projectPath, pmdRuleset));
        } catch (Exception e) {
            System.err.println("PMD failed: " + e.getMessage());
        }

        System.out.println("=== Checking for Missing Tests ===");
        try {
            findings.addAll(TestFileChecker.run(projectPath));
        } catch (Exception e) {
            System.err.println("TestFileChecker failed: " + e.getMessage());
        }

        System.out.println("=== Running Secret Scanner ===");
        try {
            findings.addAll(SecretScanner.run(projectPath));
        } catch (Exception e) {
            System.err.println("SecretScanner failed: " + e.getMessage());
        }
        System.out.println("\n=== Analysis Results ===\n");
        ReportPrinter.print(findings);

    }
}

