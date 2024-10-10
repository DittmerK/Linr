package com.dittmer.linrboot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.update4j.Configuration;
import org.update4j.service.Delegate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application implements Delegate
{

    public static String config_url = "https://github.com/DittmerK/Linr/blob/main/linr/stable.xml";

    @Override
    public void main(List<String> args) throws Throwable 
    {
        if(args != null && args.size() > 0)
            config_url = args.get(0);
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        primaryStage.setMinWidth(650);
		primaryStage.setMinHeight(500);

		URL configUrl = new URI(config_url).toURL();
		Configuration config = null;
		try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8)) {
			config = Configuration.read(in);
		} catch (IOException e) {
			System.err.println("Could not load remote config, falling back to local.");
			try (Reader in = Files.newBufferedReader(Paths.get("business/config.xml"))) {
				config = Configuration.read(in);
			}
		}

		StartupView startup = new StartupView(config);

		Scene scene = new Scene(startup);

		primaryStage.setScene(scene);

		primaryStage.setTitle("Update4j Demo Launcher");
		primaryStage.show();
    }
    
}
