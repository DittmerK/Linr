package com.dittmer.linr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Util 
{

    /**
    * Calculates the similarity (a number within 0 and 1) between two strings.
    */
    public static double similarity(String s1, String s2) 
    {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) // longer should always have greater length
        {
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
            return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) 
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) 
        {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) 
            {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) 
                    {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static String scrubLeadingAndTrailingSpace(String s)
    {
        while(s.length() > 1 && (s.charAt(s.length()-1) == ' ' || s.charAt(s.length()-1) == '\t'))
        {
            s = s.substring(0, s.length()-1);
        }
        while(s.length() > 1 && (s.charAt(0) == ' ' || s.charAt(0) == '\t'))
        {
            s = s.substring(1, s.length());
        }
        return s;
    }

    public static void loadSave()
    {
        loadSave("usersettings.lnr", "shows.lnr");
    }

    public static void loadSave(String settingsFileString, String showsFileString)
    {
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadUserSettingsFile(linrFile, settingsFileString);
        loadShowsFile(showsFileString, linrFile);
        if(App.currentShow != null)
        {
            loadActorsFromSave(linrFile);
            loadNotesFromSave(linrFile);
        }
    }

    public static void loadUserSettingsFile(File linrFile, String saveFileName)
    {
        File settingsFile = new File(linrFile, saveFileName);
        try(BufferedReader br = new BufferedReader(new FileReader(settingsFile)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                UserSettings.parse(line);
            }
            br.close();
        } catch (FileNotFoundException e) {} catch(IOException e) {}
    }

    public static void loadShowsFile(String showsFileString, File linrFile)
    {
        File showsFile = new File(linrFile, showsFileString);
        App.shows = new ArrayList<Show>();
        //Read file if it exists
        try(BufferedReader br = new BufferedReader(new FileReader(showsFile)))
        {
            String line;
            int lastIndex = -1;
            while((line = br.readLine()) != null)
            {
                if(lastIndex >= 0)
                    App.shows.add(Show.parse(line));
                else
                    lastIndex = Integer.parseInt(line);
            }
            if(lastIndex >= 0)
                App.currentShow = App.shows.get(lastIndex);
            br.close();
        } catch (FileNotFoundException e) {} catch(IOException e) {}

    }

    public static void loadActorsFromSave(File linrFile)
    {
        Iterator<Show> showIter = App.shows.iterator();
        while(showIter.hasNext())
        {
            try {
                loadActorsSaveFile(showIter.next(), linrFile);
            } catch (FileNotFoundException e) {
                showIter.remove();
            }   
        }
    }

    public static void loadActorsSaveFile(Show show, File linrFile) throws FileNotFoundException
    {
        show.cast = new ArrayList<Actor>();
        File castFile = new File(linrFile, show.castFile);
        //Read file if it exists
        try(BufferedReader br = new BufferedReader(new FileReader(castFile)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                show.cast.add(Actor.parse(line));
            }
            br.close();
        } catch(IOException e) {}
    }

    public static void loadNotesFromSave(File linrFile)
    {
        Iterator<Show> showIter = App.shows.iterator();
        while(showIter.hasNext())
        {
            try {
                loadNotesSaveFile(showIter.next(), linrFile);
            } catch (FileNotFoundException e) {
                showIter.remove();
            }   
        }
    }

    public static void loadNotesSaveFile(Show show, File linrFile) throws FileNotFoundException
    {
        File lineNotesFile = new File(linrFile, show.notesFile);
        show.lineNotes = new ArrayList<LineNote>();
        //Read file if it exists
        try(BufferedReader br = new BufferedReader(new FileReader(lineNotesFile)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                show.lineNotes.add(LineNote.parse(line, true));
            }
            br.close();
        } catch(IOException e) {} catch(NumberFormatException e) {}

    }

    public static void writeSaveFiles()
    {
        writeSaveFiles("usersettings.lnr", "shows.lnr");
    }

    public static void writeSaveFiles(String settingsFileName, String saveFileName)
    {
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeSettingsFile(linrFile, settingsFileName);
        writeShowsSaveFile(linrFile, saveFileName);
        writeCastsToSave(linrFile);
        writeNotesToSave(linrFile);
    }

    public static void writeSettingsFile(File linrFile, String saveFileName)
    {
        File lineNotesFile = new File(linrFile, saveFileName);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(lineNotesFile));
            bw.write(new UserSettings().toString());
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void writeShowsSaveFile(File linrFile, String saveFileName)
    {
        File lineNotesFile = new File(linrFile, saveFileName);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(lineNotesFile));
            bw.write("" + App.shows.indexOf(App.currentShow));
            bw.newLine();
            for(Show show : App.shows)
            {
                bw.write(show.toSaveString());
                bw.newLine();
            }
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void writeCastsToSave(File linrFile)
    {
        for(Show show : App.shows)
        {
            writeActorsSaveFile(show, linrFile);
        }
    }

    public static void writeActorsSaveFile(Show show, File linrFile)
    {
        File castFile = new File(linrFile, show.castFile);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(castFile));
            for(Actor a : show.cast)
            {
                bw.write(a.toString());
                bw.newLine();
            }
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void writeNotesToSave(File linrFile)
    {
        for(Show show : App.shows)
        {
            writeNotesSaveFile(show, linrFile);   
        }
    }

    public static void writeNotesSaveFile(Show show)
    {
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeNotesSaveFile(show, linrFile);
    }

    public static void writeNotesSaveFile(Show show, File linrFile)
    {
        show.lineNotes.sort(new SortLineNotes());
        File lineNotesFile = new File(linrFile, show.notesFile);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(lineNotesFile));
            for(LineNote ln : show.lineNotes)
            {
                bw.write(ln.toString());
                bw.newLine();
            }
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void exportPDFs()
    {
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
        //Open save window
        JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
        jfc.setDialogTitle("Select Export Folder");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int jfcResult = jfc.showSaveDialog(null);
        if(jfcResult == JFileChooser.APPROVE_OPTION)
        {
            File exportDir = jfc.getSelectedFile();
            try
            {
                float[] colWidths = new float[]{50,50,125,250,246,100.8898f};
                String[] headerRow = new String[]{"Scene", "Page", "Action", "Line", "Notes", "Occurences"};
                for(Actor actor : lineNotesByActor.keySet())
                {
                    ArrayList<String[]> table = lineNotesByActor.get(actor);
                    String[][] array2D = new String[table.size()][];
                    for (int i = 0; i < array2D.length; i++) 
                    {     
                        String[] row = table.get(i);
                        array2D[i] = row; 
                    }
                    if(array2D.length > 0)
                        PDFTableGenerator.generatePDF(array2D, colWidths, exportDir.getAbsolutePath() + "/" + actor.name + "LineNotes.pdf", headerRow, actor.name);
                }
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

    }

    public static void alert(String alert)
    {
        if(Thread.currentThread().getName().equals("JavaFX Application Thread"))
        {
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/alert.fxml"));
                AlertController alertController = new AlertController();
                loader.setController(alertController);
                root = loader.load();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
                stage.show();
                alertController.text.setText(alert);

            } catch (IOException e1) 
            {
                e1.printStackTrace();
            }
        } else
        {
            System.out.println(alert);
        }
    }
}
