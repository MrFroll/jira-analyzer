package org.opengravity.jira.analyzer.core;

public class CannotAnalyzeException extends Exception {

  public CannotAnalyzeException(Exception e) {
    super(e);
  }
}
