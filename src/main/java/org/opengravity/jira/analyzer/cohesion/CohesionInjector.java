package org.opengravity.jira.analyzer.cohesion;

import com.google.inject.AbstractModule;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.core.Extractor;
import org.opengravity.jira.analyzer.extractors.FileOrHttpExtractor;
import org.opengravity.jira.analyzer.loader.DumpLoader;
import org.opengravity.jira.analyzer.core.IssueLoader;

public class CohesionInjector extends AbstractModule {

  private final Mode mode;
  private final Configuration config;

  public CohesionInjector(Mode mode, Configuration config) {
    this.mode = mode;
    this.config = config;
  }

  @Override
  protected void configure() {
    if (Mode.PROD == mode) {
      bind(Configuration.class).toInstance(config);
      bind(Extractor.class).to(FileOrHttpExtractor.class);
      bind(IssueLoader.class).to(DumpLoader.class);
    } else {
      throw new IllegalArgumentException("Not implemented yet");
    }
  }
}