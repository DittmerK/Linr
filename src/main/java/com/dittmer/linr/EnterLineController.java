package com.dittmer.linr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;

public class EnterLineController implements Initializable
{
    @FXML
    public AnchorPane rootPane;
    @FXML
    public JFXHamburger menuHamburger;
    @FXML
    public JFXDrawer menuDrawer;
    @FXML
    public TextField textActor;
    @FXML
    public TextField textScene;
    @FXML
    public TextField textPage;
    @FXML
    public ComboBox<String> comboAction;
    @FXML
    public TextArea textLine;
    @FXML
    public TextArea textNotes;
    @FXML
    public Button buttonSubmit;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try
        {
            AnchorPane sidePanel = FXMLLoader.load(getClass().getResource("fxml/sidemenu.fxml"));
            menuDrawer.setSidePane(sidePanel);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        HamburgerBasicCloseTransition burgerTask1 = new HamburgerBasicCloseTransition(menuHamburger);
        burgerTask1.setRate(-1);
        menuHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            burgerTask1.setRate(burgerTask1.getRate() * -1);
            burgerTask1.play();
            if(menuDrawer.isOpened())
            {
                System.out.println("Closing");
                menuDrawer.close();
            }
            else
            {
                System.out.println("Opening");
                menuDrawer.open();
            }
        });

        ObservableList<String> actions = FXCollections.observableArrayList(LineNote.actions);
        comboAction.setItems(actions);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
      
            if (text.matches("\\d?")) { // this is the important line
                return change;
            }
             
            return null;
        };
        textPage.setTextFormatter(new TextFormatter<String>(filter));

        textActor.setOnKeyPressed( evt ->{
            if(evt.getCode().equals(KeyCode.TAB)){
                textScene.requestFocus();
            }
        });

        textScene.setOnKeyPressed( evt ->{
            if(evt.getCode().equals(KeyCode.TAB)){
                textPage.requestFocus();
            }
        });

        textPage.setOnKeyPressed( evt ->{
            if(evt.getCode().equals(KeyCode.TAB)){
                textLine.requestFocus();
            }
        });

        textLine.setOnKeyPressed( evt ->{
            if(evt.getCode().equals(KeyCode.TAB)){
                textNotes.requestFocus();
            }
        });
        
    }

    public void submit()
    {
        LineNote ln = new LineNote(textActor.getText(), textScene.getText(), Integer.parseInt(textPage.getText()), comboAction.getValue(), textLine.getText(), textNotes.getText(), 1, false);
        
        //Check for duplicate
        boolean foundDupe = false;
        ArrayList<LineNote> lineNotes = App.lineNotes;
        for(LineNote ln2 : lineNotes)
        {
            if(ln2.actor.equals(ln.actor) && ln2.line.equals(ln.line))
            {
                foundDupe = true;
                break;
            }
        }
        if(foundDupe)
        {
            //Launch dupe window
        } 
        else
        {
            lineNotes.add(ln);
        }

        textActor.setText("");
        textScene.setText("");
        textPage.setText("");
        comboAction.setValue("Action");
        textLine.setText("");
        textNotes.setText("");
    }
    
    
}
