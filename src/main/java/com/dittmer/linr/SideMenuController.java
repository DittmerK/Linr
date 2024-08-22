package com.dittmer.linr;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SideMenuController implements Initializable
{
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button buttonSave;
    @FXML
    public Button buttonExport;
    @FXML
    public ChoiceBox<Show> choiceShow;
    @FXML
    public Button buttonNewShow;
    @FXML
    public VBox vboxSave;
    @FXML 
    public VBox vboxExport;
    @FXML
    public VBox vboxActive;
    @FXML
    public VBox vboxNew;

    public void save()
    {
        Util.writeNotesSaveFile(App.currentShow);
    } 

    public void export()
    {
        Util.exportPDFs();
    }

    public void newShow()
    {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/newshow.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            Stage currStage = (Stage)buttonExport.getScene().getWindow();
            currStage.close();
                    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        choiceShow.getItems().addAll(App.shows);
        choiceShow.setValue(App.currentShow);
        choiceShow.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                App.currentShow = choiceShow.getItems().get((Integer) newValue);
            }
            
        });
    }
}
