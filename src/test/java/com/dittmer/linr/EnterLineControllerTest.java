package com.dittmer.linr;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.dittmer.linr.App;
import com.dittmer.linr.EnterLineController;
import com.dittmer.linr.LineNote;
import com.itextpdf.text.pdf.parser.Line;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class EnterLineControllerTest
{
    @Test
    public void testSubmit()
    {
        new JFXPanel();
        EnterLineController elc = new EnterLineController();
        App.lineNotes = new ArrayList<LineNote>();
        App.lineNotes.add(new LineNote("Alexa", "1", 0, "", "", "", 0, false));
        elc.textActor = new TextField();
        elc.textScene = new TextField();
        elc.textPage = new TextField();
        elc.textLine = new TextArea();
        elc.textNotes = new TextArea();
        elc.comboAction = new ComboBox<String>();
        elc.textActor.setText("Test");
        elc.textScene.setText("Scene");
        elc.textPage.setText("1");
        elc.textLine.setText("Line");
        elc.textNotes.setText("Notes");
        elc.comboAction.setValue("Called Line");
        elc.submit();
        ArrayList<LineNote> lineNotes = App.lineNotes;
        boolean found = false;
        LineNote expected = new LineNote("Test", "Scene", 1, "Called Line", "Line", "Notes", 1, false);
        for(LineNote ln : lineNotes)
        {
            if(expected.equals(ln))
                found = true;
        }
        assertTrue(found);
    }
}
