package org.opengravity.jira.analyzer.api;

import java.util.List;
import org.opengravity.jira.analyzer.domain.Issue;
import org.opengravity.jira.analyzer.extractors.http.CannotExtractIssuesException;

public interface Extractor {

  List<Issue> extract() throws CannotExtractIssuesException;
}
