package org.jabref.gui.strings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import org.jabref.gui.help.HelpAction;
import org.jabref.gui.icon.IconTheme.JabRefIcons;
import org.jabref.gui.util.BaseDialog;
import org.jabref.gui.util.IconValidationDecorator;
import org.jabref.logic.help.HelpFile;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabase;

import com.airhacks.afterburner.views.ViewLoader;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import org.controlsfx.tools.ValueExtractor;

public class StringDialogView extends BaseDialog<Void> {

    @FXML private Button btnNewString;
    @FXML private Button btnRemove;
    @FXML private Button btnHelp;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private TableView<StringViewModel> tblStrings;
    @FXML private TableColumn<StringViewModel, String> colLabel;
    @FXML private TableColumn<StringViewModel, String> colContent;

    private final ControlsFxVisualizer visualizer = new ControlsFxVisualizer();
    private final StringDialogViewModel viewModel;

    public StringDialogView(BibDatabase database) {
        viewModel = new StringDialogViewModel(database);

        ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(save);

        ViewLoader.view(this)
                  .load()
                  .setAsContent(this.getDialogPane());
    }

    @FXML
    private void initialize() {
        visualizer.setDecoration(new IconValidationDecorator());

        btnHelp.setGraphic(JabRefIcons.HELP.getGraphicNode());
        btnHelp.setTooltip(new Tooltip(Localization.lang("Open Help page")));

        btnNewString.setGraphic(JabRefIcons.ADD.getGraphicNode());
        btnNewString.setTooltip(new Tooltip(Localization.lang("New string")));

        btnRemove.setGraphic(JabRefIcons.REMOVE.getGraphicNode());
        btnRemove.setTooltip(new Tooltip(Localization.lang("Remove string")));

        //Register TextfieldTableCell Control for validation
        ValueExtractor.addObservableValueExtractor(c -> c instanceof TextFieldTableCell, c -> ((TextFieldTableCell<?, String>) c).textProperty());

        colLabel.setCellFactory(column -> {

            TextFieldTableCell<StringViewModel, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
            column.setCellValueFactory(cellData -> {

                visualizer.initVisualization(cellData.getValue().labelValidation(), cell);
                return cellData.getValue().getLabel();
            });
            return cell;

        });

        colContent.setCellFactory(column -> {

            TextFieldTableCell<StringViewModel, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
            column.setCellValueFactory(cellData -> {

                visualizer.initVisualization(cellData.getValue().contentValidation(), cell);
                return cellData.getValue().getContent();
            });
            return cell;

        });

        colLabel.setOnEditCommit((CellEditEvent<StringViewModel, String> cell) -> {
                                     cell.getRowValue().setLabel(cell.getNewValue());
                                 });
        colContent.setOnEditCommit((CellEditEvent<StringViewModel, String> cell) -> {
                                       cell.getRowValue().setContent(cell.getNewValue());
                                   });

        tblStrings.itemsProperty().bindBidirectional(viewModel.allStringsProperty());
        tblStrings.setEditable(true);

    }

    @FXML
    private void addString(ActionEvent event) {
        viewModel.addNewString();
    }

    @FXML
    private void openHelp(ActionEvent event) {
        HelpAction.openHelpPage(HelpFile.STRING_EDITOR);
    }

    @FXML
    private void removeString(ActionEvent event) {
        StringViewModel selected = tblStrings.getSelectionModel().getSelectedItem();
        viewModel.removeString(selected);
    }

    @FXML
    private void save() {
        viewModel.save();
    }
}