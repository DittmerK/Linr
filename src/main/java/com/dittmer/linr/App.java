package com.dittmer.linr;


import java.net.URL;
import java.util.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application{
    
    public static String saveFileString = "shows.lnr";

    public static ArrayList<Show> shows;
    public static Show currentShow;
    
    public Scene scene;


    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Linr");
        stage.setResizable(false);
        if(currentShow == null)
        {
            URL fxmlURL = App.class.getResource("fxml/newShow.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else
        {
            URL fxmlURL = App.class.getResource("fxml/enterline.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args)
    {
        Util.loadSave(saveFileString);
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
