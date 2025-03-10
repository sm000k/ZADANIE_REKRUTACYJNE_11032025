package org.acme.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.exception.UserNotFoundException;
import org.acme.model.GitHubBranch;
import org.acme.model.GitHubRepo;
import org.acme.model.GitHubRepoWithBranches;
import org.acme.rest.GitHubClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GitHubService {

    private static final Logger LOGGER = Logger.getLogger(GitHubService.class);

    @Inject
    @RestClient
    GitHubClient gitHubClient;

    @Inject
    TokenManager tokenManager;

    public static class Repository {

        String name;

        public Repository(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Repository{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }


    public static class Branch {
        String name;

        public Branch(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Branch{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    static class GitHubClientLocal {

        Multi<Repository> getRepositories(String username) {
            return Multi.createFrom().iterable(Arrays.asList(new Repository("repository1"), new Repository("repository2"), new Repository("repository3")));
        }

        Multi<Branch> getBranches(Repository repository) {
            return Multi.createFrom().iterable(Arrays.asList(new Branch("branch1"), new Branch("branch2"), new Branch("branch3")));
        }
    }

    public static void main(String[] args) {

    }

  
 // Metoda zwracająca listę nazw repozytoriów bez użycia Mutiny
      public List<GitHubRepo> getRepositoriesSync(String username) {
         String authToken = tokenManager.getAuthToken();
         List<GitHubRepo> repos = gitHubClient.getRepositoriesSync(username, authToken);
 LOGGER.info("Repositories: " + repos);
         return repos;
     }

 // Metoda zwracająca listę nazw repozytoriów
 public Uni<List<String>> getRepositoryNames(String username) {
    String authToken = tokenManager.getAuthToken();
    return gitHubClient.getRepositories(username, authToken)
.onItem().invoke(repos -> LOGGER.info("Repositories: " + repos))
            .onItem().transform(repos -> repos.stream()
                    .map(GitHubRepo::getName)
                    .collect(Collectors.toList()))
                .onFailure().invoke(e -> LOGGER.error("Failed to get repositories for user: " + username, e));
}

// Metoda zwracająca listę repozytoriów z branchami
public Uni<List<GitHubRepoWithBranches>> getAllRepositoriesWithBranches(String username) {
    String authToken = tokenManager.getAuthToken();
    return gitHubClient.getRepositories(username, authToken)
        .onFailure().recoverWithUni(e -> {
            LOGGER.error("Failed to get repositories for user: " + username, e);
            throw new UserNotFoundException(404, "User not found");
        })
        .flatMap(repos -> {
            List<GitHubRepo> nonForkRepos = repos.stream()
                .filter(repo -> !repo.isFork())
                .sorted((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()))
                .collect(Collectors.toList());
            return Multi.createFrom().iterable(nonForkRepos)
                .onItem().transformToUniAndMerge(repo -> {
                    String repoName = repo.getName();
                    return gitHubClient.getBranches(username, repoName, authToken)
                        .onItem().transformToUni(branchList -> {
                            List<GitHubBranch> sortedBranches = branchList.stream()
                                .sorted((b1, b2) -> b1.getName().compareToIgnoreCase(b2.getName()))
                                .collect(Collectors.toList());
                            return Multi.createFrom().iterable(sortedBranches)
                                .onItem().transformToUniAndMerge(branch -> gitHubClient.getLastCommit(username, repoName, branch.getName(), authToken)
                                    .onItem().transform(commit -> {
                                        branch.setLastCommitSha(commit.getSha());
                                        return branch;
                                    }))
                                .collect().asList()
                                .onItem().transform(branchesWithCommits -> new GitHubRepoWithBranches(repoName, repo.getOwner().getLogin(), branchesWithCommits));
                        })
                        .onFailure().invoke(e -> LOGGER.error("Failed to get branches for repo: " + repoName, e));
                })
                .collect().asList()
                .onItem().transform(repoWithBranchesList -> repoWithBranchesList.stream()
                    .sorted((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()))
                    .map(repoWithBranches -> {
                        List<GitHubBranch> sortedBranches = repoWithBranches.getBranches().stream()
                            .sorted((b1, b2) -> b1.getName().compareToIgnoreCase(b2.getName()))
                            .collect(Collectors.toList());
                        return new GitHubRepoWithBranches(repoWithBranches.getName(), repoWithBranches.getOwnerLogin(), sortedBranches);
                    })
                    .collect(Collectors.toList()));
        });
}

}