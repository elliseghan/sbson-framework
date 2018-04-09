package ca.concordia.cs.aseg.sbson.common.http;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HTTPUtils {

    public static HttpResponse sendHTTPRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());
        return response;
    }

    public static HttpResponse sendHTTPRequest(String url, Path savePath) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        HttpResponse<Path> response = client.send(httpRequest, HttpResponse.BodyHandler.asFile(savePath));
        return response;
    }
}
