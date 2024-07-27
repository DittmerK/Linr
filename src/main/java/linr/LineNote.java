package linr;

import com.itextpdf.text.BaseColor;
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

    public static String[] actions = {"Called Line", "Paraphrased", "Jumped/Missed Cue", "Pronounciation", "Order of Words", "Missed Word", "Added Word"};
    public static LineNote parse(String input)
    {
        try
        {
            LineNote ret = new LineNote();
            String[] values = input.split(",");
            ret.actor = values[0];
            ret.scene = values[1];
            ret.page = Integer.parseInt(values[2]);
            ret.action = values[3];
            ret.line = values[4];
            ret.notes = values[5];
            ret.occurences = Integer.parseInt(values[6]);
            ret.fixed = Boolean.parseBoolean(values[7]);
            return ret;
        } catch(Exception e){}
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
}