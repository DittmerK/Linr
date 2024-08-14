package com.dittmer.linr;


import java.net.URL;
import java.util.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application{
    
    public static ArrayList<LineNote> lineNotes;
    
    public Scene scene;


    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Linr");

        URL fxmlURL = App.class.getResource("fxml/enterline.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        //Initialize arraylist
        lineNotes = new ArrayList<>();

        Util.loadSaveFile("notes.lnr");
        launch(args);
    }
    
    @Override
    public void stop() 
    {
        Util.writeSaveFile("notes.lnr");
        
    }

    
}
