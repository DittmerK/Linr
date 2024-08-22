package com.dittmer.linr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class EnterLineControllerTest
{
    @Test
    public void testSubmit()
    {
        new JFXPanel();
        EnterLineController elc = new EnterLineController();
        App.currentShow = new Show();
        App.currentShow.cast = new ArrayList<Actor>();
        App.currentShow.cast.add(new Actor("Alexa", "alexa@email.com"));
        App.currentShow.cast.add(new Actor("Test", "test@email.com"));
        App.currentShow.lineNotes = new ArrayList<LineNote>();
        App.currentShow.lineNotes.add(new LineNote(App.currentShow.getActor("Alexa"), "1", 0, "", "", "", 0, false));
        elc.textActor = new TextField();
        elc.textScene = new TextField();
        elc.textPage = new TextField();
        elc.textLine = new TextArea();
        elc.textNotes = new TextArea();
        elc.choiceAction = new ChoiceBox<String>();
        elc.textActor.setText("Test");
        elc.textScene.setText("Scene");
        elc.textPage.setText("1");
        elc.textLine.setText("Line");
        elc.textNotes.setText("Notes");
        elc.choiceAction.setValue("Called Line");
        elc.submit();
        ArrayList<LineNote> lineNotes = App.currentShow.lineNotes;
        boolean found = false;
        LineNote expected = new LineNote(App.currentShow.getActor("Test"), "Scene", 1, "Called Line", "Line", "Notes", 1, false);
        for(LineNote ln : lineNotes)
        {
            if(expected.equals(ln))
                found = true;
        }
        assertTrue(found);
    }
}
