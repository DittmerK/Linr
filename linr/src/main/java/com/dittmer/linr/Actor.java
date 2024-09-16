package com.dittmer.linr;

public class Actor 
{
    public String name;
    public String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Actor() {}
    
    public Actor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static Actor parse(String input) 
    {
        try
        {
            Actor ret = new Actor();
            String[] values = input.split(";");
            ret.name = values[0];
            ret.email = values[1];
            return ret;
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    public String toString()
    {
        return this.name + ";" + this.email;
    }
}
