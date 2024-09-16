package com.dittmer.linr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class NewShowController implements Initializable
{
    @FXML
    public AnchorPane rootPane;
    @FXML
    public TextField textShow;
    @FXML
    public TableView<Actor> tableCast;
    @FXML
    public TableColumn<Actor, String> colActorName;
    @FXML
    public TableColumn<Actor, String> colActorEmail;
    @FXML
    public Button buttonSubmit;
    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonSub;

    private ObservableList<Actor> castData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) 
    {
        tableCast.setItems(castData);
        tableCast.setEditable(true);
        // allows the individual cells to be selected
        tableCast.getSelectionModel().cellSelectionEnabledProperty().set(true);
        // when character or numbers pressed it will start edit in editable
        // fields
        tableCast.setOnKeyPressed(event -> 
        {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) 
            {
                editFocusedCell();
            }
            else if (event.getCode() == KeyCode.RIGHT ||
                event.getCode() == KeyCode.TAB) {
                tableCast.getSelectionModel().selectRightCell();
                event.consume();
            } 
            else if (event.getCode() == KeyCode.LEFT) 
            {
                tableCast.getSelectionModel().selectLeftCell();
                event.consume();
            }
            else if(event.getCode() == KeyCode.UP && tableCast.getSelectionModel().getSelectedIndex() > 0)
            {
                tableCast.getSelectionModel().selectAboveCell();
                event.consume();
            }
            else if(event.getCode() == KeyCode.DOWN && tableCast.getSelectionModel().getSelectedIndex() < castData.size()-1)
            {
                tableCast.getSelectionModel().selectBelowCell();
                event.consume();
            }
        });
        colActorName.setCellFactory(TextFieldTableCell.forTableColumn());
        colActorName.setCellValueFactory(actor -> 
        {
            SimpleObjectProperty<String> property = new SimpleObjectProperty<>();
            property.setValue(actor.getValue().getName());
            return property;
        });
        colActorName.setOnEditCommit(
            (TableColumn.CellEditEvent<Actor, String> t) ->
                ( t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setName(t.getNewValue())
            );
        colActorName.setOnEditCancel(
            (TableColumn.CellEditEvent<Actor, String> t) ->
                ( t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setName(t.getNewValue())
            );
        colActorEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colActorEmail.setCellValueFactory(actor -> 
        {
            SimpleObjectProperty<String> property = new SimpleObjectProperty<>();
            property.setValue(actor.getValue().getEmail());
            return property;
        });
        colActorEmail.setOnEditCommit(
            (TableColumn.CellEditEvent<Actor, String> t) ->
                ( t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setEmail(t.getNewValue())
            );
        colActorEmail.setOnEditCancel(
            (TableColumn.CellEditEvent<Actor, String> t) ->
                ( t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setEmail(t.getNewValue())
            );
        
    }

    @SuppressWarnings("unchecked")
    private void editFocusedCell() 
    {
        final TablePosition < Actor, ? > focusedCell = tableCast
            .focusModelProperty().get().focusedCellProperty().get();
        tableCast.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    public void submit()
    {
        ArrayList<Actor> cast = new ArrayList<>();
        castData.forEach(a -> cast.add(a));
        Show newShow = new Show();
        newShow.name=textShow.getText();
        newShow.castFile=(textShow.getText().replace(" ", "") + "actors.lnr");
        newShow.notesFile=(textShow.getText().replace(" ", "") + "notes.lnr");
        newShow.cast = cast;
        newShow.lineNotes = new ArrayList<LineNote>();
        App.shows.add(newShow);
        App.currentShow = newShow;
        textShow.setText("");
        castData.clear();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/enterline.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
            stage.show();
            Stage currStage = (Stage)textShow.getScene().getWindow();
            currStage.close();
            if(UserSettings.getName() == null)
            {
                loader = new FXMLLoader(App.class.getResource("fxml/settings.fxml"));
                root = loader.load();
                stage = new Stage();
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/dittmer.png")));
                stage.show();
            }
                    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add()
    {
        castData.add(new Actor("",""));
    }

    public void sub()
    {
        castData.remove(tableCast.focusModelProperty().get().getFocusedIndex());
    }
    
    
}
