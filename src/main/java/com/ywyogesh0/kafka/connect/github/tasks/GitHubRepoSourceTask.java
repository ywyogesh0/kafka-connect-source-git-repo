package com.ywyogesh0.kafka.connect.github.tasks;

import com.ywyogesh0.kafka.connect.github.versions.VersionUtil;
import com.ywyogesh0.kafka.connect.github.clients.GitHubRepoAPIHttpClient;
import com.ywyogesh0.kafka.connect.github.connectors.GitHubRepoSourceConnectorConfig;
import com.ywyogesh0.kafka.connect.github.models.Owner;
import com.ywyogesh0.kafka.connect.github.models.Repo;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ywyogesh0.kafka.connect.github.schemas.GitHubRepoSourceSchemas.*;


public class GitHubRepoSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(GitHubRepoSourceTask.class);
    public GitHubRepoSourceConnectorConfig config;

    protected Instant nextQuerySince;
    protected Instant lastUpdatedAt;

    protected Integer nextPageToVisit = 1;

    GitHubRepoAPIHttpClient gitHubHttpAPIClient;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {

        // Required to start your tasks. This could be open a connection to a database etc...
        config = new GitHubRepoSourceConnectorConfig(map);
        initializeLastVariables();
        gitHubHttpAPIClient = new GitHubRepoAPIHttpClient(config);
    }

    private void initializeLastVariables() {

        Map<String, Object> lastSourceOffset;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());

        if (lastSourceOffset != null) {

            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            Object nextPage = lastSourceOffset.get(NEXT_PAGE_FIELD);

            if (updatedAt != null && (updatedAt instanceof String)) {
                nextQuerySince = Instant.parse((String) updatedAt).plusSeconds(1);
                log.info("Next Query Since = " + nextQuerySince);
            }

            if (nextPage != null && (nextPage instanceof String)) {
                nextPageToVisit = Integer.valueOf((String) nextPage);
                log.info("Next Page To Visit = " + nextPageToVisit);
            }
        } else {
            log.info(" Initializing GitHubRepoSource Connector First Time...");
        }
    }


    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        gitHubHttpAPIClient.sleepIfNeed();

        // Fetch data
        final List<SourceRecord> records = new ArrayList<>();
        JSONArray issues = gitHubHttpAPIClient.getNextIssues(nextPageToVisit, nextQuerySince);

        // Count Results
        int i = 0;
        for (Object obj : issues) {

            Repo repo = Repo.fromJSON((JSONObject) obj);
            SourceRecord sourceRecord = generateSourceRecord(repo);
            records.add(sourceRecord);

            i += 1;
            lastUpdatedAt = repo.getUpdatedAt();
        }

        log.info(String.format("Fetched %s record(s)...", i));

        // All Records are fetched...
        if (i == config.getBatchSizeConfig()) {

            // We have reached a full batch, we need to get the next one
            nextPageToVisit += 1;

        } else if (i != 0) {

            // Resuming...
            nextQuerySince = lastUpdatedAt.plusSeconds(1);
            nextPageToVisit = 1;

            gitHubHttpAPIClient.sleep();
        }

        return records;
    }

    private SourceRecord generateSourceRecord(Repo repo) {
        return new SourceRecord(
                sourcePartition(),
                sourceOffset(repo.getUpdatedAt()),

                config.getTopicConfig(),

                null, // Partition will be inferred by the Framework

                KEY_SCHEMA,
                buildRecordKey(repo),

                REPO_SCHEMA,
                buildRecordValue(repo),

                repo.getUpdatedAt().toEpochMilli());
    }

    @Override
    public void stop() {
    }

    // Partition - Combination of github.user and topic.name
    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(GitHubRepoSourceConnectorConfig.USER_CONFIG, config.getUserConfig());
        map.put(GitHubRepoSourceConnectorConfig.TOPIC_CONFIG, config.getTopicConfig());
        return map;
    }

    // Offset - Combination of Updated TimeStamp and Next Page
    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, updatedAt.toString());
        map.put(NEXT_PAGE_FIELD, nextPageToVisit.toString());
        return map;
    }

    private Struct buildRecordKey(Repo repo) {
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put(ID_FIELD, repo.getId())
                .put(NAME_FIELD, repo.getName());

        return key;
    }

    private Struct buildRecordValue(Repo repo) {

        // Repo top-level fields
        Struct valueStruct = new Struct(REPO_SCHEMA)
                .put(ID_FIELD, repo.getId())
                .put(NAME_FIELD, repo.getName())
                .put(FULL_NAME_FIELD, repo.getFullName())
                .put(DESCRIPTION_FIELD, repo.getDescription())
                .put(URL_FIELD, repo.getUrl())
                .put(HTML_URL_FIELD, repo.getHtmlUrl())
                .put(CREATED_AT_FIELD, repo.getCreatedAt().toEpochMilli())
                .put(UPDATED_AT_FIELD, repo.getUpdatedAt().toEpochMilli());

        // Owner is mandatory
        Owner owner = repo.getOwner();

        Struct ownerStruct = new Struct(OWNER_SCHEMA)
                .put(OWNER_URL_FIELD, owner.getUrl())
                .put(OWNER_HTML_URL_FIELD, owner.getHtmlUrl())
                .put(OWNER_ID_FIELD, owner.getId())
                .put(OWNER_LOGIN_FIELD, owner.getLogin())
                .put(OWNER_TYPE_FIELD, owner.getType());

        valueStruct.put(OWNER_SCHEMA_VALUE, ownerStruct);

        return valueStruct;
    }

}