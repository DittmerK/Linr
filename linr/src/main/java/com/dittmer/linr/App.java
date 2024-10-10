package com.dittmer.linr;


import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.dittmer.linr.objects.Show;
import com.dittmer.linr.objects.UserSettings;
import com.dittmer.linr.util.AutoUpdater;
import com.dittmer.linr.util.Util;

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
            URL fxmlURL = App.class.getResource("/com/dittmer/linr/fxml/newShow.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image(App.class.getResourceAsStream("/com/dittmer/linr/icons/dittmer.png")));
            stage.show();
        }
        else
        {
            URL fxmlURL = App.class.getResource("fxml/enterline.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.getIcons().add(new Image(App.class.getResourceAsStream("/com/dittmer/linr/icons/dittmer.png")));
            stage.show();
            if(UserSettings.getName() == null)
            {
                loader = new FXMLLoader(App.class.getResource("/com/dittmer/linr/fxml/settings.fxml"));
                root = loader.load();
                stage = new Stage();
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image(App.class.getResourceAsStream("/com/dittmer/linr/icons/dittmer.png")));
                stage.show();
            }
        }
    }
    

    @Override
    public void stop() 
    {
        Util.writeSaveFiles();
        try 
        {
            if(AutoUpdater.requiresUpdate())
                AutoUpdater.performUpdate();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void reset() 
    {
        App.shows = null;
        App.currentShow = null;
    }

    
}
