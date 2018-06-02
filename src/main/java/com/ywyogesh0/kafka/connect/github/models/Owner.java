package com.ywyogesh0.kafka.connect.github.models;

import org.json.JSONObject;

import static com.ywyogesh0.kafka.connect.github.GitHubRepoSourceSchemas.*;

public class Owner {

    private String url;
    private String htmlUrl;
    private Integer id;
    private String login;
    private String type;

    public Owner() {
    }

    public Owner(String url, String htmlUrl, Integer id, String login, String type) {
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.id = id;
        this.login = login;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Owner withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Owner withHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Owner withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Owner withLogin(String login) {
        this.login = login;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Owner withType(String type) {
        this.type = type;
        return this;
    }

    public static Owner fromJSON(JSONObject jsonObject) {
        return new Owner()
                .withUrl(jsonObject.getString(OWNER_URL_FIELD))
                .withHtmlUrl(jsonObject.getString(OWNER_HTML_URL_FIELD))
                .withId(jsonObject.getInt(OWNER_ID_FIELD))
                .withLogin(jsonObject.getString(OWNER_LOGIN_FIELD))
                .withType(jsonObject.getString(OWNER_TYPE_FIELD));
    }
}
