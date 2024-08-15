package com.dittmer.linr;

import java.io.*;

import java.util.*;

import javax.swing.JFileChooser;


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

    public static void loadSaveFile(String saveFileName)
    {
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File lineNotesFile = new File(linrFile, saveFileName);

        //Read file if it exists
        try(BufferedReader br = new BufferedReader(new FileReader(lineNotesFile)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                App.lineNotes.add(LineNote.parse(line, true));
            }
        } catch (FileNotFoundException e) {} catch(IOException e) {} catch(NumberFormatException e) {}

    }

    public static void writeSaveFile(String saveFileName)
    {
        App.lineNotes.sort(new SortLineNotes());
        //Write savefile and close
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File lineNotesFile = new File(linrFile, saveFileName);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(lineNotesFile));
            for(LineNote ln : App.lineNotes)
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
        App.lineNotes.sort(new SortLineNotes());
        Map<String, ArrayList<String[]>> lineNotesByActor = new HashMap<String, ArrayList<String[]>>();
        for(LineNote ln : App.lineNotes)
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
                for(String actor : lineNotesByActor.keySet())
                {
                    ArrayList<String[]> table = lineNotesByActor.get(actor);
                    String[][] array2D = new String[table.size()][];
                    for (int i = 0; i < array2D.length; i++) 
                    {     
                        String[] row = table.get(i);
                        array2D[i] = row; 
                    }
                    if(array2D.length > 0)
                        PDFTableGenerator.generatePDF(array2D, colWidths, exportDir.getAbsolutePath() + "/" + actor + "LineNotes.pdf", headerRow, actor);
                }
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }

    }
}
