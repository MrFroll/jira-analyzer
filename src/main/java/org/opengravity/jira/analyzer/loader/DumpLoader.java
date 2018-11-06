package org.opengravity.jira.analyzer.loader;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.domain.Issue;

public class DumpLoader implements IssueLoader {

  public final Configuration config;

  @Inject
  public DumpLoader(Configuration configuration) {
    this.config = configuration;
  }

  @Override
  public void load(List<Issue> issues) throws CannotLoadException {
    final File file = new File(config.getDump());
    final String data = new Gson().toJson(issues);
    try {
      FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new CannotLoadException(e);
    }
  }
}
