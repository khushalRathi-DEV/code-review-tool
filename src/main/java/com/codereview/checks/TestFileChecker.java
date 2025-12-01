package com.codereview.checks;

import com.codereview.model.Finding;
import com.codereview.runners.FileCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFileChecker {
    public static List<Finding> run(String projectPath){
        List<Finding>findings = new ArrayList<>();
        File mainSrc = new File(projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java");
        File testSrc = new File(projectPath + File.separator + "src" + File.separator + "test" + File.separator + "java");

        if(!mainSrc.exists()){
            return findings;
        }
        List<File>mainFiles = FileCollector.collectJavaFiles(mainSrc);
        for(File f : mainFiles){
            String relation = mainSrc.toPath().relativize(f.toPath()).toString();
            String className = f.getName().replace(".java","");
            String expectedTest = relation.replace(className + ".java",className + "Test.java");

            File expected = new File(testSrc,expectedTest);

            if(!expected.exists()){
                findings.add(new Finding(f.getAbsolutePath(),0, com.codereview.util.SeverityMapper.forMissingTest(),"TestCheck","Missing test file (expected: " + expected.getPath() + ")"));
            }
        }
        return findings;
    }
}


// D:\code-review-tool\src\main\java\com\codereview\model\Finding.java
// foer this above
// mainsrc = D:\code-review-tool\src\main\java
// testsrc = D:\code-review-tool\src\test\java
// relation = com\codereview\model\Finding.java
// classname = Finding
// expectedtest = FindingTest.java
// expected = D:\code-review-tool\src\test\java\com\codereview\model\FindingTest.java