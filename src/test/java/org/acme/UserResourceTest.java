package org.acme;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkiverse.wiremock.devservice.WireMockConfigKey;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.GitHubRepoWithBranches;
import org.acme.service.GitHubService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;


@ConnectWireMock
@QuarkusTest
public class UserResourceTest {
    WireMock wiremock;
    // WireMockConfigKey is provided by the core module and does not require the `test` module dependency
    @ConfigProperty(name = WireMockConfigKey.PORT)
    Integer port; // the port WireMock server is listening on

    @Inject
    GitHubService gitHubService;

    @Test
    public void testFetchDataEndpoint() {

        System.setProperty("quarkus.rest-client.\"org.acme.rest.GitHubClient\".url", "http://localhost:" + port);

        wiremock.register(get(urlEqualTo("/users/sm000k/repos"))

                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(PAYLOAD)));

        given()
                .when().get("/github/sm000k/")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body(containsString("Complete-Python-3-Bootcamp"));
    }

    @Test
    public void testFetchDataEndpoint2() {

        System.setProperty("quarkus.rest-client.\"org.acme.rest.GitHubClient\".url", "http://localhost:" + port);

        wiremock.register(get(urlEqualTo("/users/sm000k/repos"))

                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(PAYLOAD)));

        given()
                .when().get("/github/sm000k/")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("find { it.name == 'Complete-Python-3-Bootcamp' }.owner.login", containsString("sm000k"))
                .body("find { it.name == 'FizzBuzzEnterpriseEdition' }.owner.login", containsString("sm000k"));
    }

    @Test
    public void testGetAllRepositoriesWithBranches() {

        System.setProperty("quarkus.rest-client.\"org.acme.rest.GitHubClient\".url", "http://localhost:" + port);

        // Create a modified payload where fork is set to false so it won't be filtered out
        String nonForkPayload = PAYLOAD.replace("\"fork\": true", "\"fork\": false");

        wiremock.register(get(urlEqualTo("/users/sm000k/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(nonForkPayload)));

        wiremock.register(get(urlPathMatching("/repos/sm000k/[^/]+/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\": \"main\"}, {\"name\": \"dev\"}]")));

        wiremock.register(get(urlPathMatching("/repos/sm000k/[^/]+/commits/[^/]+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sha\": \"abc123\"}")));

        List<GitHubRepoWithBranches> result = gitHubService.getAllRepositoriesWithBranches("sm000k").await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        GitHubRepoWithBranches repo1 = result.stream().filter(r -> r.getName().equals("Complete-Python-3-Bootcamp")).findFirst().orElse(null);
        assertThat(repo1).isNotNull();
        assertThat(repo1.getBranches()).hasSize(2);
        assertThat(repo1.getBranches().stream().filter(b -> b.getName().equals("main")).findFirst().orElse(null).getLastCommitSha()).isEqualTo("abc123");
        assertThat(repo1.getBranches().stream().filter(b -> b.getName().equals("dev")).findFirst().orElse(null).getLastCommitSha()).isEqualTo("abc123");

        GitHubRepoWithBranches repo2 = result.stream().filter(r -> r.getName().equals("FizzBuzzEnterpriseEdition")).findFirst().orElse(null);
        assertThat(repo2).isNotNull();
        assertThat(repo2.getBranches()).hasSize(2);
        assertThat(repo2.getBranches().stream().filter(b -> b.getName().equals("main")).findFirst().orElse(null).getLastCommitSha()).isEqualTo("abc123");
        assertThat(repo2.getBranches().stream().filter(b -> b.getName().equals("dev")).findFirst().orElse(null).getLastCommitSha()).isEqualTo("abc123");
    }

    
       private static final String PAYLOAD = """
            [
              {
                "id": 626686581,
                "node_id": "R_kgDOJVp6dQ",
                "name": "Complete-Python-3-Bootcamp",
                "full_name": "sm000k/Complete-Python-3-Bootcamp",
                "private": false,
                "owner": {
                  "login": "sm000k",
                  "id": 10740418,
                  "node_id": "MDQ6VXNlcjEwNzQwNDE4",
                  "avatar_url": "https://avatars.githubusercontent.com/u/10740418?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/sm000k",
                  "html_url": "https://github.com/sm000k",
                  "followers_url": "https://api.github.com/users/sm000k/followers",
                  "following_url": "https://api.github.com/users/sm000k/following{/other_user}",
                  "gists_url": "https://api.github.com/users/sm000k/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/sm000k/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/sm000k/subscriptions",
                  "organizations_url": "https://api.github.com/users/sm000k/orgs",
                  "repos_url": "https://api.github.com/users/sm000k/repos",
                  "events_url": "https://api.github.com/users/sm000k/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/sm000k/received_events",
                  "type": "User",
                  "user_view_type": "public",
                  "site_admin": false
                },
                "html_url": "https://github.com/sm000k/Complete-Python-3-Bootcamp",
                "description": "Course Files for Complete Python 3 Bootcamp Course on Udemy",
                "fork": true,
                "url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp",
                "forks_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/forks",
                "keys_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/keys{/key_id}",
                "collaborators_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/collaborators{/collaborator}",
                "teams_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/teams",
                "hooks_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/hooks",
                "issue_events_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/issues/events{/number}",
                "events_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/events",
                "assignees_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/assignees{/user}",
                "branches_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/branches{/branch}",
                "tags_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/tags",
                "blobs_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/git/blobs{/sha}",
                "git_tags_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/git/tags{/sha}",
                "git_refs_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/git/refs{/sha}",
                "trees_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/git/trees{/sha}",
                "statuses_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/statuses/{sha}",
                "languages_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/languages",
                "stargazers_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/stargazers",
                "contributors_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/contributors",
                "subscribers_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/subscribers",
                "subscription_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/subscription",
                "commits_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/commits{/sha}",
                "git_commits_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/git/commits{/sha}",
                "comments_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/comments{/number}",
                "issue_comment_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/issues/comments{/number}",
                "contents_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/contents/{+path}",
                "compare_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/compare/{base}...{head}",
                "merges_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/merges",
                "archive_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/{archive_format}{/ref}",
                "downloads_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/downloads",
                "issues_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/issues{/number}",
                "pulls_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/pulls{/number}",
                "milestones_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/milestones{/number}",
                "notifications_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/notifications{?since,all,participating}",
                "labels_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/labels{/name}",
                "releases_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/releases{/id}",
                "deployments_url": "https://api.github.com/repos/sm000k/Complete-Python-3-Bootcamp/deployments",
                "created_at": "2023-04-12T01:02:17Z",
                "updated_at": "2023-04-12T01:05:35Z",
                "pushed_at": "2023-04-11T10:59:47Z",
                "git_url": "git://github.com/sm000k/Complete-Python-3-Bootcamp.git",
                "ssh_url": "git@github.com:sm000k/Complete-Python-3-Bootcamp.git",
                "clone_url": "https://github.com/sm000k/Complete-Python-3-Bootcamp.git",
                "svn_url": "https://github.com/sm000k/Complete-Python-3-Bootcamp",
                "homepage": null,
                "size": 37125,
                "stargazers_count": 1,
                "watchers_count": 1,
                "language": null,
                "has_issues": false,
                "has_projects": true,
                "has_downloads": true,
                "has_wiki": true,
                "has_pages": false,
                "has_discussions": false,
                "forks_count": 0,
                "mirror_url": null,
                "archived": false,
                "disabled": false,
                "open_issues_count": 0,
                "license": null,
                "allow_forking": true,
                "is_template": false,
                "web_commit_signoff_required": false,
                "topics": [],
                "visibility": "public",
                "forks": 0,
                "open_issues": 0,
                "watchers": 1,
                "default_branch": "master"
              },
              {
                "id": 476339687,
                "node_id": "R_kgDOHGRd5w",
                "name": "FizzBuzzEnterpriseEdition",
                "full_name": "sm000k/FizzBuzzEnterpriseEdition",
                "private": false,
                "owner": {
                  "login": "sm000k",
                  "id": 10740418,
                  "node_id": "MDQ6VXNlcjEwNzQwNDE4",
                  "avatar_url": "https://avatars.githubusercontent.com/u/10740418?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/sm000k",
                  "html_url": "https://github.com/sm000k",
                  "followers_url": "https://api.github.com/users/sm000k/followers",
                  "following_url": "https://api.github.com/users/sm000k/following{/other_user}",
                  "gists_url": "https://api.github.com/users/sm000k/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/sm000k/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/sm000k/subscriptions",
                  "organizations_url": "https://api.github.com/users/sm000k/orgs",
                  "repos_url": "https://api.github.com/users/sm000k/repos",
                  "events_url": "https://api.github.com/users/sm000k/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/sm000k/received_events",
                  "type": "User",
                  "user_view_type": "public",
                  "site_admin": false
                },
                "html_url": "https://github.com/sm000k/FizzBuzzEnterpriseEdition",
                "description": "FizzBuzz Enterprise Edition is a no-nonsense implementation of FizzBuzz made by serious businessmen for serious business purposes.",
                "fork": true,
                "url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition",
                "forks_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/forks",
                "keys_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/keys{/key_id}",
                "collaborators_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/collaborators{/collaborator}",
                "teams_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/teams",
                "hooks_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/hooks",
                "issue_events_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/issues/events{/number}",
                "events_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/events",
                "assignees_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/assignees{/user}",
                "branches_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/branches{/branch}",
                "tags_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/tags",
                "blobs_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/git/blobs{/sha}",
                "git_tags_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/git/tags{/sha}",
                "git_refs_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/git/refs{/sha}",
                "trees_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/git/trees{/sha}",
                "statuses_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/statuses/{sha}",
                "languages_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/languages",
                "stargazers_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/stargazers",
                "contributors_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/contributors",
                "subscribers_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/subscribers",
                "subscription_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/subscription",
                "commits_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/commits{/sha}",
                "git_commits_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/git/commits{/sha}",
                "comments_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/comments{/number}",
                "issue_comment_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/issues/comments{/number}",
                "contents_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/contents/{+path}",
                "compare_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/compare/{base}...{head}",
                "merges_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/merges",
                "archive_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/{archive_format}{/ref}",
                "downloads_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/downloads",
                "issues_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/issues{/number}",
                "pulls_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/pulls{/number}",
                "milestones_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/milestones{/number}",
                "notifications_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/notifications{?since,all,participating}",
                "labels_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/labels{/name}",
                "releases_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/releases{/id}",
                "deployments_url": "https://api.github.com/repos/sm000k/FizzBuzzEnterpriseEdition/deployments",
                "created_at": "2022-03-31T14:22:16Z",
                "updated_at": "2023-09-29T14:56:06Z",
                "pushed_at": "2022-03-21T18:23:04Z",
                "git_url": "git://github.com/sm000k/FizzBuzzEnterpriseEdition.git",
                "ssh_url": "git@github.com:sm000k/FizzBuzzEnterpriseEdition.git",
                "clone_url": "https://github.com/sm000k/FizzBuzzEnterpriseEdition.git",
                "svn_url": "https://github.com/sm000k/FizzBuzzEnterpriseEdition",
                "homepage": "",
                "size": 340,
                "stargazers_count": 1,
                "watchers_count": 1,
                "language": null,
                "has_issues": false,
                "has_projects": true,
                "has_downloads": true,
                "has_wiki": true,
                "has_pages": false,
                "has_discussions": false,
                "forks_count": 0,
                "mirror_url": null,
                "archived": false,
                "disabled": false,
                "open_issues_count": 0,
                "license": null,
                "allow_forking": true,
                "is_template": false,
                "web_commit_signoff_required": false,
                "topics": [],
                "visibility": "public",
                "forks": 0,
                "open_issues": 0,
                "watchers": 1,
                "default_branch": "uinverse"
              }
            ]""";
    
    
}