package com.dittmer.linr;

import com.dittmer.linr.util.Util;

import javafx.application.Application;

public class Launcher 
{
    public static void main(String[] args)
    {
        Util.loadSave(App.settingsFileString, App.saveFileString);
        Application.launch(App.class, args);
    }    
}
