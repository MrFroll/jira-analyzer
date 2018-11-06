package org.opengravity.jira.analyzer;

import org.opengravity.jira.analyzer.cohesion.CannotAnalyzeException;
import org.opengravity.jira.analyzer.extractors.http.CannotExtractIssuesException;
import org.opengravity.jira.analyzer.graph.CannotExportGraphException;

public interface Analyzer {

  void analyze(Configuration confi)
      throws CannotExtractIssuesException, CannotExportGraphException, CannotAnalyzeException;
}
