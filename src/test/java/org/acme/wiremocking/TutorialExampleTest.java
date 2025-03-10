package org.acme.wiremocking;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@WireMockTest
public class TutorialExampleTest {

    @Test
    void test_something_with_wiremock(WireMockRuntimeInfo wmRuntimeInfo) throws IOException, InterruptedException, URISyntaxException {

        // Instance DSL can be obtained from the runtime info parameter
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get("/instance-dsl").willReturn(ok()));

        // Info such as port numbers is also available
        int port = wmRuntimeInfo.getHttpPort();

        // Setup the WireMock mapping stub for the test
        stubFor(get("/my/resource")
                .withHeader("Content-Type", containing("xml"))
                .willReturn(ok()
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>SUCCESS</response>")));

        // Setup HTTP POST request (with HTTP Client embedded in Java 11+)
        final HttpClient client = HttpClient.newBuilder().build();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/my/resource"))
                .header("Content-Type", "text/xml")
                .GET().build();

        // Send the request and receive the response
        final HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verify the response (with AssertJ)
        assertThat(response.statusCode()).as("Wrong response status code").isEqualTo(200);
        assertThat(response.body()).as("Wrong response body").contains("<response>SUCCESS</response>");
    }
}