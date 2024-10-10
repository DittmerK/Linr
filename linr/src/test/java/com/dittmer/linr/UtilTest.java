/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.dittmer.linr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.dittmer.linr.controllers.SideMenuController;
import com.dittmer.linr.objects.*;
import com.dittmer.linr.util.Util;

import java.io.*;
import java.util.ArrayList;

public class UtilTest {
    
    public UtilTest() {}
    
    @Test
    public void testScrubLeadingAndTrailingSpaces()
    {
        String s = "    s   ";
        assertEquals("s", Util.scrubLeadingAndTrailingSpace(s));
    }

    @Test 
    public void testEditDistance()
    {
        String foo = "foo";
        String foot = "foot";
        int editDistance = Util.editDistance(foot, foo);
        assertEquals(1, editDistance);
    }

    @Test
    public void testSimilarity()
    {
        String foo = "foo";
        String foot = "foot";
        double sim = Util.similarity(foot, foo);
        assertEquals(.75, sim, 0.001);
    }

    @Test
    public void testLoadSaveFile()
    {
        //Create testdata file
        BufferedWriter bws;
        BufferedWriter bwa;
        BufferedWriter bwn;
        //Create or find file
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        try {
            linrFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        App.reset();
        File showsFile = new File(linrFile, "utiltestshows.lnr");
        File actorsFile = new File(linrFile, "actors.lnr");
        File lineNotesFile = new File(linrFile, "notes.lnr");
        try {
            bws = new BufferedWriter(new FileWriter(showsFile));
            bws.write("0");
            bws.newLine();
            bws.write("Test show;actors.lnr;notes.lnr");
            bws.close();
            bwa = new BufferedWriter(new FileWriter(actorsFile));
            bwa.write("Alexa;alexa@email.com");
            bwa.newLine();
            bwa.close();
            bwn = new BufferedWriter(new FileWriter(lineNotesFile));
            bwn.write("Alexa;1;2;Called Line;Test Line;;1;FALSE");
            bwn.newLine();
            bwn.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            assertFalse(true);//Fail out
        }        

        //Run code and test
        Util.loadSave("utiltestsettings.lnr", "utiltestshows.lnr");

        assertNotNull(App.currentShow);
        assertNotNull(App.currentShow.cast);
        assertEquals(App.currentShow.cast.size(), 1);
        assertEquals(1, App.currentShow.lineNotes.size());
        LineNote expectedLN = new LineNote(App.currentShow.cast.get(0), "1", 2, "Called Line", "Test Line", "", 1, true);
        assertTrue(expectedLN.equals(App.currentShow.lineNotes.get(0)));

        //Cleanup
        showsFile.delete();
        actorsFile.delete();
        lineNotesFile.delete();
        
    }

    @Test
    public void testWriteSaveFileDoesntExist()
    {
        //Setup Testdata
        Actor alexa = new Actor("Alexa", "alexa@email.com");
        LineNote expectedLN = new LineNote(alexa, "1", 2, "Called Line", "Test Line", " ", 1, true);
        Show show = new Show();
        show.name = "Test Show";
        show.castFile = "testcast.lnr";
        show.notesFile = "testnotes.lnr";
        show.lineNotes = new ArrayList<LineNote>();
        show.lineNotes.add(expectedLN);
        show.cast = new ArrayList<Actor>();
        show.cast.add(alexa);
        App.shows = new ArrayList<Show>();
        App.shows.add(show);
        App.currentShow = show;
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        File showsFile = new File(linrFile, "utiltestshows.lnr");
        File castFile = new File(linrFile, "testcast.lnr");
        File notesFile = new File(linrFile, "testnotes.lnr");

        //Run code and test
        assertFalse(showsFile.exists());
        assertFalse(castFile.exists());
        assertFalse(notesFile.exists());
        Util.writeSaveFiles("utiltestsettings.lnr", "utiltestshows.lnr");
        assertTrue(linrFile.exists());
        assertTrue(showsFile.exists());
        assertTrue(castFile.exists());
        assertTrue(notesFile.exists());
        try(BufferedReader br = new BufferedReader(new FileReader(showsFile)))
        {
            assertEquals("0", br.readLine());
            assertEquals("Test Show;testcast.lnr;testnotes.lnr", br.readLine());
        }catch (FileNotFoundException e) {} catch(IOException e) {}
        try(BufferedReader br = new BufferedReader(new FileReader(castFile)))
        {
            assertEquals("Alexa;alexa@email.com", br.readLine());
        }catch (FileNotFoundException e) {} catch(IOException e) {}
        try(BufferedReader br = new BufferedReader(new FileReader(notesFile)))
        {
            assertEquals("Alexa;1;2;Called Line;Test Line; ;1;TRUE", br.readLine());
        } catch (FileNotFoundException e) {} catch(IOException e) {}
        showsFile.delete();
        castFile.delete();
        notesFile.delete();
    }

    @Test
    public void testSaveButton()
    {
         //Setup Testdata
         Actor alexa = new Actor("Alexa", "alexa@email.com");
         LineNote expectedLN = new LineNote(alexa, "1", 2, "Called Line", "Test Line", " ", 1, true);
         Show show = new Show();
         show.name = "Test Show";
         show.castFile = "testcast.lnr";
         show.notesFile = "testnotes.lnr";
         show.lineNotes = new ArrayList<LineNote>();
         show.lineNotes.add(expectedLN);
         show.cast = new ArrayList<Actor>();
         show.cast.add(alexa);
         App.shows = new ArrayList<Show>();
         App.shows.add(show);
         App.currentShow = show;
         File linrFile = new File(System.getProperty("user.home") + "/.linr");
         File notesFile = new File(linrFile, "testnotes.lnr");
 
         //Run code and test
         assertFalse(notesFile.exists());
         Util.writeNotesSaveFile(show);
         assertTrue(linrFile.exists());
         assertTrue(notesFile.exists());
         try(BufferedReader br = new BufferedReader(new FileReader(notesFile)))
         {
             assertEquals("Alexa;1;2;Called Line;Test Line; ;1;TRUE", br.readLine());
         } catch (FileNotFoundException e) {} catch(IOException e) {}
         
         notesFile.delete();
    }

    @Test
    public void testExportPDFS()
    {
        //Setup Testdata
        LineNote expectedLN = new LineNote(new Actor("TESTTEST", "test@email.com"), "1", 2, "Called Line", "Test Line", " ", 1, false);
        App.currentShow = new Show();
        App.currentShow.lineNotes = new ArrayList<LineNote>();
        App.currentShow.lineNotes.add(expectedLN);

        //Run code and test
        new SideMenuController().export();
        //Test for file
        File testFile = new File(System.getProperty("user.home") + "/TESTTESTLineNotes.pdf");
        assertTrue(testFile.exists());
        testFile.delete();
    }
}
