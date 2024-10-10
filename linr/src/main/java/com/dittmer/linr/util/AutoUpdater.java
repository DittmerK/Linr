package com.dittmer.linr.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.InputStreamReader;

import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.OS;

public class AutoUpdater 
{
    private static final String DEV_UPDATE_CONFIG = "https://github.com/DittmerK/Linr/blob/main/linr/dev.xml";
    private static final String STABLE_UPDATE_CONFIG = "https://github.com/DittmerK/Linr/blob/main/linr/stable.xml";
    public static final String LOCAL_UPDATE_CONFIG = "update.xml";
    private static Configuration configuration = null;

    public static boolean requiresUpdate() throws IOException
    {
        Configuration read = getConfig();
        return read.requiresUpdate();
    }

    private static Configuration getConfig() throws MalformedURLException, IOException 
    {
        if(configuration != null)
            return configuration;

        Reader reader = new InputStreamReader(URI.create(STABLE_UPDATE_CONFIG).toURL().openStream());
        configuration = Configuration.read(reader);
        reader.close();
        return configuration;
    }

    //TODO: Other operating systems
    public static void performUpdate() throws IOException 
    {
        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "..\\runtime\\bin\\java", "-jar linrboot.jar", STABLE_UPDATE_CONFIG});
        System.exit(0);
    }

    //Generate
    private static void generateConfig() throws IOException {
        File imageDir = new File("linr\\build\\jpackage\\Linr\\app");
        imageDir.mkdir();
        Configuration build = Configuration.builder()
                .baseUri("https://github.com/DittmerK/Linr/blob/main/releases/")
                .basePath(imageDir.getAbsolutePath())
                .files(FileMetadata.streamDirectory(imageDir.getAbsolutePath())
                        .peek(r -> r.classpath(!r.getSource().toString().endsWith(".jar")).modulepath(r.getSource().toString().endsWith(".jar")).ignoreBootConflict()))
                .build();

                Configuration config = Configuration.builder()
                .baseUri("https://github.com/DittmerK/Linr/blob/main/releases/1.0/")
                .basePath("${user.dir}")
                .file(FileMetadata.readFrom(imageDir + "/angus-activation-2.0.2.jar")
                                .uri(mavenUrl("org.eclipse.angus", "angus-activation", "2.0.2"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/angus-mail-2.0.3.jar")
                                .uri(mavenUrl("org.eclipse.angus", "angus-mail", "2.0.3"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/commons-lang3-3.16.0.jar")
                                .uri(mavenUrl("org.apache.commons", "commons-lang3", "3.16.0"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/commons-text-1.12.0.jar")
                                .uri(mavenUrl("org.apache.commons", "commons-text", "1.12.0"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/commons-logging-1.3.0.jar")
                                .uri(mavenUrl("commons-logging", "commons-logging", "1.3.0"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/fontbox-3.0.2.jar")
                                .uri(mavenUrl("org.apache.pdfbox", "fontbox", "3.0.2"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/jakarta.activation-api-2.1.3.jar")
                                .uri(mavenUrl("jakarta.activation", "jakarta.activation-api", "2.1.3"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/jakarta.mail-api-2.1.3.jar")
                                .uri(mavenUrl("jakarta.mail", "jakarta.mail-api", "2.1.3"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-base-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-base", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-controls-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-controls", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-fxml-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-fxml", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-graphics-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-graphics", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-media-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-media", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/javafx-swing-22.0.1-win.jar")
                                .uri(mavenUrl("org.openjfx", "javafx-swing", "22.0.1"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/jfoenix-9.0.10.jar")
                                .uri(mavenUrl("com.jfoenix", "jfoenix", "9.0.10"))
                                .classpath())    
                .file(FileMetadata.readFrom(imageDir + "/linr-1.0.jar")
                                .uri("Linr-1.0.jar")
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/Linr.cfg")
                                .uri("Linr.cfg")
                                .classpath())        
                .file(FileMetadata.readFrom(imageDir + "/pdfbox-3.0.2.jar")
                                .uri(mavenUrl("org.apache.pdfbox", "pdfbox", "3.0.2"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/pdfbox-io-3.0.2.jar")
                                .uri(mavenUrl("org.apache.pdfbox", "pdfbox-io", "3.0.2"))
                                .classpath())
                .file(FileMetadata.readFrom(imageDir + "/update4j-1.5.9.jar")
                                .uri(mavenUrl("org.update4j", "update4j", "1.5.9"))
                                .classpath())
                .property("maven.central", MAVEN_BASE)
                .build();


        FileWriter writer = new FileWriter("files.xml");
        build.write(writer);
        writer.flush();
        writer.close();
        writer = new FileWriter("stable.xml");
        config.write(writer);
        writer.flush();
        writer.close();
    }

    private static final String MAVEN_BASE = "https://repo1.maven.org/maven2";

    private static String mavenUrl(String groupId, String artifactId, String version, OS os) {
        StringBuilder builder = new StringBuilder();
        builder.append(MAVEN_BASE + '/');
        builder.append(groupId.replace('.', '/') + "/");
        builder.append(artifactId + "/");
        builder.append(version + "/");
        builder.append(artifactId + "-" + version);

        if (os != null) {
            builder.append('-' + os.getShortName());
        }

        builder.append(".jar");

        return builder.toString();
    }

    private static String mavenUrl(String groupId, String artifactId, String version) {
        return mavenUrl(groupId, artifactId, version, null);
    }

    public static void main(String[] args) throws Exception {
        generateConfig();
    }
}
