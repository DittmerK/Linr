package com.dittmer.linr;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class UserSettingsController implements Initializable
{
    @FXML
    public TabPane tabRoot;
    @FXML
    public TextField textName;
    @FXML
    public TextArea textBody;
    @FXML
    public TextField textServer;
    @FXML
    public TextField textPort;
    @FXML
    public TextField textUser;
    @FXML
    public PasswordField textPass;

    public void save()
    {
        UserSettings.setName(textName.getText());
        UserSettings.setMessageBody(swapVariables(textBody.getText()));
        UserSettings.setMailServer(textServer.getText());
        UserSettings.setMailPort(textPort.getText());
        UserSettings.setMailUser(textUser.getText());
        UserSettings.setMailPass(textPass.getText());

        ((Stage)tabRoot.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
      
            if (text.matches("\\d?")) 
            { // this is the important line
                return change;
            } else if(text.equals(UserSettings.getMailPort()))
            {
                return change;
            }
             
            return null;
        };
        textPort.setTextFormatter(new TextFormatter<String>(filter));

        textName.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textBody.requestFocus();
            }
        });

        textBody.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textName.requestFocus();
            }
        });

        textServer.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textPort.requestFocus();
            }
        });

        textPort.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textUser.requestFocus();
            }
        });

        textUser.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textPass.requestFocus();
            }
        });

        textPass.setOnKeyPressed( evt ->
        {
            if(evt.getCode().equals(KeyCode.TAB))
            {
                textServer.requestFocus();
            }
        });

        if(UserSettings.getName() != null)
            textName.setText(UserSettings.getName());
        if(UserSettings.getMessageBody() != null)
            textBody.setText(swapVariables(UserSettings.getMessageBody()));
        if(UserSettings.getMailServer() != null)
            textServer.setText(UserSettings.getMailServer());
        if(UserSettings.getMailPort() != null)
            textPort.setText(UserSettings.getMailPort());
        if(UserSettings.getMailUser() != null)
            textUser.setText(UserSettings.getMailUser());
        if(UserSettings.getMailPass() != null)
            textPass.setText(UserSettings.getMailPass());

    }

    public String swapVariables(String oldValue)
    {
        if(oldValue.contains("%1$s"))
            return oldValue.replace("%1$s", "{ACTOR}").replace("%2$s", "{SM}");
        else
            return oldValue.replace("{SM}", "%2$s").replace("{ACTOR}", "%1$s");
    }
}
