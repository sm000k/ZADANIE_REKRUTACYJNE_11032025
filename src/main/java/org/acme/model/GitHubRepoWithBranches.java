package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GitHubRepoWithBranches {

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner_login")
    private String ownerLogin;

    @JsonProperty("branches")
    private List<GitHubBranch> branches;

    public GitHubRepoWithBranches(String name, String ownerLogin, List<GitHubBranch> branches) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
    }

    public String getName() {
        return name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public List<GitHubBranch> getBranches() {
        return branches;
    }

    @Override
    public String toString() {
        return "GitHubRepoWithBranches{" +
                "name='" + name + '\'' +
                ", ownerLogin='" + ownerLogin + '\'' +
                ", branches=" + branches +
                '}';
    }
}
