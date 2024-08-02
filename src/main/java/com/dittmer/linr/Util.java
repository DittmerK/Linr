package com.dittmer.linr;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.JFileChooser;

import com.itextpdf.text.*;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Util 
{

    public static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    public static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);

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
                App.lineNotes.add(LineNote.parse(line));
            }
        } catch (FileNotFoundException e) {} catch(IOException e) {}

    }

    public static void writeSaveFile(String saveFileName)
    {
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

    public static void addPdfCellGrey(PdfPTable table, String value)
    {
        PdfPCell cell=new PdfPCell(new Phrase(value, boldFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);
    }

    public static void exportPDFs()
    {
        Map<String, ArrayList<LineNote>> lineNotesByActor = new HashMap<String, ArrayList<LineNote>>();
        for(LineNote ln : App.lineNotes)
        {
            if(ln == null)
                continue;

            
            if(!lineNotesByActor.keySet().contains(ln.actor))
            {
                lineNotesByActor.put(ln.actor, new ArrayList<LineNote>());
            }
            lineNotesByActor.get(ln.actor).add(ln);

        }
        //Open save window
        JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
        jfc.setDialogTitle("Select Export Folder");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int jfcResult = jfc.showSaveDialog(null);
        if(jfcResult == JFileChooser.APPROVE_OPTION)
        {
            File exportDir = jfc.getSelectedFile();
            Map<String, PdfPTable> actorToTable = new HashMap<String, PdfPTable>();
            try
            {
                for(String actor : lineNotesByActor.keySet())
                {                    
                    //Initialize table
                    PdfPTable lineNoteTable = new PdfPTable(6);
                    lineNoteTable.setWidths(new int[]{1,1,2,4,4,2});
                    lineNoteTable.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

                    //Add header row
                    addPdfCellGrey(lineNoteTable, "Scene");
                    addPdfCellGrey(lineNoteTable, "Page");
                    addPdfCellGrey(lineNoteTable, "Action");
                    addPdfCellGrey(lineNoteTable, "Line");
                    addPdfCellGrey(lineNoteTable, "Notes");
                    addPdfCellGrey(lineNoteTable, "Occurences");
                    lineNoteTable.setHeaderRows(1);

                    actorToTable.put(actor, lineNoteTable);

                }

                //Add line notes to table
                for (LineNote ln : App.lineNotes) 
                {   
                    ln.addToTable(actorToTable.get(ln.actor));                          
                }

                //Create document, add table, save
                for(String actor : lineNotesByActor.keySet())
                {
                    //Setup document
                    Document lineNoteDoc = new Document(PageSize.LETTER.rotate());
                    PdfWriter.getInstance(lineNoteDoc, new FileOutputStream(exportDir.getAbsolutePath() + "/" + actor + "LineNotes.pdf"));
                    lineNoteDoc.open();

                    //Add Title
                    Paragraph title = new Paragraph(actor + " Line Notes For " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), titleFont);
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    lineNoteDoc.add(title); 
                    lineNoteDoc.add(new Chunk(""));

                    //Attach table to PDF and close the document
                    lineNoteDoc.add(actorToTable.get(actor));                       
                    lineNoteDoc.close();
                }
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            catch(DocumentException e)
            {
                e.printStackTrace();
            }
        }

    }
}
