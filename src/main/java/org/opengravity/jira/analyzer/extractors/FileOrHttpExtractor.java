package org.opengravity.jira.analyzer.extractors;

import java.io.File;
import java.util.List;
import javax.inject.Inject;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.core.Extractor;
import org.opengravity.jira.analyzer.core.domain.Issue;
import org.opengravity.jira.analyzer.extractors.file.FileExtractor;
import org.opengravity.jira.analyzer.core.CannotExtractIssuesException;
import org.opengravity.jira.analyzer.extractors.http.HttpExtractor;

public class FileOrHttpExtractor implements Extractor {

  private final Extractor fileExtractor;
  private final Extractor httpExtractor;
  private final Configuration configuration;

  @Inject
  public FileOrHttpExtractor(Configuration configuration) {
    this.fileExtractor = new FileExtractor(configuration);
    this.httpExtractor = new HttpExtractor(configuration);
    this.configuration = configuration;
  }

  @Override
  public List<Issue> extract() throws CannotExtractIssuesException {
    final File file = new File(configuration.getDump());
    if (file.exists() && !file.isDirectory()) {
      return fileExtractor.extract();
    } else {
      return httpExtractor.extract();
    }
  }
}
