package org.opengravity.cohesion.extractor;

public interface CallBack {

  void onSuccess(Task task);

  void onFail(Task task);
}
