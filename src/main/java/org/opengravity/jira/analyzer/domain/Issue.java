package org.opengravity.jira.analyzer.domain;

import java.util.HashSet;
import java.util.Set;

public class Issue {

  private final Set<String> components = new HashSet<>();
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<String> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    return "Issue{" +
        "id='" + id + '\'' +
        ", components=" + components +
        '}';
  }
}
