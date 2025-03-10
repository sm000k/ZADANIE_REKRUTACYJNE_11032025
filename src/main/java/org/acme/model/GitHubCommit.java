package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubCommit {

    @JsonProperty("sha")
    private String sha;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public String toString() {
        return "GitHubCommit{" +
                "sha='" + sha + '\'' +
                '}';
    }
}
