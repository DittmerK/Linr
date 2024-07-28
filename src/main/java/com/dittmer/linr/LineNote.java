package com.dittmer.linr;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class LineNote
{
    public String actor;
    public String scene;
    public int page;
    public String action;
    public String line;
    public String notes;
    public int occurences;
    public boolean fixed;


    public LineNote() {}

    public LineNote(String actor, String scene, int page, String action, String line, String notes, int occurences,
            boolean fixed) {
        this.actor = actor;
        this.scene = scene;
        this.page = page;
        this.action = action;
        this.line = line;
        this.notes = notes;
        this.occurences = occurences;
        this.fixed = fixed;
    }

    public static String[] actions = {"Called Line", "Paraphrased", "Jumped/Missed Cue", "Pronounciation", "Order of Words", "Missed Word", "Added Word"};
    
    
    
    public static LineNote parse(String input)
    {
        try
        {
            LineNote ret = new LineNote();
            String[] values = input.split(",");
            ret.actor = values[0];
            ret.scene = values[1];
            ret.page = Integer.parseInt(values[2].replaceAll(" ", ""));
            ret.action = values[3];
            ret.line = values[4];
            ret.notes = values[5];
            ret.occurences = Integer.parseInt(values[6].replaceAll(" ", ""));
            ret.fixed = Boolean.parseBoolean(values[7].replaceAll(" ", ""));
            return ret;
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    public String toString()
    {
        return actor + "," + scene + "," + page + "," + action + "," + line + "," + notes + "," + occurences + "," + (fixed ? "TRUE" : "FALSE");
    }

    public void addToTable(PdfPTable table)
    {
        PdfPCell cell=new PdfPCell(new Phrase(scene));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);
        cell=new PdfPCell(new Phrase("" + page));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);
        cell=new PdfPCell(new Phrase(action));
        table.addCell(cell);
        cell=new PdfPCell(new Phrase(line));
        table.addCell(cell);
        cell=new PdfPCell(new Phrase(notes));
        table.addCell(cell);
        cell=new PdfPCell(new Phrase("" + occurences));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);
        cell=new PdfPCell(new Phrase((fixed ? "Yes" : "No")));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actor == null) ? 0 : actor.hashCode());
        result = prime * result + ((scene == null) ? 0 : scene.hashCode());
        result = prime * result + page;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((line == null) ? 0 : line.hashCode());
        result = prime * result + ((notes == null) ? 0 : notes.hashCode());
        result = prime * result + occurences;
        result = prime * result + (fixed ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LineNote other = (LineNote) obj;
        if (actor == null) {
            if (other.actor != null)
                return false;
        } else if (!actor.equals(other.actor))
            return false;
        if (scene == null) {
            if (other.scene != null)
                return false;
        } else if (!scene.equals(other.scene))
            return false;
        if (page != other.page)
            return false;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (line == null) {
            if (other.line != null)
                return false;
        } else if (!line.equals(other.line))
            return false;
        if (notes == null) {
            if (other.notes != null)
                return false;
        } else if (!notes.equals(other.notes))
            return false;
        if (occurences != other.occurences)
            return false;
        if (fixed != other.fixed)
            return false;
        return true;
    }

   
}