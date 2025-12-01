package com.codereview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.codereview.checks.SecretScanner;
import com.codereview.checks.TestFileChecker;
import com.codereview.model.Finding;
import com.codereview.output.ReportPrinter;
import com.codereview.runners.CheckstyleRunner;
import com.codereview.runners.PMDRunner;

public class Main {
    private static final String DEFAULT_CHECKSTYLE_CONFIG = "D:\\code-review-tool\\src\\main\\resources\\google_checks.xml";
    private static final String DEFAULT_PMD_RULESET = String.join(",",
            "category/java/bestpractices.xml",
            "category/java/codestyle.xml",
            "category/java/errorprone.xml",
            "category/java/performance.xml",
            "category/java/design.xml"
    );

    public static void main(String[] args) throws Exception {
        CliOptions options = parseArguments(args);

        if (options.showHelp) {
            printHelp();
            return;
        }

        if (options.projectPath == null) {
            System.err.println("Error: Project path is required.");
            System.err.println("Use -help or --help for usage information.");
            System.exit(1);
        }

        File projectDir = new File(options.projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            System.err.println("Error: Project path does not exist or is not a directory: " + options.projectPath);
            System.exit(1);
        }

        // Determine which checks to run
        boolean runAll = !options.runCheckstyle && !options.runPMD &&
                !options.runTestCheck && !options.runSecretScan;

        List<Finding> findings = new ArrayList<>();

        // Run Checkstyle
        if (runAll || options.runCheckstyle) {
            System.out.println("=== Running Checkstyle ===");
            try {
                findings.addAll(CheckstyleRunner.run(options.projectPath, options.checkstyleConfig));
            } catch (Exception e) {
                System.err.println("Checkstyle failed: " + e.getMessage());
            }
        }

        // Run PMD
        if (runAll || options.runPMD) {
            System.out.println("=== Running PMD ===");
            try {
                findings.addAll(PMDRunner.run(options.projectPath, options.pmdRuleset));
            } catch (Exception e) {
                System.err.println("PMD failed: " + e.getMessage());
            }
        }

        // Run Test Checker
        if (runAll || options.runTestCheck) {
            System.out.println("=== Checking for Missing Tests ===");
            try {
                findings.addAll(TestFileChecker.run(options.projectPath));
            } catch (Exception e) {
                System.err.println("TestFileChecker failed: " + e.getMessage());
            }
        }

        // Run Secret Scanner
        if (runAll || options.runSecretScan) {
            System.out.println("=== Running Secret Scanner ===");
            try {
                findings.addAll(SecretScanner.run(options.projectPath));
            } catch (Exception e) {
                System.err.println("SecretScanner failed: " + e.getMessage());
            }
        }

        System.out.println("\n=== Analysis Results ===\n");
        ReportPrinter.print(findings);

        String sanitizedProjectName = sanitizeProjectName(options.projectPath);
        String reportOutputPath = "build/findings-report_" + sanitizedProjectName + ".txt";
        try {
            ReportPrinter.save(findings, reportOutputPath);
            System.out.println("Findings saved to: " + new File(reportOutputPath).getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save findings report: " + e.getMessage());
        }
    }

    private static CliOptions parseArguments(String[] args) {
        CliOptions options = new CliOptions();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-help":
                case "--help":
                case "-h":
                    options.showHelp = true;
                    break;
                case "-p":
                case "--project":
                    if (i + 1 < args.length) {
                        options.projectPath = args[++i];
                    } else {
                        System.err.println("Error: -p/--project requires a path argument");
                    }
                    break;
                case "--checkstyle":
                    options.runCheckstyle = true;
                    break;
                case "--pmd":
                    options.runPMD = true;
                    break;
                case "--test":
                    options.runTestCheck = true;
                    break;
                case "--secret":
                    options.runSecretScan = true;
                    break;
                case "--checkstyle-config":
                    if (i + 1 < args.length) {
                        options.checkstyleConfig = args[++i];
                    } else {
                        System.err.println("Error: --checkstyle-config requires a path argument");
                    }
                    break;
                case "--pmd-ruleset":
                    if (i + 1 < args.length) {
                        options.pmdRuleset = args[++i];
                    } else {
                        System.err.println("Error: --pmd-ruleset requires a ruleset argument");
                    }
                    break;
                default:
                    // If it doesn't start with -, treat it as project path
                    if (!arg.startsWith("-") && options.projectPath == null) {
                        options.projectPath = arg;
                    } else if (!arg.startsWith("-")) {
                        System.err.println("Warning: Ignoring unknown argument: " + arg);
                    } else {
                        System.err.println("Warning: Unknown option: " + arg);
                    }
                    break;
            }
        }

        return options;
    }

    private static void printHelp() {
        System.out.println("Code Review Tool - Static Analysis Tool");
        System.out.println("======================================");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar code-review-tool.jar [OPTIONS] <project-path>");
        System.out.println("  java -jar code-review-tool.jar [OPTIONS] -p <project-path>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help                    Show this help message");
        System.out.println("  -p, --project <path>          Path to the project directory to analyze");
        System.out.println();
        System.out.println("Check Options (if none specified, all checks run by default):");
        System.out.println("  --checkstyle                  Run Checkstyle code analysis");
        System.out.println("  --pmd                         Run PMD code analysis");
        System.out.println("  --test                        Check for missing test files");
        System.out.println("  --secret                      Scan for hardcoded secrets");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar code-review-tool.jar /path/to/project");
        System.out.println("  java -jar code-review-tool.jar --checkstyle --pmd /path/to/project");
        System.out.println("  java -jar code-review-tool.jar -p /path/to/project --test --secret");

    }

    private static String sanitizeProjectName(String projectPath) {
        String projectName = new File(projectPath).getName();
        if (projectName == null || projectName.trim().isEmpty()) {
            projectName = "project";
        }
        return projectName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static class CliOptions {
        boolean showHelp = false;
        String projectPath = null;
        boolean runCheckstyle = false;
        boolean runPMD = false;
        boolean runTestCheck = false;
        boolean runSecretScan = false;
        String checkstyleConfig = DEFAULT_CHECKSTYLE_CONFIG;
        String pmdRuleset = DEFAULT_PMD_RULESET;
    }
}

