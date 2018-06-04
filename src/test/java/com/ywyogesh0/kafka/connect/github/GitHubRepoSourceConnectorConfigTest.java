package com.ywyogesh0.kafka.connect.github;

import com.ywyogesh0.kafka.connect.github.connectors.GitHubRepoSourceConnectorConfig;
import org.junit.Test;

public class GitHubRepoSourceConnectorConfigTest {
  @Test
  public void doc() {
    System.out.println(GitHubRepoSourceConnectorConfig.conf().toRst());
  }
}