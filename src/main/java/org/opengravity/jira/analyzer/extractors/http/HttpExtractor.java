package org.opengravity.jira.analyzer.extractors.http;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.BooleanLatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.api.Extractor;
import org.opengravity.jira.analyzer.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpExtractor implements CallBack, Extractor {

  private static final Logger log = LoggerFactory.getLogger(HttpExtractor.class);

  private final ScheduledExecutorService queueCheckTread;
  private final ExecutorService taskExecutor;
  private final Queue<Task> tasks = new LinkedBlockingQueue<>();
  private final Queue<Issue> issues = new ConcurrentLinkedQueue<>();
  private final Configuration configuration;
  private AtomicInteger taskRemain;
  private BooleanLatch completion = new BooleanLatch();

  public HttpExtractor(Configuration configuration) {
    this.configuration = configuration;
    queueCheckTread = Executors.newSingleThreadScheduledExecutor();
    taskExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public List<Issue> extract() throws CannotExtractIssuesException {
    prepareExtractionTasks();
    executeExtractionTasks();
    waitForCompletion();
    return new ArrayList<>(issues);
  }

  private void executeExtractionTasks() {
    queueCheckTread.scheduleAtFixedRate(
        () -> {
          try {
            Task task = tasks.poll();
            if (task != null) {
              log.info("Got task {} from queue", task);
              taskExecutor.execute(task);
            } else {
              log.info("No tasks in queue");
            }
          } catch (Exception e) {
            log.error("Caught error in queueCheckTread", e.getMessage());
          }
        }, configuration.getSchedulerDelay(), configuration.getSchedulerDelay(),
        TimeUnit.MILLISECONDS
    );
  }

  private void waitForCompletion() {
    try {
      completion.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void prepareExtractionTasks() throws CannotExtractIssuesException {
    int amount = calculateIssueAmount();
    log.info("There are {} issues", amount);
    taskRemain = new AtomicInteger(populateTaskQueue(amount));
  }

  private void stop() {
    queueCheckTread.shutdownNow();
    final List<Runnable> runnables = taskExecutor.shutdownNow();
    log.warn("Found {} unfinished tasks", runnables.size());
  }

  private int populateTaskQueue(int amount) {
    final int taskSize = configuration.getTaskSize();
    int startPosition = 0;
    while (startPosition <= amount) {
      tasks.add(Task.of(startPosition, configuration, this));
      startPosition += taskSize;
    }
    log.info("{} tasks have been created.", tasks.size());
    for (Task task : tasks) {
      log.info("{}", task);
    }
    return tasks.size();
  }

  private int calculateIssueAmount() throws CannotExtractIssuesException {
    final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    final AnonymousAuthenticationHandler handler = new AnonymousAuthenticationHandler();
    try (JiraRestClient rest = factory.create(configuration.getUrl(), handler)) {
      SearchRestClient search = rest.getSearchClient();
      return search.searchJql("project=" + configuration.getProject()).claim().getTotal();
    } catch (Exception e) {
      throw new CannotExtractIssuesException(e);
    }
  }

  @Override
  public void onSuccess(Task task) {
    issues.addAll(task.getIssues());
    int remain = this.taskRemain.decrementAndGet();
    log.info("Task {} has been done, {} task remained", task, remain);
    if (remain == 0) {
      stop();
      completion.release();
    }
  }

  @Override
  public void onFail(Task task) {
    if (task.canRetry()) {
      log.info("Error while executing {}", task);
      tasks.offer(task);
    } else {
      log.info("Critical error while executing {}", task);
      stop();
      completion.release();
    }
  }
}
