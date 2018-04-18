package ca.concordia.cs.aseg.sbson.core.miner.npm;


import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NpmMiner {
    private static String directory = "D:/DEVELOPMENT/NPM/Registry";
    public static void main(String[] args) {
        String registryURl ="https://replicate.npmjs.com/_changes?feed=continuous&include_docs=true";
        String fileName = "C:\\Users\\e_eghan\\Desktop\\npm-follower\\registry.log";
        createLocalRegistryCopy(registryURl,fileName);
        parseRegistry(fileName);

    }

    private static void createLocalRegistryCopy(String urlString,String saveLocation){
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            FileWriter fileWriter = new FileWriter(saveLocation,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String line;
            while((line=bufferedReader.readLine())!=null){
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void parseRegistry(String registryLocation){
        try (BufferedReader br = Files.newBufferedReader(Paths.get(registryLocation))) {
            String line;
            //String data = "";
            while ((line = br.readLine()) != null) {
                //if (line.contains("registry -")) {
                    //if (!data.trim().isEmpty()) {
                        saveNodeToFile(line, directory);
                   // }
                    //data = line.substring(line.lastIndexOf("registry - ") + 11).trim();
               // } else {
                  //  data += line;
              //  }
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
            System.out.println(new Date(System.currentTimeMillis()).toString() + " - Working on: " + name);
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
                }else{
                    System.err.println(new Date(System.currentTimeMillis()).toString() + " - File already exists: " + name);
                }
            } catch (Exception e) {
                System.err.println(new Date(System.currentTimeMillis()).toString() + " - Saving to file failed: " + name);
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(new Date(System.currentTimeMillis()).toString() + " - Getting name from data failed: " + nodeData);
            e.printStackTrace();
        }

    }
}
