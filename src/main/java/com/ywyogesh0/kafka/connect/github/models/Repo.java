package com.ywyogesh0.kafka.connect.github.models;

import org.json.JSONObject;

import java.time.Instant;

import static com.ywyogesh0.kafka.connect.github.GitHubRepoSourceSchemas.*;

public class Repo {

    private Integer id;
    private String name;
    private String fullName;
    private String description;
    private String url;
    private String htmlUrl;
    private Instant createdAt;
    private Instant updatedAt;

    private Owner owner;

    public Repo() {
    }

    public Repo(Integer id, String name, String fullName, String description, String url, String htmlUrl,
                Instant createdAt, Instant updatedAt, Owner owner) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public static Repo fromJSON(JSONObject jsonObject) {
        Repo repo = new Repo();

        repo.setId(jsonObject.getInt(ID_FIELD));
        repo.setName(jsonObject.getString(NAME_FIELD));
        repo.setFullName(jsonObject.getString(FULL_NAME_FIELD));

        Object desc = jsonObject.get(DESCRIPTION_FIELD);
        repo.setDescription(!jsonObject.NULL.equals(desc) ? (String) desc : "");

        repo.setUrl(jsonObject.getString(URL_FIELD));
        repo.setHtmlUrl(jsonObject.getString(HTML_URL_FIELD));

        repo.setCreatedAt(Instant.parse(jsonObject.getString(CREATED_AT_FIELD)));
        repo.setUpdatedAt(Instant.parse(jsonObject.getString(UPDATED_AT_FIELD)));

        repo.setOwner(Owner.fromJSON(jsonObject.getJSONObject(OWNER_SCHEMA_VALUE)));

        return repo;
    }
}
