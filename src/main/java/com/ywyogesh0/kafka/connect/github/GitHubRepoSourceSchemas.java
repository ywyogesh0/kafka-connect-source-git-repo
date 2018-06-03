package com.ywyogesh0.kafka.connect.github;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class GitHubRepoSourceSchemas {

    public static String NEXT_PAGE_FIELD = "next_page";

    // Repo Top-Level Fields
    public static String ID_FIELD = "id";
    public static String NAME_FIELD = "name";
    public static String FULL_NAME_FIELD = "full_name";
    public static String DESCRIPTION_FIELD = "description";
    public static String URL_FIELD = "url";
    public static String HTML_URL_FIELD = "html_url";
    public static String CREATED_AT_FIELD = "created_at";
    public static String UPDATED_AT_FIELD = "updated_at";

    // Owner Fields
    public static String OWNER_URL_FIELD = "url";
    public static String OWNER_HTML_URL_FIELD = "html_url";
    public static String OWNER_ID_FIELD = "id";
    public static String OWNER_LOGIN_FIELD = "login";
    public static String OWNER_TYPE_FIELD = "type";

    // Schema Names
    public static String KEY_SCHEMA_VALUE = "list-repo";
    public static String OWNER_SCHEMA_VALUE = "owner";
    public static String REPO_SCHEMA_VALUE = "repo";

    // Key Schema
    public static Schema KEY_SCHEMA = SchemaBuilder.struct().name(KEY_SCHEMA_VALUE)
            .version(1)

            .field(ID_FIELD, Schema.INT32_SCHEMA)
            .field(NAME_FIELD, Schema.STRING_SCHEMA)

            .build();

    // Owner Schema
    public static Schema OWNER_SCHEMA = SchemaBuilder.struct().name(OWNER_SCHEMA_VALUE)
            .version(1)

            .field(OWNER_URL_FIELD, Schema.STRING_SCHEMA)
            .field(OWNER_HTML_URL_FIELD, Schema.STRING_SCHEMA)
            .field(OWNER_ID_FIELD, Schema.INT32_SCHEMA)
            .field(OWNER_LOGIN_FIELD, Schema.STRING_SCHEMA)
            .field(OWNER_TYPE_FIELD, Schema.STRING_SCHEMA)

            .build();

    public static Schema REPO_SCHEMA = SchemaBuilder.struct().name(REPO_SCHEMA_VALUE)
            .version(1)

            .field(ID_FIELD, Schema.STRING_SCHEMA)
            .field(NAME_FIELD, Schema.STRING_SCHEMA)
            .field(FULL_NAME_FIELD, Schema.STRING_SCHEMA)
            .field(DESCRIPTION_FIELD, Schema.STRING_SCHEMA)
            .field(URL_FIELD, Schema.STRING_SCHEMA)
            .field(HTML_URL_FIELD, Schema.STRING_SCHEMA)

            .field(CREATED_AT_FIELD, Schema.INT64_SCHEMA)
            .field(UPDATED_AT_FIELD, Schema.INT64_SCHEMA)

            .field(OWNER_SCHEMA_VALUE, OWNER_SCHEMA) // mandatory

            .build();
}
