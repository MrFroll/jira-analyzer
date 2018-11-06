package org.opengravity.jira.analyzer.cohesion;

import java.util.List;
import javax.inject.Inject;
import org.opengravity.jira.analyzer.Analyzer;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.api.Extractor;
import org.opengravity.jira.analyzer.domain.Issue;
import org.opengravity.jira.analyzer.extractors.http.CannotExtractIssuesException;
import org.opengravity.jira.analyzer.graph.CannotExportGraphException;
import org.opengravity.jira.analyzer.graph.CohesionGraph;
import org.opengravity.jira.analyzer.loader.CannotLoadException;
import org.opengravity.jira.analyzer.loader.IssueLoader;

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
      cohesionGraph.build(issues);
      cohesionGraph.exportToGraphML(config);

    } catch (CannotExtractIssuesException | CannotExportGraphException | CannotLoadException e) {
      throw new CannotAnalyzeException(e);
    }
  }
}
