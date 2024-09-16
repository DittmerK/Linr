package com.dittmer.linr;


import java.net.URL;
import java.util.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application{
    
    public static String settingsFileString = "usersettings.lnr";
    public static String saveFileString = "shows.lnr";

    public static ArrayList<Show> shows;
    public static Show currentShow;
    
    public Scene scene;


    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Linr");
        stage.setResizable(false);
        if(currentShow == null || App.shows.isEmpty())
        {
            URL fxmlURL = App.class.getResource("fxml/newShow.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
            stage.show();
        }
        else
        {
            URL fxmlURL = App.class.getResource("fxml/enterline.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
            stage.show();
            if(UserSettings.getName() == null)
            {
                loader = new FXMLLoader(App.class.getResource("fxml/settings.fxml"));
                root = loader.load();
                stage = new Stage();
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
                stage.show();
            }
        }
    }

    public static void main(String[] args)
    {
        Util.loadSave(settingsFileString, saveFileString);
        launch(args);
    }
    

    @Override
    public void stop() 
    {
        Util.writeSaveFiles();
        
    }

    public static void reset() 
    {
        App.shows = null;
        App.currentShow = null;
    }

    
}
