package org.opengravity.jira.analyzer.core;

import java.util.List;
import org.opengravity.jira.analyzer.core.domain.Issue;
import org.opengravity.jira.analyzer.loader.CannotLoadException;

public interface IssueLoader {

  void load(List<Issue> issues) throws CannotLoadException;
}
