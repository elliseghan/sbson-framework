package ca.concordia.cs.aseg.sbson.core;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Utils {
    public static String MAVEN_INDEX_LOCATION, LOCAL_MAVEN_REPO, TRIPLES_LOCATION, POM_DUMP_LOCATION, JAR_DUMP_LOCATION;

    public static void initProperties() {
        Properties prop;
        try {
            prop = new Properties();
            String propFileName = "src/main/resources/sbson-core.properties";
            InputStream inputStream = new FileInputStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            }
            Utils.MAVEN_INDEX_LOCATION = prop.getProperty("MAVEN_INDEX_LOCATION");
            Utils.LOCAL_MAVEN_REPO = prop.getProperty("LOCAL_MAVEN_REPO");
            Utils.TRIPLES_LOCATION = prop.getProperty("TRIPLES_LOCATION");
            Utils.POM_DUMP_LOCATION = Utils.MAVEN_INDEX_LOCATION + "Maven Repo/";
            Utils.JAR_DUMP_LOCATION = Utils.POM_DUMP_LOCATION;
        } catch (FileNotFoundException e) {
            System.out.println("property file not found in resource folder");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLongestCommonPrefix(List<String> strings) {

        if (strings.size() == 0) {
            return ""; // Or maybe return null?
        }

        for (int prefixLen = 0; prefixLen < strings.get(0).length(); prefixLen++) {
            char c = strings.get(0).charAt(prefixLen);
            for (int i = 1; i < strings.size(); i++) {
                if (prefixLen >= strings.get(i).length() || strings.get(i).charAt(prefixLen) != c) {
                    // Mismatch found
                    return strings.get(i).substring(0, prefixLen);
                }
            }
        }
        return strings.get(0);

    }

    public static String[] createUrl(String value, String type, String split) {
        String[] result = new String[2];
        String str = "";

        if (type.equals("pom") || type.equals("jar")) {
            result[1]=createDirectoryStructure(value, split)+ "." + type;
            str = "https://repo1.maven.org/maven2/" + result[1];

        } else if(type.equals("sources.jar")){
            result[1]=createDirectoryStructure(value, split)+ "-" + type;
            str = "https://repo1.maven.org/maven2/" + result[1];
        }else {

            String[] gav = value.split(split);
            str = "http://search.maven.org/solrsearch/select?q=g:%22" + gav[0] + "%22+AND+a:%22" + gav[1]
                    + "%22+AND+v:%22" + gav[2] + "%22&core=gav&rows=20&wt=xml";
        }
        result[0] = str;
        return result;
    }

    public static String[] createUrl(String value, String type, String split, String saveDir) {
        String[] result = createUrl(value, type, split);
        result[1] = saveDir + result[1];
        return result;
    }

    public static boolean getFileFromURL(String url, String saveName, boolean append) {
        boolean success = false;
        URL website;
        try {
            File file = new File(saveName);
            System.out.println(file.getAbsolutePath());
            if (file.exists()){
                Date lastModified = new Date(file.lastModified());
                Date today = new Date(System.currentTimeMillis());
                if(DateUtils.isSameDay(lastModified,today)==true){
                    return true;
                }
            }
            website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File parentDir = file.getParentFile();
            if (parentDir != null)
                parentDir.mkdirs();

            FileOutputStream fos = new FileOutputStream(saveName, append);

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            success = true;
            Thread.sleep(1000);
        } catch (Exception e) {
            if (e instanceof java.net.UnknownHostException || e instanceof FileNotFoundException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        return success;
    }
    public static boolean getFileFromURL(String url, String saveName) {
        boolean success = false;
        URL website;
        try {
            File file = new File(saveName);
            if (file.exists()) {
                return true;
            }
            website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File parentDir = file.getParentFile();
            if (parentDir != null)
                parentDir.mkdirs();

            FileOutputStream fos = new FileOutputStream(saveName, false);

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            success = true;
            Thread.sleep(1000);
        } catch (Exception e) {
            if (e instanceof java.net.UnknownHostException || e instanceof FileNotFoundException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static String[] getGAVFromFileName(String path) {
        return getGAVFromFileName(path, Utils.POM_DUMP_LOCATION);
    }

    public static String[] getGAVFromFileName(String path, String basePath) {
        path = path.replace("\\", "/");
        basePath = basePath.replace("\\", "/");
        if (path.startsWith(basePath)) {
            String[] gav = new String[3];
            path = path.replace(basePath, "");

            int pos = path.lastIndexOf("/");
            path = path.substring(0, pos);

            pos = path.lastIndexOf("/");
            gav[2] = path.substring(pos + 1);

            path = path.substring(0, pos);
            pos = path.lastIndexOf("/");
            gav[1] = path.substring(pos + 1);

            pos = path.lastIndexOf("/");
            path = path.substring(0, pos);
            path = path.replace("/", ".");
            gav[0] = path;
            return gav;
        } else {
            return null;
        }
    }

    public static File getFileFromGAV(String G, String A, String V, String type) {
        String str = createDirectoryStructure(G, A, V);
        return new File(Utils.POM_DUMP_LOCATION + str + "." + type);
    }

    public static File getFileFromGAV(String GAV, String split, String type) {
        String str = createDirectoryStructure(GAV, split);
        return new File(Utils.POM_DUMP_LOCATION + str + "." + type);
    }

    public static File getArtifactFromRepo(String GAV, String type, String split, String saveName) {
        String[] urls = createUrl(GAV, type, split);
        if (getFileFromURL(urls[0], saveName) == true) {
            return new File(saveName);
        } else {
            return null;
        }
    }

    private static String createDirectoryStructure(String GAV, String split) {
        String[] gav = GAV.split(split);
        return createDirectoryStructure(gav[0], gav[1], gav[2]);
    }

    private static String createDirectoryStructure(String G, String A, String V) {
        String str = "/" + A + "-" + V;
        G = G.replace(".", "/");
        str = G + "/" + A + "/" + V + str;
        return str;
    }

    public static File getJarFromURI(String uri) {
        return getJarFromShortURI(shortenURI(uri));
    }

    public static File getJarFromShortURI(String uri) {

        File jarFile = null;
        String localLocation = getJarLocFromShortURI(uri);
        if (localLocation != null) {
            jarFile = new File(localLocation);
        } else {
            String[] jarURLs = createUrl(uri, "jar", ":");
            System.out.println(jarURLs[0]);
            String fileSaveName = JAR_DUMP_LOCATION + jarURLs[1];
            boolean fileGrab = getFileFromURL(jarURLs[0], fileSaveName);
            if (fileGrab) {
                jarFile = new File(fileSaveName);
            }
        }
        return jarFile;
    }

    public static String getJarLocFromLongURI(String uri) {
        return getJarLocFromShortURI(shortenURI(uri));
    }

    public static String getJarLocFromShortURI(String uri) {
        String jarFile = null;
        String[] jarURLs = createUrl(uri, "jar", ":");
        // System.out.println(jarURLs[0]);
        // System.out.println(jarURLs[1]);
        String fileSaveName = JAR_DUMP_LOCATION + jarURLs[1];
        if (new File(fileSaveName).exists()) {
            jarFile = fileSaveName;
        }
        return jarFile;
    }

    public static String shortenURI(String longURI) {
        longURI = longURI.replace('<', ' ');
        longURI = longURI.replace('>', ' ');
        int position = longURI.indexOf("#");
        return longURI.substring(position + 1).trim();
    }

    public static String extendURI(String shortURI, boolean domainLevel) {
        if (domainLevel)
            return "http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + shortURI;
        else
            return "http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#" + shortURI;
    }

    public static String getTagValueFromXML(File xmlFile, String tagName)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        Element rootElement = document.getDocumentElement();
        NodeList list = rootElement.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
}
