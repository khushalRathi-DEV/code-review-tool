package com.codereview.model;

public class Finding {
    public enum Severity {
        INFO,
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }


    private final String filePath;
    private final int line;
    private final Severity severity;
    private final String tool ; 
    private final String message;

    public Finding(String filePath, int line, Severity severity, String tool, String message) {
        this.filePath = filePath;
        this.line = line;
        this.severity = severity;
        this.tool = tool;
        this.message = message;
    }

    // getters
    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getTool() {
        return tool;
    }

    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return severity + " | " + filePath + ":" + line + " | " + tool + " | " + message;
    }

}
