package com.codereview.runners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.codereview.model.Finding;
import com.codereview.model.Finding.Severity;
import com.puppycrawl.tools.checkstyle.*;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class CheckstyleRunner {

  public static List<Finding> run(String projectPath,String configPath) throws Exception{
      List<Finding> results = new ArrayList<>();
      Configuration config = ConfigurationLoader.loadConfiguration(
              configPath,
              new PropertiesExpander(System.getProperties()),
              ConfigurationLoader.IgnoredModulesOptions.OMIT
      );

      Checker checker = new Checker();
      checker.setModuleClassLoader(Checker.class.getClassLoader());
      checker.configure(config);

      checker.addListener(new AuditListener() {
        @Override
        public void auditStarted(AuditEvent evt){}

        @Override
        public void auditFinished(AuditEvent evt){}

        @Override
        public void fileStarted(AuditEvent evt){}

        @Override
        public void fileFinished(AuditEvent evt){}

        @Override
        public void addError(AuditEvent evt){
          String file = evt.getFileName();
          int line = evt.getLine();
          Severity severity = evt.getSeverityLevel() == SeverityLevel.ERROR ? Severity.ERROR : Severity.WARN; 
          results.add(new Finding(file,line,severity,"checkstyle",evt.getMessage()));
        }
        @Override
        public void addException(AuditEvent evt,Throwable throwable){}
      });
      
      List<File> files = FileCollector.collectJavaFiles(new File(projectPath));





      return results;
  }
}
