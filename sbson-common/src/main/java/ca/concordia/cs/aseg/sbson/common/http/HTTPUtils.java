package ca.concordia.cs.aseg.sbson.common.http;



import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HTTPUtils {

    public static HttpResponse sendHTTPRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        /*HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());
        return response;*/

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return response;
    }

    public static HttpResponse sendHTTPRequest(String url, Path savePath) throws IOException, InterruptedException, URISyntaxException {
       /* HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        HttpResponse<Path> response = client.send(httpRequest, HttpResponse.BodyHandler.asFile(savePath));
        return response;*/

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return response;
    }
}
