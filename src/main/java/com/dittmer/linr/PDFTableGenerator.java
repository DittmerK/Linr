package com.dittmer.linr;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;


/*
 * Inspired by https://gist.github.com/scobal/6125427 and https://github.com/eduardohl/Paginated-PDFBox-Table-Sample/tree/85c96eafcba0f342caf565ed8bdb2e927c84b1d2
 */
public class PDFTableGenerator {


    
    // Generates document from Table object
    public static void generatePDF(String[][] content, float[] colWidths, String filename, String[] headerRow, String actor) throws IOException {
        PDDocument doc = null;
        try {
            doc = new PDDocument();
            drawTable(doc, content, colWidths, headerRow, actor);
            doc.save(filename);
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    // Configures basic setup for the table and draws it page by page
    public static void drawTable(PDDocument doc, String[][] content, float[] colWidths, String[] headerRow, String actor) throws IOException {
        // Calculate pagination
        PagesInfo pi = paginate(content, colWidths);
        String[][][] pagesContents = pi.pages;
        float[][] rowHeights = pi.rowHeights;
        Integer numberOfPages = pagesContents.length;
        //Pull each page and draw it
        for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
            String[][] currentPageContent = pagesContents[pageCount];
            PDPage page = generatePage(doc);
            PDPageContentStream contentStream = generateContentStream(doc, page);
            drawTable(page, contentStream, 10, currentPageContent, colWidths, rowHeights[pageCount], headerRow, actor);
            contentStream.close();
        }
    }


    public static class PagesInfo
    {
        String[][][] pages;
        float[][] rowHeights;

        public PagesInfo(String[][][] pages, float[][] rowHeights)
        {
            this.pages = pages;
            this.rowHeights = rowHeights;
        }
    }


    private static PagesInfo paginate(String[][] table, float[] colWidths) {
        ArrayList<String[][]> pages = new ArrayList<String[][]>();
        float maxHeight = 520 - 40;//Leave space for headerrow
        int numberOfCells = table.length * table[0].length;
        ArrayList<String[]> currentPage = new ArrayList<String[]>();
        ArrayList<String> currentRow = new ArrayList<String>();
        ArrayList<Float> currentPageRowHeights = new ArrayList<Float>();
        float currentHeight = 0f;
        ArrayList<float[]> rowHeights = new ArrayList<float[]>();
        int rowsOfPreviousPages = 0;
        for(int i = 0; i < numberOfCells; i++)
        {
            int col = i % table[0].length;
            int row = Math.floorDiv(i, table[0].length);
            int rowCurrPage = row - rowsOfPreviousPages;
            String text = WordUtils.wrap(table[row][col], (int)Math.ceil(colWidths[col])/10,"\n", true);
            currentRow.add(text);
            if(col==0)
                currentPageRowHeights.add(40f);
            if(text.contains("\n"))
            {
                float requestedHeight = 40f + 15f * StringUtils.countMatches(text, "\n");
                if(currentHeight + requestedHeight < maxHeight)
                    currentPageRowHeights.set(rowCurrPage, requestedHeight);
                else
                {
                    //Paginate and move line to new page
                    currentRow = new ArrayList<String>();
                    currentPageRowHeights.remove(rowCurrPage);
                    String[][] newPage = new String[currentPage.size()][];
                    for(int j = 0; j < currentPage.size(); j++)
                    {
                        newPage[j] = currentPage.get(j);
                    }
                    pages.add(newPage);
                    currentPage = new ArrayList<String[]>();
                    currentHeight = 0;
                    float[] currentPageRowHeightsArray = new float[currentPageRowHeights.size()];
                    for(int j = 0; j < currentPageRowHeights.size(); j++)
                    {
                        currentPageRowHeightsArray[j] = currentPageRowHeights.get(j);
                    }
                    rowHeights.add(currentPageRowHeightsArray);
                    rowsOfPreviousPages += newPage.length;
                    currentPageRowHeights = new ArrayList<Float>();
                    i-=(1+col);
                    continue;
                }
            }
            if(col == table[0].length-1)
            {
                currentHeight += currentPageRowHeights.get(rowCurrPage);
                currentPage.add(currentRow.toArray(new String[currentRow.size()]));
                currentRow = new ArrayList<String>();
                if(currentHeight >= maxHeight-40 || i == numberOfCells-1)
                {
                    String[][] newPage = new String[currentPage.size()][];
                    for(int j = 0; j < currentPage.size(); j++)
                    {
                        newPage[j] = currentPage.get(j);
                    }
                    pages.add(newPage);
                    currentPage = new ArrayList<String[]>();
                    currentHeight = 0;
                    float[] currentPageRowHeightsArray = new float[currentPageRowHeights.size()];
                    for(int j = 0; j < currentPageRowHeights.size(); j++)
                    {
                        currentPageRowHeightsArray[j] = currentPageRowHeights.get(j);
                    }
                    rowHeights.add(currentPageRowHeightsArray);
                    rowsOfPreviousPages += newPage.length;
                    currentPageRowHeights = new ArrayList<Float>();
                }
            }
        }
        String[][][] pagesArray = new String[pages.size()][][];
        for(int i = 0; i < pages.size(); i++)
        {
            pagesArray[i] = pages.get(i);
        }
        float[][] heightsArray = new float[rowHeights.size()][];
        for(int i = 0; i < rowHeights.size(); i++)
        {
            heightsArray[i] = rowHeights.get(i);
        }
        return new PagesInfo(pagesArray, heightsArray);
    }

    private static PDPage generatePage(PDDocument doc) {
        PDPage page = new PDPage();
        page.setMediaBox(PDRectangle.A4);
        page.setRotation(90);
        doc.addPage(page);
        return page;
    }

    private static PDPageContentStream generateContentStream(PDDocument doc, PDPage page) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.OVERWRITE, false, false);
        // User transformation matrix to change the reference when drawing.
        // This is necessary for the landscape position to draw correctly
        contentStream.transform(new Matrix(0, 1, -1, 0, PDRectangle.LETTER.getWidth(), 0));
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 14);
        return contentStream;
    }

    private static void drawTable(PDPage page, PDPageContentStream contentStream, float margin, String[][] content, float[] colWidths, float[] rowHeights, String[] headerRow, String actor) throws IOException 
    {

        final int rows = content.length;
        final int cols = content[0].length;
        final float tableWidth = page.getMediaBox().getHeight() - margin - margin;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;
        float tableHeight = 40f; //40 for header row
        for(float f : rowHeights)
            tableHeight += f;
        float y = page.getMediaBox().getHeight()-290;

        //Title
        contentStream.beginText();
        String titleText = actor + "'s Line Notes For " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        float titleX = (page.getMediaBox().getWidth()/2) - (titleText.length()/2);
        contentStream.newLineAtOffset(titleX, y+20);
        contentStream.showText(titleText);
        contentStream.endText();

        //draw the rows
        float nexty = y ;
        contentStream.moveTo(margin, nexty);
        contentStream.lineTo(margin+tableWidth, nexty);
        contentStream.stroke();
        nexty-= 40f;
        for (int i = 0; i <= rows; i++) {
            contentStream.moveTo(margin, nexty);
            contentStream.lineTo(margin+tableWidth, nexty);
            contentStream.stroke();
            if(i<rows)nexty-= rowHeights[i];
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i < cols; i++) {
            contentStream.moveTo(nextx, y);
            contentStream.lineTo(nextx, y-tableHeight);
            contentStream.stroke();
            nextx += (colWidths != null) ? colWidths[i] : colWidth;
        }
        contentStream.moveTo(nextx, y);
        contentStream.lineTo(nextx, y-tableHeight);
        contentStream.stroke();

        //Write the text
        //Header
        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < headerRow.length; i++)
        {
            contentStream.beginText();
            contentStream.newLineAtOffset(textx,texty);
            contentStream.showText(headerRow[i]);
            contentStream.endText();
            textx += (colWidths != null) ? colWidths[i] : colWidth;
        }
        texty-=40f;
        textx = margin+cellMargin;
        //Contents
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                if (text != null && text.contains("\n")) {
                    String[] lines = text.split("\n");
                    //contentStream.newLine();
                    for (int k = 0; k < lines.length; k++) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textx,texty-(15*k));
                        contentStream.showText(lines[k]);
                        contentStream.endText();
                    }
                } else {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textx,texty);
                    contentStream.showText(text);
                    contentStream.endText();
                }
                textx += (colWidths != null) ? colWidths[j] : colWidth;
            }
            texty-=rowHeights[i];
            textx = margin+cellMargin;
        }
    }
}
