package com.ywyogesh0.kafka.connect.github;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubRepoSourceConnector extends SourceConnector {

    private static Logger log = LoggerFactory.getLogger(GitHubRepoSourceConnector.class);
    private GitHubRepoSourceConnectorConfig config;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        log.info("GitHubRepoSourceConnector Started...");
        config = new GitHubRepoSourceConnectorConfig(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GitHubRepoSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        List<Map<String, String>> taskConfigs = new ArrayList<>(1);
        taskConfigs.add(config.originalsStrings());

        return taskConfigs;
    }

    @Override
    public void stop() {
        log.info("GitHubRepoSourceConnector Stopped...");
    }

    @Override
    public ConfigDef config() {
        return GitHubRepoSourceConnectorConfig.conf();
    }
}
