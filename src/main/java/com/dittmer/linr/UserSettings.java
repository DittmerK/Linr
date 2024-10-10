package com.dittmer.linr;

public class UserSettings
{
    private static String name;
    private static String messageBody;

    private static String mailServer;
    private static String mailUser;
    private static String mailPass;
    private static String mailPort;

    public static String getName() {
        return name;
    }
    public static void setName(String name) {
        UserSettings.name = name;
    }
    public static String getMessageBody() {
        return messageBody;
    }
    public static void setMessageBody(String messageBody) {
        UserSettings.messageBody = messageBody;
    }
    public static String getMailServer() {
        return mailServer;
    }
    public static void setMailServer(String mailServer) {
        UserSettings.mailServer = mailServer;
    }
    public static String getMailUser() {
        return mailUser;
    }
    public static void setMailUser(String mailUser) {
        UserSettings.mailUser = mailUser;
    }
    public static String getMailPass() {
        return mailPass;
    }
    public static void setMailPass(String mailPass) {
        UserSettings.mailPass = mailPass;
    }
    public static String getMailPort() {
        return mailPort;
    }
    public static void setMailPort(String mailPort) {
        UserSettings.mailPort = mailPort;
    }
    
    public static void parse(String value)
    {
        String[] parts = value.split(";");
        name = parts[0];
        messageBody = parts[1];
        mailServer = parts[2];
        mailPort = parts[3];
        mailUser = parts[4];
        mailPass = parts[5];
    }

    public String toString()
    {
        return name + ";" + messageBody + ";" + mailServer + ";" + mailPort + ";" + mailUser + ";" + mailPass;
    }
}