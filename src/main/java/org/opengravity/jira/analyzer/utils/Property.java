package org.opengravity.jira.analyzer.utils;

import java.net.URI;

public enum Property {
  URL("jira.url", URI.class);

  private final Class<URI> type;
  private final String path;

  Property(String s, Class<URI> uriClass) {
    this.path = s;
    this.type = uriClass;
  }

  public Class<URI> getType() {
    return type;
  }

  public String getPath() {
    return path;
  }
}
