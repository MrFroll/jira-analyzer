package org.opengravity.jira.analyzer.extractors.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.api.Extractor;
import org.opengravity.jira.analyzer.domain.Issue;
import org.opengravity.jira.analyzer.extractors.http.CannotExtractIssuesException;

public class FileExtractor implements Extractor {

  private final Configuration configuration;

  public FileExtractor(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public List<Issue> extract() throws CannotExtractIssuesException {
    try {
      final File file = new File(configuration.getDump());
      if (file.exists() && !file.isDirectory()) {
        final String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        final Type type = new TypeToken<List<Issue>>() {
        }.getType();
        return new Gson().fromJson(data, type);
      }
    } catch (IOException e) {
      throw new CannotExtractIssuesException(e);
    }
    return new ArrayList<>();
  }
}
