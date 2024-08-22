/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.dittmer.linr;

import java.io.File;

import org.junit.jupiter.api.Test;


public class AppTest {
    
    public AppTest() {}
    
    @Test
    public void testApp()
    {
        //Just to increase code coverage
        App.reset();
        App.saveFileString = "apptestshows.lnr";
        App.main(null);

        //Cleanup
        File linrFile = new File(System.getProperty("user.home") + "/.linr");
        new File(linrFile, "apptestshows.lnr").delete();
        new File(linrFile, App.currentShow.castFile).delete();
        new File(linrFile, App.currentShow.notesFile).delete();
    }
}
