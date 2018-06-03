package com.ywyogesh0.kafka.connect.github;

import com.ywyogesh0.kafka.connect.github.validators.BatchSizeValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;

public class GitHubRepoSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG = "topic.name";
    private static final String TOPIC_CONFIG_DOC = "topic name";

    public static final String USER_CONFIG = "github.user";
    private static final String USER_CONFIG_DOC = "User of the repository";

    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_CONFIG_DOC = "Number of repositories to retrieve at a time. " +
            "Defaults to 100 (max value)";

    public static final String AUTH_USERNAME_CONFIG = "auth.username";
    private static final String AUTH_USERNAME_CONFIG_DOC = "Optional Username to authenticate calls";

    public static final String AUTH_PASSWORD_CONFIG = "auth.password";
    private static final String AUTH_PASSWORD_CONFIG_DOC = "Optional Password to authenticate calls";

    public GitHubRepoSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public GitHubRepoSourceConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_CONFIG_DOC)
                .define(USER_CONFIG, Type.STRING, Importance.HIGH, USER_CONFIG_DOC)
                .define(BATCH_SIZE_CONFIG, Type.INT, 5, new BatchSizeValidator(),
                        Importance.LOW, BATCH_SIZE_CONFIG_DOC)
                .define(AUTH_USERNAME_CONFIG, Type.STRING, "", Importance.LOW, AUTH_USERNAME_CONFIG_DOC)
                .define(AUTH_PASSWORD_CONFIG, Type.STRING, "", Importance.LOW, AUTH_PASSWORD_CONFIG_DOC);
    }

    public String getTopicConfig() {
        return this.getString(TOPIC_CONFIG);
    }

    public String getUserConfig() {
        return this.getString(USER_CONFIG);
    }

    public Integer getBatchSizeConfig() {
        return this.getInt(BATCH_SIZE_CONFIG);
    }

    public String getAuthUsernameConfig() {
        return this.getString(AUTH_USERNAME_CONFIG);
    }

    public String getAuthPasswordConfig() {
        return this.getPassword(AUTH_PASSWORD_CONFIG).value();
    }
}
