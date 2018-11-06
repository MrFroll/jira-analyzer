package org.opengravity.jira.analyzer.extractors.http;

public interface CallBack {

  void onSuccess(Task task);

  void onFail(Task task);
}
