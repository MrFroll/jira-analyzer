package org.opengravity.jira.analyzer.graph;

import org.opengravity.jira.analyzer.core.domain.Issue;

public class IssueBuilder {

  private final String name;
  private final IssueHolder holder;


  public IssueBuilder(IssueHolder holder,
      String issues) {
    this.holder = holder;
    this.name = issues;

  }

  public void withComponents(String... components) {
    Issue issue = new Issue();
    issue.setId(name);
    for (String component : components) {
      issue.getComponents().add(component);
    }
    holder.add(issue);
  }
}
