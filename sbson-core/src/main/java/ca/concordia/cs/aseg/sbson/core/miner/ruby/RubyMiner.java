package ca.concordia.cs.aseg.sbson.core.miner.ruby;


import ca.concordia.cs.aseg.sbson.common.http.HTTPUtils;
import jdk.incubator.http.HttpResponse;
import org.json.*;

import java.io.BufferedReader;
import java.nio.channels.UnresolvedAddressException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RubyMiner {

    private static int requestCount;

    private static String getBaseUrlAllVersions(String name) {
        return "https://rubygems.org/api/v1/versions/" + name + ".json";
    }

    private static String getBaseUrlVersionDetails(String name, String version) {
        return "https://rubygems.org/api/v2/rubygems/" + name + "/versions/" + version + ".json";
    }

    public static void main(String[] args) {
        requestCount = 0;
        List<String> list = new ArrayList<>();
        String fileName = "sbson-core\\src\\main\\resources\\gem-names.csv";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            // br returns as stream and convert it into a List
            list = br.lines().collect(Collectors.toList());
            Collections.sort(list);

        } catch (Exception e) {
            e.printStackTrace();
        }

        list.forEach((name) -> getVersionsFromName(name));
    }

    private static void getVersionsFromName(String name) {
        name = name.replace("\"", "");
        System.out.println(new Date(System.currentTimeMillis()).toGMTString() + " - Working on: " + name);
        String url = getBaseUrlAllVersions(name);
        try {
            if (requestCount % 10 == 0) {
                Thread.sleep(2000);
                requestCount++;
            }
            HttpResponse response = HTTPUtils.sendHTTPRequest(url);
            if (response.statusCode() == 200) {
                // System.out.println(response.body());
                JSONArray jsonarray = new JSONArray(response.body().toString());
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonObject = jsonarray.getJSONObject(i);
                    String version = jsonObject.getString("number");
                    getFullGemDetails(name, version);
                }
            }
        } catch (UnresolvedAddressException e) {
            System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - Unresolved Address Exception at: " + url);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - HTTP request (GEM versions) failed: " + url);
            e.printStackTrace();
        }
    }

    private static void getFullGemDetails(String name, String version) {
        // System.out.println(name + ": " + version);
        String url = getBaseUrlVersionDetails(name, version);
        try {
            String directory = "E:/DEVELOPMENT/RUBYGEMS/Repo/" + name + "/" + version;
            String filename = name + "-" + version + ".json";
            Path tempFile = null;
            if (directory == null) {
                directory = Paths.get(".").toAbsolutePath().normalize().toString();
            }
            tempFile = Paths.get(directory + "/" + filename);

            if (tempFile != null) // null will be returned if the path has no parent
                Files.createDirectories(tempFile.getParent());

            if (!Files.exists(tempFile)) {
                Files.createFile(tempFile);
                if (requestCount % 10 == 0) {
                    Thread.sleep(2000);
                    requestCount++;
                }
                HttpResponse response = HTTPUtils.sendHTTPRequest(url, tempFile);
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                } else {
                    System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - Bad status code: " + url);
                }
            }
        } catch (UnresolvedAddressException e) {
            System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - Unresolved Address Exception at: " + url);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - HTTP request (GEM details) failed: " + url);
            e.printStackTrace();
        }
    }


}
