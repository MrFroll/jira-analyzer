package org.opengravity.jira.analyzer.loader;

import java.util.List;
import org.opengravity.jira.analyzer.domain.Issue;

public interface IssueLoader {

  void load(List<Issue> issues) throws CannotLoadException;
}
