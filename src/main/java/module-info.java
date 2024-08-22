module com.dittmer.linr 
{
    requires java.base;

    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.base;
    requires transitive javafx.controls;
    requires transitive com.jfoenix;
    requires transitive org.apache.pdfbox;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires javafx.graphics;

    exports com.dittmer.linr;
}