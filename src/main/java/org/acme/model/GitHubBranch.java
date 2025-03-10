package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubBranch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("last_commit_sha")
    private String lastCommitSha;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }

    public void setLastCommitSha(String lastCommitSha) {
        this.lastCommitSha = lastCommitSha;
    }

    @Override
    public String toString() {
        return "GitHubBranch{" +
                "name='" + name + '\'' +
                ", lastCommitSha='" + lastCommitSha + '\'' +
                '}';
    }
}