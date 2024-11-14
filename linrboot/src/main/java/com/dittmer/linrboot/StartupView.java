package com.dittmer.linrboot;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.UpdateOptions;
import org.update4j.service.UpdateHandler;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StartupView extends StackPane implements UpdateHandler
{

    @FXML
    public Label labelStatus;

    @FXML
    public VBox progressContainer;

    @FXML
    public Pane panePrimary;
    
    @FXML
    public Pane paneSecondary;

    private DoubleProperty primaryPercent;
    private DoubleProperty secondaryPercent;

    private Configuration config;
    

    public void setConfig(Configuration config) 
    {
        this.config = config;

		primaryPercent = new SimpleDoubleProperty(this, "primaryPercent");
		secondaryPercent = new SimpleDoubleProperty(this, "secondaryPercent");

		panePrimary.maxWidthProperty().bind(progressContainer.widthProperty().multiply(primaryPercent));
		paneSecondary.maxWidthProperty().bind(progressContainer.widthProperty().multiply(secondaryPercent));

        Task<Void> doUpdate = new Task<>() 
        {
		    @Override
			protected Void call() throws Exception 
            {
				Path zip = Paths.get("business-update.zip");
				if(config.update(UpdateOptions.archive(zip).updateHandler(StartupView.this)).getException() == null) 
                {
				    Archive.read(zip).install();
				}
				return null;
			}

		};

		run(doUpdate);
    }

	private void run(Runnable runnable) 
    {
		Thread runner = new Thread(runnable);
		runner.setDaemon(true);
		runner.start();
	}

    /*
	 * UpdateHandler methods
	 */
	@Override
	public void updateDownloadFileProgress(FileMetadata file, float frac) 
    {
		Platform.runLater(() -> {
			labelStatus.setText("Downloading " + file.getPath().getFileName() + " (" + ((int) (100 * frac)) + "%)");
			secondaryPercent.set(frac);
		});
	}

	@Override
	public void updateDownloadProgress(float frac) throws InterruptedException 
    {
		Platform.runLater(() -> primaryPercent.set(frac));
	}

	@Override
	public void failed(Throwable t) 
    {
		Platform.runLater(() -> {
			String error = "Failed: " + t.getClass().getSimpleName() + ": " + t.getMessage();
			labelStatus.setText(error);
			System.out.println(error);
		});
	}

	@Override
	public void succeeded() 
    {
		Platform.runLater(() -> labelStatus.setText("Download complete. You can now close the window."));
	}
}
