package com.codereview.util;

import com.codereview.model.Finding;

public class SeverityMapper {

    public static Finding.Severity forSecret(String matchedString) {
        matchedString = matchedString.toLowerCase();

        if (matchedString.contains("begin private key")) {
            return Finding.Severity.CRITICAL;
        }

        if (matchedString.contains("password") || matchedString.contains("api_key")) {
            return Finding.Severity.HIGH;
        }

        // entropy based findings
        return Finding.Severity.HIGH;
    }

    public static Finding.Severity forMissingTest() {
        return Finding.Severity.MEDIUM;
    }

    public static Finding.Severity forCheckstyle(String message) {
        if (message.contains("error")) return Finding.Severity.MEDIUM;
        return Finding.Severity.LOW;
    }

    public static Finding.Severity forPMD(int priority) {
        switch (priority) {
            case 1:
                return Finding.Severity.CRITICAL;
            case 2:
                return Finding.Severity.HIGH;
            case 3:
                return Finding.Severity.MEDIUM;
            case 4:
                return Finding.Severity.LOW;
            default:
                return Finding.Severity.INFO;
        }
    }
}
