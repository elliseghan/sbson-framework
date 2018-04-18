package ca.concordia.cs.aseg.sbson.core.miner.ruby;


import ca.concordia.cs.aseg.sbson.common.http.HTTPUtils;

import org.apache.http.HttpResponse;
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

        for (String name : list) {
            getVersionsFromName(name);
        }
    }

    private static void getVersionsFromName(String name) {



            name = name.replace("\"", "");
        if (name.compareTo("gibbon") > 0) {
            System.out.println(new Date(System.currentTimeMillis()).toString() + " - Working on: " + name);
            String url = getBaseUrlAllVersions(name);
            try {
                if (requestCount % 10 == 0) {
                    Thread.sleep(1500);
                    requestCount++;
                }
                HttpResponse response = HTTPUtils.sendHTTPRequest(url);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // System.out.println(response.body());
                    JSONArray jsonarray = new JSONArray(response.getEntity());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonObject = jsonarray.getJSONObject(i);
                        String version = jsonObject.getString("number");
                        getFullGemDetails(name, version);
                    }
                }
            } catch (UnresolvedAddressException e) {
                System.err.println(new Date(System.currentTimeMillis()).toString() + " - Unresolved Address Exception at: " + url);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println(new Date(System.currentTimeMillis()).toString() + " - HTTP request (GEM versions) failed: " + url);
                e.printStackTrace();
            }
        }
    }

    private static void getFullGemDetails(String name, String version) {
        // System.out.println(name + ": " + version);
        String url = getBaseUrlVersionDetails(name, version);
        try {
            String directory = "D:/DEVELOPMENT/RUBYGEMS/Repo/" + name + "/" + version;
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
                    Thread.sleep(1500);
                    requestCount++;
                }
                HttpResponse response = HTTPUtils.sendHTTPRequest(url, tempFile);
                if (response.getStatusLine().getStatusCode() == 200) {
                    System.out.println(response.getEntity());
                } else {
                    System.err.println(new Date(System.currentTimeMillis()).toString() + " - Bad status code: " + url);
                }
            }
        } catch (UnresolvedAddressException e) {
            System.err.println(new Date(System.currentTimeMillis()).toString() + " - Unresolved Address Exception at: " + url);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(new Date(System.currentTimeMillis()).toString() + " - HTTP request (GEM details) failed: " + url);
            e.printStackTrace();
        }
    }


}
