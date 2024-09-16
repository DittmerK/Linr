package com.dittmer.linr;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AlertController 
{
    @FXML
    public Button buttonPass;
    @FXML
    public Text text;

    public void okay()
    {
        Stage currStage = (Stage)text.getScene().getWindow();
        currStage.close();
    }
}
