package com.codereview.checks;

import com.codereview.model.Finding;
import com.codereview.runners.FileCollector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecretScanner {
    private static final Pattern[] PATTERNS = new Pattern[]{
            Pattern.compile("(?i)password\\s*[:=]\\s*\"([^\"]{6,})\""),
            Pattern.compile("(?i)api[_-]?key\\s*[:=]\\s*\"?([A-Za-z0-9\\-_=]{8,})\"?"),
            Pattern.compile("-----BEGIN PRIVATE KEY-----")
    };

    public static List<Finding> run(String ProjectPath) throws IOException{
        List<Finding>findings = new ArrayList<>();
        List<File> files = FileCollector.collectJavaFiles(new File(ProjectPath));

        for(File f : files){
            //String content = Files.readString(f.toPath());
            String content = new String(Files.readAllBytes(f.toPath()), java.nio.charset.StandardCharsets.UTF_8);
            int lineNo = 1;
            String[] lines = content.split("\\R");
            for(String line : lines){
                for(Pattern p : PATTERNS){
                    Matcher m = p.matcher(line);
                    if(m.find()){
                        findings.add(new Finding(f.getAbsolutePath(),lineNo, Finding.Severity.WARN, "Secret","Possible hardcoded secret: " + m.group(0).trim()));
                    }
                }
                // entropy check for long strings
                if (line.contains("\"")) {
                    for (String token : line.split("\"")) {
                        if (isPotentialSecretToken(token)) {
                            double ent = shannonEntropy(token);
                            if (ent > 4.0) {
                                findings.add(new Finding(f.getAbsolutePath(), lineNo, Finding.Severity.WARN, "Secret",
                                        "High-entropy string detected"));
                            }
                        }
                    }
                }
                lineNo++;
            }
        }
        return findings;
    }

    private static boolean isPotentialSecretToken(String token) {
        String trimmed = token.trim();
        if (trimmed.length() < 40) {
            return false;
        }
        return trimmed.matches("[A-Za-z0-9+/=_-]+");
    }

    private static double shannonEntropy(String s){
        int[] freq = new int[256];
        for(char c:s.toCharArray()){
            freq[(int)c]++;
        }
        double entropy = 0.0;
        for(int f : freq){
            if(f == 0)
                continue;
            double p = (double) f / s.length();
            entropy -= p*(Math.log(p) / Math.log(2));
        }
        return entropy;
    }
}
