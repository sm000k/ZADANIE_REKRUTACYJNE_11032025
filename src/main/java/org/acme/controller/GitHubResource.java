package org.acme.controller;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.GitHubRepo;
import org.acme.model.GitHubRepoWithBranches;
import org.acme.service.GitHubService;

import java.util.Arrays;
import java.util.List;



@Path("/github")
public class GitHubResource {

    @Inject
    GitHubService gitHubService;

        @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GitHubRepo> fetchRepos(@PathParam("username") String username) {
        return gitHubService.getRepositoriesSync(username);
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> fetchData() {
        List<String> data = Arrays.asList("ONE", "TWO", "THREE");
        return data;
    }

    @GET
    @Path("/repos/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<String>> getRepositoryNames(@PathParam("username") String username) {
        return gitHubService.getRepositoryNames(username);
    }
    @GET
@Path("/repositories-with-branches/{username}")
@Produces(MediaType.APPLICATION_JSON)
public Uni<List<GitHubRepoWithBranches>> getAllRepositoriesWithBranches(@PathParam("username") String username) {
    return gitHubService.getAllRepositoriesWithBranches(username);
}

}