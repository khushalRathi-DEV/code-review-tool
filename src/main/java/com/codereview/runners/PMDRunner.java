package com.codereview.runners;

import com.codereview.model.Finding;
import com.codereview.model.Finding.Severity;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PMDRunner {
    public static List<Finding> run(String projectPath, String ruleset) throws Exception {
        List<Finding> findings = new ArrayList<>();

        PMDConfiguration configuration = new PMDConfiguration();
        Path path = Paths.get(projectPath);
        configuration.addInputPath(path);

        Language javaLanguage = LanguageRegistry.PMD.getLanguageById("java");
        if (javaLanguage != null) {
            LanguageVersion javaVersion = javaLanguage.getDefaultVersion();
            configuration.setDefaultLanguageVersion(javaVersion);
        }
        //configuration.addRuleSet(ruleset);
        // Support multiple rule sets separated by commas
        for (String rs : ruleset.split(",")) {
            if (!rs.trim().isEmpty()) {
                configuration.addRuleSet(rs.trim());
            }
        }

        // Create and run PMD analysis
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            Report report = pmd.performAnalysisAndCollectReport();
            
            // Convert PMD violations to Finding objects
            for (RuleViolation violation : report.getViolations()) {
                String filePath = violation.getFileId().getOriginalPath();
                int line = violation.getLocation().getStartLine();
                String message = violation.getDescription();
                
                // Map PMD priority to Finding severity
                // PMD priority: 1-2 = ERROR, 3-4 = WARN, 5 = INFO
//                Severity severity;
                int priority = violation.getRule().getPriority().getPriority();
                Severity severity = com.codereview.util.SeverityMapper.forPMD(priority);

//                if (priority <= 2) {
//                    severity = Severity.ERROR;
//                } else if (priority <= 4) {
//                    severity = Severity.WARN;
//                } else {
//                    severity = Severity.INFO;
//                }
                
                findings.add(new Finding(filePath, line, severity, "pmd", message));
            }
        }

        return findings;
    }
}
