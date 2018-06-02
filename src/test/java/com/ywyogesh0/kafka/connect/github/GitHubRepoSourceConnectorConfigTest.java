package com.ywyogesh0.kafka.connect.github;

import org.junit.Test;

public class GitHubRepoSourceConnectorConfigTest {
  @Test
  public void doc() {
    System.out.println(GitHubRepoSourceConnectorConfig.conf().toRst());
  }
}