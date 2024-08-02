package com.dittmer.linr;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SideMenuController 
{
    @FXML
    public Button buttonSave;

    @FXML
    public Button buttonExport;

    public void save()
    {
        Util.writeSaveFile("notes.csv");
    } 

    public void export()
    {
        Util.exportPDFs();
    }
}
