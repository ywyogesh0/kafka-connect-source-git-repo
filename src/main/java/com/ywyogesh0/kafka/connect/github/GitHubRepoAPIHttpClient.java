package com.ywyogesh0.kafka.connect.github;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.apache.kafka.connect.errors.ConnectException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * GitHubRepoAPIHttpClient used to launch HTTP Get requests
 */
public class GitHubRepoAPIHttpClient {

    private static final String API_URL = "https://api.github.com/users/%s/repos?" +
            "page=%s&per_page=%s&type=all&direction=asc&sort=updated";

    private static final Logger log = LoggerFactory.getLogger(GitHubRepoAPIHttpClient.class);

    // HTTP Header Names
    private static final String X_RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    private static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    // HTTP requests optimization
    private Integer XRateLimit = 9999;
    private Integer XRateRemaining = 9999;
    private long XRateReset = Instant.MAX.getEpochSecond();

    GitHubRepoSourceConnectorConfig config;

    public GitHubRepoAPIHttpClient(GitHubRepoSourceConnectorConfig config) {
        this.config = config;
    }

    protected JSONArray getNextIssues(Integer page) throws InterruptedException {

        HttpResponse<JsonNode> jsonResponse;

        try {
            jsonResponse = getNextIssuesAPI(page);

            // Deal with headers in any case
            Headers headers = jsonResponse.getHeaders();

            XRateLimit = Integer.valueOf(headers.getFirst(X_RATE_LIMIT_LIMIT));
            XRateRemaining = Integer.valueOf(headers.getFirst(X_RATE_LIMIT_REMAINING));
            XRateReset = Integer.valueOf(headers.getFirst(X_RATE_LIMIT_RESET));

            switch (jsonResponse.getStatus()) {

                case 200:
                    return jsonResponse.getBody().getArray();

                case 401:
                    throw new ConnectException("Bad GitHub credentials provided, please edit configurations...");

                case 403:
                    // We have issued too many requests...

                    log.info(jsonResponse.getBody().getObject().getString("message"));

                    log.info(String.format("Your rate limit is %s", XRateLimit));
                    log.info(String.format("Your remaining calls are %s", XRateRemaining));
                    log.info(String.format("The limit will reset at %s",
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(XRateReset), ZoneOffset.systemDefault())));

                    long sleepTime = XRateReset - Instant.now().getEpochSecond();
                    log.info(String.format("Sleeping for %s seconds...", sleepTime));
                    Thread.sleep(1000 * sleepTime);

                    return getNextIssues(page);

                default:
                    log.error(constructUrl(page));
                    log.error(String.valueOf(jsonResponse.getStatus()));
                    log.error(jsonResponse.getBody().toString());
                    log.error(jsonResponse.getHeaders().toString());
                    log.error("Unknown error: Sleeping 5 seconds " +
                            "before re-trying...");

                    Thread.sleep(5000);
                    return getNextIssues(page);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Thread.sleep(5000L);

            return new JSONArray();
        }
    }

    protected HttpResponse<JsonNode> getNextIssuesAPI(Integer page) throws UnirestException {
        GetRequest uniRest = Unirest.get(constructUrl(page));

        if (!config.getAuthUsernameConfig().isEmpty() && !config.getAuthPasswordConfig().isEmpty()) {
            uniRest = uniRest.basicAuth(config.getAuthUsernameConfig(), config.getAuthPasswordConfig());
        }

        log.debug(String.format("GET %s", uniRest.getUrl()));
        return uniRest.asJson();
    }

    protected String constructUrl(Integer page) {
        return String.format(
                API_URL,
                config.getUserConfig(),
                page,
                config.getBatchSizeConfig());
    }

    public void sleep() throws InterruptedException {
        long sleepTime = (long) Math.ceil(
                (double) (XRateReset - Instant.now().getEpochSecond()) / XRateRemaining);
        log.debug(String.format("Sleeping for %s seconds", sleepTime));
        Thread.sleep(1000 * sleepTime);
    }

    public void sleepIfNeed() throws InterruptedException {
        // Sleep if needed
        if (XRateRemaining <= 10 && XRateRemaining > 0) {
            log.info(String.format("Approaching limit soon, you have %s requests left", XRateRemaining));
            sleep();
        }
    }
}