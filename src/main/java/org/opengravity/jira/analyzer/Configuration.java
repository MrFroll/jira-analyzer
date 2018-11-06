package org.opengravity.jira.analyzer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class Configuration {

  private final URI url;
  private final Integer taskSize;
  private final String project;
  private final long schedulerDelay;
  private final int retries;
  private final String dump;
  private final String graphMl;

  public Configuration(Properties properties) throws URISyntaxException {
    this.url = new URI(properties.getProperty("jira.url"));
    this.taskSize = Integer.valueOf(properties.getProperty("task.size"));
    this.project = properties.getProperty("jira.project");
    this.schedulerDelay = Long.valueOf(properties.getProperty("task.scheduler.delay"));
    this.retries = Integer.valueOf(properties.getProperty("task.retry"));
    this.dump = properties.getProperty("result.issues.dump");
    this.graphMl = properties.getProperty("result.graphml");
  }

  public int getTaskSize() {
    return taskSize;
  }

  public String getProject() {
    return project;
  }

  public int getRetries() {
    return retries;
  }

  public URI getUrl() {
    return url;
  }

  public long getSchedulerDelay() {
    return schedulerDelay;
  }

  public String getDump() {
    return dump;
  }

  public String getGraphMl() {
    return graphMl;
  }
}
