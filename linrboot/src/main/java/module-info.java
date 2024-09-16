module com.dittmer.linrboot 
{
    requires java.base;

    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.base;
    requires jakarta.mail;
    requires org.eclipse.angus.mail;
    requires transitive javafx.controls;
    requires transitive com.jfoenix;
    requires transitive org.apache.pdfbox;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires javafx.graphics;

    exports com.dittmer.linrboot;
    
}
