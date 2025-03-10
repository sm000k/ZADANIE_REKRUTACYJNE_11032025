package org.acme.rest;

import io.smallrye.mutiny.Uni;
import org.acme.model.GitHubBranch;
import org.acme.model.GitHubRepo;
import org.acme.model.GitHubCommit;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@RegisterRestClient(baseUri = "https://api.github.com")
public interface GitHubClient {

    @GET
    @Path("/users/{username}/repos")
    List<GitHubRepo> getRepositoriesSync(@PathParam("username") String username);
    @GET
    @Path("/users/{username}/repos")
    Uni<List<GitHubRepo>> getRepositories(@PathParam("username") String username);
    
    @GET
    @Path("/repos/{username}/{repo}/branches")
    Uni<List<GitHubBranch>> getBranches(@PathParam("username") String username, @PathParam("repo") String repo);

    @GET
    @Path("/repos/{username}/{repo}/commits/{branch}")
    @Produces("application/json")
    Uni<GitHubCommit> getLastCommit(@PathParam("username") String username, @PathParam("repo") String repo, @PathParam("branch") String branch);
}