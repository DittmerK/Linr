package com.dittmer.linr.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPAddressSucceededException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;

import com.dittmer.linr.*;
import com.dittmer.linr.objects.*;
import com.dittmer.linr.util.*;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class SideMenuController implements Initializable
{
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button buttonSave;
    @FXML
    public Button buttonExport;
    @FXML
    public ChoiceBox<Show> choiceShow;
    @FXML
    public Button buttonNewShow;
    @FXML
    public VBox vboxSave;
    @FXML 
    public VBox vboxExport;
    @FXML
    public VBox vboxActive;
    @FXML
    public VBox vboxNew;

    public void save()
    {
        Util.writeNotesSaveFile(App.currentShow);
    } 

    public void export()
    {
        Util.exportPDFs();
    }

    public void newShow()
    {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/dittmer/linr/fxml/newShow.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("/com/dittmer/linr/icons/dittmer.png")));
            stage.show();
            Stage currStage = (Stage)buttonExport.getScene().getWindow();
            currStage.close();
                    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void email()
    {
        //Seperate Line Notes By Actor
        App.currentShow.lineNotes.sort(new SortLineNotes());
        Map<Actor, ArrayList<String[]>> lineNotesByActor = new HashMap<Actor, ArrayList<String[]>>();
        for(LineNote ln : App.currentShow.lineNotes)
        {
            if(ln == null)
                continue;

            
            if(!lineNotesByActor.keySet().contains(ln.actor))
            {
                lineNotesByActor.put(ln.actor, new ArrayList<String[]>());
            }
            if(ln.fixed == false)
                lineNotesByActor.get(ln.actor).add(ln.addToTable());

        }

            
        //Get system properties
        Properties properties = System.getProperties();

        //Setup mail server
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", UserSettings.getMailServer());
        properties.setProperty("mail.smtp.host", UserSettings.getMailServer());
        properties.setProperty("mail.smtp.port", UserSettings.getMailPort());

        //Get the Session object.
        Authenticator authenticator = new Authenticator() 
        {
            protected PasswordAuthentication getPasswordAuthentication() 
            {
                return new PasswordAuthentication(UserSettings.getMailUser(), UserSettings.getMailPass());
            }
        };
        Session session = Session.getInstance(properties, authenticator);

        for(Actor a : App.currentShow.cast)
        {

            try {
                File exportDir = new File(System.getProperty("user.home") + "/.linr");
                try {
                    exportDir.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                float[] colWidths = new float[]{50,50,125,250,246,100.8898f};
                String[] headerRow = new String[]{"Scene", "Page", "Action", "Line", "Notes", "Occurences"};
                ArrayList<String[]> table = lineNotesByActor.get(a);
                String[][] array2D = new String[table.size()][];
                for (int i = 0; i < array2D.length; i++) 
                {     
                        String[] row = table.get(i);
                        array2D[i] = row; 
                }
                if(array2D.length > 0)
                {
                    String fileName = a.name + "LineNotes.pdf";
                    PDFTableGenerator.generatePDF(array2D, colWidths, exportDir.getAbsolutePath() + "/" + fileName, headerRow, a.name);
                    File generatedPDF = new File(exportDir, fileName);
                    //Create a default MimeMessage object.
                    MimeMessage message = new MimeMessage(session);

                    //Set From: header field of the header.
                    message.setFrom(new InternetAddress(UserSettings.getMailUser()));

                    //Set To: header field of the header.
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(a.email));

                    //Set Subject: header field
                    message.setSubject(App.currentShow.name + " Line Notes For " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd")));

                    //Build the body of the message
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(String.format(UserSettings.getMessageBody(), a.name, UserSettings.getName()));

                    Multipart mp = new MimeMultipart();

                    mp.addBodyPart(messageBodyPart);

                    //Add attachment
                    messageBodyPart = new MimeBodyPart();
                    DataSource ds = new FileDataSource(generatedPDF);
                    messageBodyPart.setDataHandler(new DataHandler(ds));
                    messageBodyPart.setFileName(fileName);
                    mp.addBodyPart(messageBodyPart);

                    message.setContent(mp);

                    //Send message
                    Transport.send(message);
                    generatedPDF.delete();
                }
                Util.alert("Messages sent sucessfullly");
            } 
            catch (MessagingException e) 
            {
                if (e instanceof SMTPSendFailedException) 
                {
		            SMTPSendFailedException ssfe = (SMTPSendFailedException)e;
                    Util.alert("SMTP SEND FAILED: \n" +
                                                ssfe.toString() + "\n" +
                                                "   Command: " + ssfe.getCommand() + "\n" +
                                                "   RetCode: " + ssfe.getReturnCode() + "\n" +
                                                "   Response: " + ssfe.getMessage());
    		    }
                else 
                {
	    		    Util.alert("Send failed: " + e.toString());
		        }
		        Exception ne;
    		    while ((ne = e.getNextException()) != null &&
	    		    ne instanceof MessagingException) 
                {
		            e = (MessagingException)ne;
		            if (e instanceof SMTPAddressFailedException) 
                    {
		                SMTPAddressFailedException ssfe = (SMTPAddressFailedException)e;
			            Util.alert("ADDRESS FAILED:\n"+
		                            ssfe.toString() + "\n" +
		    	                    "  Address: " + ssfe.getAddress() + "\n" +
	    	                        "  Command: " + ssfe.getCommand() + "\n" +
    		                        "  RetCode: " + ssfe.getReturnCode() + "\n" +
		                            "  Response: " + ssfe.getMessage());
		            }
                    else if (e instanceof SMTPAddressSucceededException) 
                    {
                        SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException)e;
    		            Util.alert("ADDRESS SUCCEEDED:\n" +
	        	                    ssfe.toString() + "\n" +
        			                "  Address: " + ssfe.getAddress() + "\n" +
	    		                    "  Command: " + ssfe.getCommand() + "\n" +
		            	            "  RetCode: " + ssfe.getReturnCode() + "\n" +
        			                "  Response: " + ssfe.getMessage());
    	            
                    }
                }
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }

    public void settings()
    {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/dittmer/linr/fxml/settings.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
            stage.show();
                    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        choiceShow.getItems().addAll(App.shows);
        choiceShow.setValue(App.currentShow);
        choiceShow.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                save();
                App.currentShow = choiceShow.getItems().get((Integer) newValue);
            }
            
        });
    }
}
