package com.codereview;

import com.codereview.runners.FileCollector;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("Usage: java -jar tool.jar <project-path>");
            return;
        }
        String projectPath = args[0];
        System.out.println("Scanning for .java files in: " + projectPath);
        File root = new File(projectPath);
        if (!root.exists() || !root.isDirectory()) {
            System.out.println("Error: Path does not exist or is not a directory.");
            return;
        }

        List<File> files = FileCollector.collectJavaFiles(root);
        System.out.println("Found " + files.size() + " .java files");
        for(File file : files) {
            System.out.println(file.getAbsolutePath());
        }

    }
}
