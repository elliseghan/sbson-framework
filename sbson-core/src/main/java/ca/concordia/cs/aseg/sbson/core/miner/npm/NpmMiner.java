package ca.concordia.cs.aseg.sbson.core.miner.npm;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NpmMiner {
    private static int requestCount;
    private static String directory = "E:/DEVELOPMENT/NPM/Registry";

    public static void main(String[] args) {
        requestCount = 0;
        List<String> list = new ArrayList<>();
        String fileName = "D:\\npm-follower\\registry.log";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            String data = "";
            while ((line = br.readLine()) != null) {

                if (line.contains("registry -")) {
                    if (!data.trim().isEmpty()) {
                        // System.out.println("data: " + data);
                        //save node details to file
                        saveNodeToFile(data, directory);
                    }
                    requestCount++;
                    data = line.substring(line.lastIndexOf("registry - ") + 11).trim();
                } else {
                    data += line;
                }
                /*if (requestCount == 10)
                    break;*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveNodeToFile(String nodeData, String directory) {
        String name;
        try {
            JSONObject jsonObject = new JSONObject(nodeData);
            name = jsonObject.getString("_id");
            System.out.println(new Date(System.currentTimeMillis()).toGMTString() + " - Working on: " + name);
            try {
                String filename = name + ".json";
                Path tempFile = null;
                if (directory == null) {
                    directory = Paths.get(".").toAbsolutePath().normalize().toString();
                }
                tempFile = Paths.get(directory + "/" + filename);

                if (tempFile != null) // null will be returned if the path has no parent
                    Files.createDirectories(tempFile.getParent());

                if (!Files.exists(tempFile)) {
                    Files.createFile(tempFile);
                    Files.write(tempFile, nodeData.getBytes());
                }
            } catch (Exception e) {
                System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - Saving to file failed: " + name);
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(new Date(System.currentTimeMillis()).toGMTString() + " - Getting name from data failed: " + nodeData);
            e.printStackTrace();
        }

    }
}
