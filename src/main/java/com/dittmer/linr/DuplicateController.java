package com.dittmer.linr;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DuplicateController
{

    public LineNote ln;
    public ArrayList<LineNote> possibleDupes;
    public int currentIndex = 0;

    @FXML
    public Text textOldLine;
    @FXML
    public Text textOldNote;
    @FXML
    public Text textNewLine;
    @FXML
    public Text textNewNote;
    @FXML
    public Button buttonMerge;
    @FXML
    public Button buttonNext;

    public void setArgs(LineNote ln, ArrayList<LineNote> possibleDupes)
    {
        this.ln = ln;
        this.possibleDupes = possibleDupes;

        if(!possibleDupes.isEmpty())
        {
            textOldLine.setText(ln.line);
            textOldNote.setText(ln.notes);
            textNewLine.setText(possibleDupes.get(0).line);
            textNewNote.setText(possibleDupes.get(0).notes);
            if(possibleDupes.size() == 1)
            {
                buttonNext.setText("Save");
            }
        }
    }

    public void next()
    {
        if(currentIndex == possibleDupes.size()-1)
        {
            App.lineNotes.add(ln);
            ((Stage)buttonMerge.getScene().getWindow()).close();
        }
        else
        {
            currentIndex++;
            textNewLine.setText(possibleDupes.get(currentIndex).line);
            textNewNote.setText(possibleDupes.get(currentIndex).notes);
            if(possibleDupes.size() == currentIndex + 1)
            {
                buttonNext.setText("Save");
            }
        }
    }

    public void merge()
    {
        for(LineNote ln : App.lineNotes)
        {
            if(ln.equals(possibleDupes.get(currentIndex)))
            {
                ln.occurences++;
                ln.fixed = false;
            }
        }
        ((Stage)buttonMerge.getScene().getWindow()).close();
    }

}
