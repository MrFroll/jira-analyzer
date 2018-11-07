package org.opengravity.jira.analyzer.extractors.http;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.core.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(HttpExtractor.class);

  private final List<Issue> issues = new ArrayList<>();
  private int position;
  private int tries;
  private Configuration configuration;
  private CallBack callBack;

  public Task(int startPosition, Configuration configuration, CallBack callBack) {
    this.position = startPosition;
    this.callBack = callBack;
    this.configuration = configuration;
  }

  public static Task of(int startPosition, Configuration configuration, CallBack callBack) {
    return new Task(startPosition, configuration, callBack);
  }

  public List<Issue> getIssues() {
    return issues;
  }

  @Override
  public void run() {
    tries++;
    final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    final URI url = configuration.getUrl();
    try (JiraRestClient rest = factory.create(url, new AnonymousAuthenticationHandler())) {
      final SearchRestClient search = rest.getSearchClient();
      final String jql = "project=" + configuration.getProject();
      final int taskSize = configuration.getTaskSize();
      final Promise<SearchResult> request = search.searchJql(jql, taskSize, position, null)
          .fail(this::fail)
          .done(this::success);
      request.claim();
    } catch (Exception e) {
      log.error("Error in retriever", e.getMessage());
    }
  }

  private void success(SearchResult searchResult) {
    searchResult.getIssues().forEach(item -> issues.add(convert(item)));
    callBack.onSuccess(this);
  }

  private void fail(Throwable throwable) {
    log.warn("Fail while execution of task {}", this);
    log.warn("Throwable:\n{}", throwable);
    callBack.onFail(this);
  }

  public boolean canRetry() {
    return tries < this.configuration.getRetries();
  }

  private Issue convert(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {
    Issue issue = new Issue();
    List<String> components = Lists.newArrayList(jiraIssue.getComponents())
        .stream()
        .map(BasicComponent::getName)
        .collect(Collectors.toList());
    issue.getComponents().addAll(components);
    issue.setId(jiraIssue.getKey());
    return issue;
  }

  @Override
  public String toString() {
    return "Task{" +
        "position=" + position +
        ", tries=" + tries +
        '}';
  }
}
