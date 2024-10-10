package com.dittmer.linr.objects;

import java.util.ArrayList;

public class Show 
{
    public String name;
    public ArrayList<LineNote> lineNotes;
    public ArrayList<Actor> cast;
    public String notesFile;
    public String castFile;


    public Actor getActor(String actorName)
    {
        for(Actor actor : cast)
        {
            if(actor.name.toLowerCase().equals(actorName.toLowerCase()))
                return actor;
        }
        return null;
    }

    public static Show parse(String input) 
    {
        try
        {
            Show ret = new Show();
            String[] values = input.split(";");
            ret.name = values[0];
            ret.notesFile = values[2];
            ret.castFile = values[1];
            return ret;
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    public String toString()
    {
        return this.name;
    }

    public String toSaveString()
    {
        return this.name + ";" + this.castFile + ";" + this.notesFile;
    }
}
