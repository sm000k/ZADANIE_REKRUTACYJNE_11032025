package org.acme.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("fork")
    private boolean fork;

    private List<GitHubBranch> branches;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public List<GitHubBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<GitHubBranch> branches) {
        this.branches = branches;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {

        @JsonProperty("login")
        private String login;

        // Getters and setters

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        @Override
        public String toString() {
            return "Owner{login='" + login + "'}";
        }
    }

    @Override
    public String toString() {
        return "\nGitHubRepo{name='" + name + "', owner=" + owner + ", fork=" + fork + ", branches=" + branches + '}';
    }
}
