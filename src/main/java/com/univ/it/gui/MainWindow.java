package com.univ.it.gui;

import com.univ.it.table.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.rmi.Naming;
import java.util.*;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    private VBox verticalLayout;
    private IRemoteDataBase currentDB;
    private TabPane tabPane;

    private final ObservableList<String> availableOptions =
            FXCollections.observableArrayList(
                    "Char",
                    "CharInterval",
                    "Integer",
                    "Real"
            );

    private void initUI(Stage stage) {
        //currentDB = new LocalDataBase("New database");

        if (!initRemoteDb()) {
            showErrorMessage("Cannot connect to remote Db");
            System.exit(-1);
        }

        StackPane root = new StackPane();
        verticalLayout = new VBox();

        Scene scene = new Scene(root, 600, 480);

        initializeMenuBar();
        initializeTableTab();

        root.getChildren().add(verticalLayout);

        stage.setTitle("Database Logic");
        stage.setScene(scene);
        stage.show();

        showDataBase();
    }

    private boolean initRemoteDb() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.out.println("Security manager installed.");
        }

        try {
            currentDB = (IRemoteDataBase) Naming.lookup("//localhost/RmiDataBase");
            return true;
        } catch (Exception e) {
            showErrorMessage("RmiClient exception: " + e);
            e.printStackTrace();
            return false;
        }
    }

    private void initializeMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuHelp = new Menu("Help");
        Menu menuTable = new Menu("Table");

        MenuItem saveDbMenuItem = new MenuItem("Save changes");
        saveDbMenuItem.setOnAction(t -> saveChangesToRemoteDB());
        menuFile.getItems().addAll(saveDbMenuItem);

        MenuItem newTableMenuItem = new MenuItem("New Table");
        newTableMenuItem.setOnAction(t -> createTable());
        MenuItem dropTableMenuItem = new MenuItem("Drop Table");
        dropTableMenuItem.setOnAction(t -> dropTable());
        MenuItem addNewRowTableMenuItem = new MenuItem("Add New Row");
        addNewRowTableMenuItem.setOnAction(t -> addNewRowTable());
        MenuItem calculateDifferenceMenuItem = new MenuItem("Calculate Difference");
        calculateDifferenceMenuItem.setOnAction(t -> calculateDifference());
        menuTable.getItems().addAll(
                newTableMenuItem,
                dropTableMenuItem,
                addNewRowTableMenuItem,
                calculateDifferenceMenuItem);

        MenuItem helpMenuItem = new MenuItem("About");
        helpMenuItem.setOnAction(t -> showHelpWindow());
        menuHelp.getItems().addAll(helpMenuItem);

        menuBar.getMenus().addAll(menuFile, menuTable, menuHelp);
        verticalLayout.getChildren().add(menuBar);
    }

    private void saveChangesToRemoteDB() {
        try {
            currentDB.writeToFile("");
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createTable() {
        Label secondLabel = new Label("Name of table");
        TextField tableNameTextField = new TextField();

        HBox horizontalLayout = new HBox();
        horizontalLayout.getChildren().addAll(secondLabel, tableNameTextField);

        VBox _verticalLayout = new VBox();

        Button addNewColumnButton = new Button("Add new column");

        Button createNewTableButton = new Button("Create New Table");

        HBox buttonsLayout = new HBox();
        buttonsLayout.getChildren().addAll(addNewColumnButton, createNewTableButton);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(horizontalLayout, _verticalLayout, buttonsLayout);

        // New window (Stage)
        Scene secondScene = new Scene(mainLayout);
        Stage newWindow = new Stage();
        newWindow.setTitle("New Table");
        newWindow.setScene(secondScene);

        HBox columnCreationLayout = new HBox();
        ArrayList<ComboBox> comboBoxes = new ArrayList<>();
        ComboBox comboBox = new ComboBox(availableOptions);
        comboBoxes.add(comboBox);
        columnCreationLayout.getChildren().addAll(new Label("Column"), comboBox);
        _verticalLayout.getChildren().add(columnCreationLayout);
        addNewColumnButton.setOnAction(e -> {
            HBox _columnCreationLayout = new HBox();
            ComboBox _comboBox = new ComboBox(availableOptions);
            comboBoxes.add(_comboBox);
            _columnCreationLayout.getChildren().addAll(new Label("Column"), _comboBox);
            _verticalLayout.getChildren().add(_columnCreationLayout);
        });

        createNewTableButton.setOnAction(e -> {
            boolean allTypesChosen = true;
            ArrayList<Column> columns = new ArrayList<>();
            for (ComboBox _comboBox : comboBoxes) {
                if (_comboBox.getValue() == null) {
                    allTypesChosen = false;
                    break;
                } else {
                    columns.add(new Column("com.univ.it.types.Attribute" + _comboBox.getValue().toString()));
                }
            }
            if (!allTypesChosen) {
                showErrorMessage("Not all Types are chosen");
                return;
            }
            if (!tableNameTextField.getText().equals("")) {
                Table newTable = new Table(tableNameTextField.getText(), columns);
                try {
                    currentDB.addTable(newTable);
                } catch (Exception ex) {
                    showErrorMessage(ex.getMessage());
                    return;
                }
                newWindow.close();
                showTable(newTable);
            } else {
                showErrorMessage("Empty table name");
            }
        });

        newWindow.show();
    }

    private void showDataBase() {
        HashMap<String, Table> tables;
        try{
            tables = currentDB.getTables();
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
            StackTraceElement[] elements = e.getStackTrace();
            System.out.println(e.getMessage());
            for (StackTraceElement el : elements) {
                System.out.println(el.toString());
            }
            return;
        }
        for (HashMap.Entry<String, Table> entry : tables.entrySet()) {
            String tableName = entry.getKey();
            Table table = entry.getValue();
            Tab tab = new Tab();
            tab.setText(tableName);
            TableView tableView = new TableView();
            tab.setContent(tableView);
            tabPane.getTabs().add(tab);
            showTable(table, tableView);
        }
    }

    @SuppressWarnings("unchecked")
    private void showTable(Table table, TableView tableView) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        for (int i = 0; i < table.columnNumber(); i++) {
            TableColumn col = new TableColumn(table.getColumn(i).getName());
            final int j = i;
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                            new SimpleStringProperty(param.getValue().get(j).toString())
            );
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(
                    (EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) t -> {
                        String newValue = t.getNewValue();
                        try {
                            table.replaceAt(t.getTablePosition().getRow(), j, newValue);
                        } catch (Exception e) {
                            showErrorMessage(e.toString());
                        }
                    }
            );
            tableView.getColumns().add(col);
        }

        for (int i = 0; i < table.size(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int j = 0; j < table.getRow(i).size(); j++) {
                row.add(table.getRow(i).getAt(j).toString());
            }
            data.add(row);
        }
        tableView.setEditable(true);
        tableView.setItems(data);
    }

    private void dropTable() {
        String tabName;
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tabName = tab.getText();
        } catch (Exception e) {
            showErrorMessage("No current table");
            return;
        }
        try {
            if (!currentDB.dropTable(tabName)) {
                showErrorMessage("Error occurred");
            }
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
        closeAllTabs();
        showDataBase();
    }

    private void addNewRowTable() {
        Table table;
        String tableName;
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tableName = tab.getText();
            table = currentDB.getTable(tableName);
        } catch (Exception e) {
            showErrorMessage("No current table");
            return;
        }
        VBox mainLayout = new VBox();
        ArrayList<TextField> textFields = new ArrayList<>();
        for (int i = 0; i < table.columnNumber(); ++i) {
            HBox horizontalBoxLayout = new HBox();
            Column col = table.getColumn(i);
            Label label = new Label(col.getName());
            TextField valueTextField = new TextField();
            textFields.add(valueTextField);
            horizontalBoxLayout.getChildren().addAll(label, valueTextField);
            mainLayout.getChildren().add(horizontalBoxLayout);
        }

        Button addButton = new Button("Add new row");
        mainLayout.getChildren().add(addButton);

        Scene secondScene = new Scene(mainLayout);

        Stage newWindow = new Stage();
        newWindow.setTitle("Add new row");
        newWindow.setScene(secondScene);

        addButton.setOnAction(e -> {
            boolean allValuesFilled = true;
            Row row = new Row();
            for (int i = 0; i < textFields.size(); ++i) {
                TextField textField = textFields.get(i);
                if (textField.getText().equals("")) {
                    allValuesFilled = false;
                } else {
                    Column col = table.getColumn(i);
                    try {
                        row.pushBack(col.createAttribute(textField.getText()));
                    } catch (Exception ex) {
                        showErrorMessage(ex.toString());
                    }
                }
            }
            if (!allValuesFilled) {
                showErrorMessage("Not all values filled");
            }
            try {
                currentDB.addRow(tableName, row);
            } catch (Exception ex) {
                showErrorMessage(ex.toString());
            }
            newWindow.close();
        closeAllTabs();
        showDataBase();
        });

        newWindow.show();
    }

    @SuppressWarnings("unchecked")
    private void calculateDifference() {
        Collection<String> allTableNames;
        try {
            allTableNames = currentDB.getTables().keySet();
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
            return;
        }
        Label firstTableLabel = new Label("First Table Name");
        ComboBox firstTableComboBox = new ComboBox();
        for (String tableName : allTableNames) {
            firstTableComboBox.getItems().add(tableName);
        }

        Label secondTableLabel = new Label("First Table Name");
        ComboBox secondTableComboBox = new ComboBox();
        for (String tableName : allTableNames) {
            secondTableComboBox.getItems().add(tableName);
        }

        HBox horizontalLayout1 = new HBox();
        horizontalLayout1.getChildren().addAll(firstTableLabel, firstTableComboBox);

        HBox horizontalLayout2 = new HBox();
        horizontalLayout2.getChildren().addAll(secondTableLabel, secondTableComboBox);

        Button calculateDifference = new Button("Calculate Difference");

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(horizontalLayout1, horizontalLayout2, calculateDifference);

        Scene secondScene = new Scene(mainLayout);

        Stage newWindow = new Stage();
        newWindow.setTitle("Calculate Difference");
        newWindow.setScene(secondScene);

        calculateDifference.setOnAction(e -> {
            if (firstTableComboBox.getValue() == null || secondTableComboBox.getValue() == null) {
                showErrorMessage("Choose table");
                return;
            }
            String firstTableName = firstTableComboBox.getValue().toString();
            String secondTableName = secondTableComboBox.getValue().toString();
            try {
                currentDB.addTable(
                        Table.differenceBetween(
                                currentDB.getTable(firstTableName),
                                currentDB.getTable(secondTableName)
                        )
                );
            } catch (Exception e1) {
                showErrorMessage(e1.getMessage());
                return;
            }
            newWindow.close();
            closeAllTabs();
            showDataBase();
        });

        newWindow.show();
    }

    private void showTable(Table table) {
        Tab tab = new Tab();
        tab.setText(table.getName());
        TableView tableView = new TableView();
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
        showTable(table, tableView);
    }

    private static void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showHelpWindow() {
        Label secondLabel = new Label("Author: Mykola Bondarenko");

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("About");
        newWindow.setScene(secondScene);

        newWindow.show();
    }

    private void initializeTableTab() {
        tabPane = new TabPane();
        verticalLayout.getChildren().add(tabPane);
    }

    private void closeAllTabs() {
        tabPane.getTabs().clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
