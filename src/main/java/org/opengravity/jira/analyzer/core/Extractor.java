package org.opengravity.jira.analyzer.core;

import java.util.List;
import org.opengravity.jira.analyzer.core.domain.Issue;

public interface Extractor {

  List<Issue> extract() throws CannotExtractIssuesException;
}
