package org.opengravity.jira.analyzer.loader;

public class CannotLoadException extends Exception {

  public CannotLoadException(Exception e) {
    super(e);
  }
}
