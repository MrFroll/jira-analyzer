package org.opengravity.cohesion.extractor;

import java.util.ArrayList;
import java.util.List;

public class Issue {

  private final List<String> components = new ArrayList<>();
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getComponents() {
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
