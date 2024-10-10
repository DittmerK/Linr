package com.dittmer.linrboot;


import javafx.application.Application;

public class Launcher 
{
    public static void main(String[] args)
    {
        if(args != null && args.length > 0)
            App.config_url = args[0];
        Application.launch(App.class, args);
    }    
}
