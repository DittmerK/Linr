package com.dittmer.linrboot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.update4j.Configuration;
import org.update4j.service.Delegate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application implements Delegate
{

    public static String config_url = "https://github.com/DittmerK/Linr/raw/refs/heads/main/stable.xml";

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

        SSLContext sslc = SSLContext.getInstance("TLSv1.2");
        sslc.init(null, null, null);
        HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).sslContext(sslc).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(config_url)).build();
        HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

		Configuration config = null;

		try (Reader in = new InputStreamReader(response.body(), StandardCharsets.UTF_8)) 
        {
			config = Configuration.read(in);
		} 
        catch (IOException e) 
        {
			System.out.println("Could not load remote config, falling back to local.");
            e.printStackTrace();
			try (Reader in = Files.newBufferedReader(Paths.get("business/config.xml"))) 
            {
				config = Configuration.read(in);
			}
		}

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/dittmer/linrboot/fxml/StartupView.fxml"));
        StartupView svController = new StartupView();
        loader.setController(svController);
        Parent root = loader.load();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Linr Updater");
        primaryStage.setScene(new Scene(root));
        //primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/com/dittmer/linr/icons/dittmer.png")));
        primaryStage.show();
        svController.setConfig(config);
    }
    
}
