package org.opengravity.jira.analyzer.graph;

import java.util.ArrayList;
import java.util.List;
import org.opengravity.jira.analyzer.core.domain.Issue;

public class IssueHolder {

  private List<Issue> issues;
  private CohesionGraph cohesionGraph;


  public void setUp() {
    issues = new ArrayList<>();
    cohesionGraph = new CohesionGraph();
  }

  public void tearDown() {
    issues = null;
    cohesionGraph = null;
  }

  protected IssueBuilder issue(String issues) {
    return new IssueBuilder(this, issues);
  }

  protected void buildGraph() {
    cohesionGraph.buildAndWeight(issues);
  }

  protected double weight(String s, String s1) {
    return cohesionGraph.weight(s, s1);
  }

  public void add(Issue issue) {
    issues.add(issue);
  }
}
