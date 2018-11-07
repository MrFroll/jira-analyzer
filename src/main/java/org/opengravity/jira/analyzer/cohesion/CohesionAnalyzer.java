package org.opengravity.jira.analyzer.cohesion;

import java.util.List;
import javax.inject.Inject;
import org.opengravity.jira.analyzer.core.Analyzer;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.core.CannotAnalyzeException;
import org.opengravity.jira.analyzer.core.Extractor;
import org.opengravity.jira.analyzer.core.domain.Issue;
import org.opengravity.jira.analyzer.core.CannotExtractIssuesException;
import org.opengravity.jira.analyzer.core.CannotExportGraphException;
import org.opengravity.jira.analyzer.graph.CohesionGraph;
import org.opengravity.jira.analyzer.loader.CannotLoadException;
import org.opengravity.jira.analyzer.core.IssueLoader;

public class CohesionAnalyzer implements Analyzer {

  @Inject
  private Extractor extractor;

  @Inject
  private IssueLoader issueLoader;

  @Override
  public void analyze(Configuration config) throws CannotAnalyzeException {
    try {
      final List<Issue> issues = extractor.extract();
      issueLoader.load(issues);
      CohesionGraph cohesionGraph = new CohesionGraph();
      cohesionGraph.buildAndWeight(issues);
      cohesionGraph.exportToGraphML(config);

    } catch (CannotExtractIssuesException | CannotExportGraphException | CannotLoadException e) {
      throw new CannotAnalyzeException(e);
    }
  }
}
