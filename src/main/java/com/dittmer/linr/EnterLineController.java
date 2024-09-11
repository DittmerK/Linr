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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
        menuHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> 
        {
            burgerTask1.setRate(burgerTask1.getRate() * -1);
            burgerTask1.play();
            if(menuDrawer.isOpened())
                menuDrawer.close();
            else
                menuDrawer.open();
        });
        

        ObservableList<String> actions = FXCollections.observableArrayList(LineNote.actions);
        comboAction.setItems(actions);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
      
            if (text.matches("\\d?")) 
            { // this is the important line
                return change;
            }
             
            return null;
        };
        textPage.setTextFormatter(new TextFormatter<String>(filter));

        textActor.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textScene.requestFocus();
            }
        });

        textScene.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                if(evt.isShiftDown())
                    textActor.requestFocus();
                else
                    textPage.requestFocus();            
            }
        });

        textPage.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                if(evt.isShiftDown())
                    textScene.requestFocus();
                else
                    comboAction.requestFocus();
            }
        });

        comboAction.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                evt.consume();
                if(evt.isShiftDown())
                    textPage.requestFocus();
                else
                    textLine.requestFocus();
            } 
            else if (evt.getCode() == KeyCode.UP || evt.getCode() == KeyCode.DOWN) 
            {
                evt.consume();
            }
        });

        textLine.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB)){
                if(evt.isShiftDown())
                    comboAction.requestFocus();
                else
                    textNotes.requestFocus();
            }
        });
        
    }

    public void submit()
    {

        Actor actor = App.currentShow.getActor(Util.scrubLeadingAndTrailingSpace(textActor.getText()));
        if(actor != null)
        {
            LineNote ln = new LineNote(actor, Util.scrubLeadingAndTrailingSpace(textScene.getText()), Integer.parseInt(Util.scrubLeadingAndTrailingSpace(textPage.getText())), comboAction.getValue(), Util.scrubLeadingAndTrailingSpace(textLine.getText()), Util.scrubLeadingAndTrailingSpace(textNotes.getText()), 1, false);
        
            //Check for duplicate
            ArrayList<LineNote> lineNotes = App.currentShow.lineNotes;
            ArrayList<LineNote> possibleDupes = new ArrayList<LineNote>();
            for(int i = 0; i < lineNotes.size(); i++)
            {
                LineNote ln2 = lineNotes.get(i);
                String lnline = ln.line.toLowerCase();
                String ln2line = ln2.line.toLowerCase();
                if(ln2.actor.equals(ln.actor) && ln2.scene.equals(ln.scene) && ln2.page == ln.page && ln2.action.equals(ln.action) && (ln2line.equals(lnline) || ln2line.contains(lnline) || lnline.contains(ln2line) || Util.similarity(lnline, ln2line) > .8))
                {
                    possibleDupes.add(ln2);
                
                }
            }
            if(possibleDupes.size() > 0)
            {
                Parent root;
                try {
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/duplicate.fxml"));
                    DuplicateController dupeController = new DuplicateController();
                    loader.setController(dupeController);
                    root = loader.load();
                    Stage stage = new Stage();
                    stage.setResizable(false);
                    stage.setTitle("Duplicate Note");
                    stage.setScene(new Scene(root, 450, 300));
                    stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
                    stage.show();
                    dupeController.setArgs(ln, possibleDupes);
                    
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                lineNotes.add(ln);
            }
        }

        textActor.setText("");
        textScene.setText("");
        textPage.setText("");
        comboAction.setValue(null);
        textLine.setText("");
        textNotes.setText("");
    }
    
    
}
