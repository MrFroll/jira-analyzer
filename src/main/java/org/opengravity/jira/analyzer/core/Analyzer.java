package org.opengravity.jira.analyzer.core;

import org.opengravity.jira.analyzer.Configuration;

public interface Analyzer {

  void analyze(Configuration config)
      throws CannotExtractIssuesException, CannotExportGraphException, CannotAnalyzeException;
}
