package com.project.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.project.dao.ProjektDAO;
import com.project.dao.ProjektDAOImpl;
import com.project.model.Projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class ProjectController {

    private String search4;
    private Integer pageNo;
    private Integer pageSize;

    private ProjektDAO projektDAO;
    private ObservableList<Projekt> projekty;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private ChoiceBox<Integer> cbPageSizes;

    @FXML
    private TableView<Projekt> tblProjekt;

    @FXML
    private TableColumn<Projekt, Integer> colId;

    @FXML
    private TableColumn<Projekt, String> colNazwa;

    @FXML
    private TableColumn<Projekt, String> colOpis;

    @FXML
    private TableColumn<Projekt, LocalDateTime> colDataCzasUtworzenia;

    @FXML
    private TableColumn<Projekt, LocalDate> colDataOddania;

    @FXML
    private TextField txtSzukaj;

    @FXML
    private Button btnDalej;

    @FXML
    private Button btnWstecz;

    @FXML
    private Button btnPierwsza;

    @FXML
    private Button btnOstatnia;

    public ProjectController() {
        projektDAO = new ProjektDAOImpl();
    }

    @FXML
    public void initialize() {
        search4 = "";
        pageNo = 0;
        pageSize = 10;

        cbPageSizes.getItems().addAll(5, 10, 20, 50, 100);
        cbPageSizes.setValue(pageSize);

        cbPageSizes.setOnAction(event -> {
            pageSize = cbPageSizes.getValue();
            pageNo = 0;
            loadPage();
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("projektId"));
        colNazwa.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        colOpis.setCellValueFactory(new PropertyValueFactory<>("opis"));
        colDataCzasUtworzenia.setCellValueFactory(new PropertyValueFactory<>("dataCzasUtworzenia"));
        colDataOddania.setCellValueFactory(new PropertyValueFactory<>("dataOddania"));
        
        TableColumn<Projekt, Void> colAkcje = new TableColumn<>("Akcje");

        colAkcje.setCellFactory(column -> new TableCell<Projekt, Void>() {
            private final Button btnEdytuj = new Button("Edytuj");
            private final Button btnUsun = new Button("Usuń");
            private final HBox box = new HBox(5, btnEdytuj, btnUsun);

            {
                box.setAlignment(Pos.CENTER);
                box.setPadding(new Insets(3));

                btnEdytuj.setOnAction(event -> {
                    Projekt projekt = getTableView().getItems().get(getIndex());
                    edytujProjekt(projekt);
                });

                btnUsun.setOnAction(event -> {
                    Projekt projekt = getTableView().getItems().get(getIndex());
                    usunProjekt(projekt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tblProjekt.getColumns().add(colAkcje);

        projekty = FXCollections.observableArrayList();
        tblProjekt.setItems(projekty);

        loadPage();
    }

    private void loadPage() {
        projekty.clear();

        List<Projekt> lista;

        if (search4 != null && !search4.isEmpty()) {
            if (search4.matches("[0-9]+")) {
                Projekt projekt = projektDAO.getProjekt(Integer.parseInt(search4));
                if (projekt != null) {
                    projekty.add(projekt);
                }
                return;
            } else if (search4.matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                lista = projektDAO.getProjektyWhereDataOddaniaIs(LocalDate.parse(search4, dateFormatter),
                        pageNo * pageSize, pageSize);
            } else {
                lista = projektDAO.getProjektyWhereNazwaLike(search4, pageNo * pageSize, pageSize);
            }
        } else {
            lista = projektDAO.getProjekty(pageNo * pageSize, pageSize);
        }

        projekty.addAll(lista);
    }

    @FXML
    private void onActionBtnSzukaj(ActionEvent event) {
        search4 = txtSzukaj.getText().trim();
        pageNo = 0;
        loadPage();
    }

    @FXML
    private void onActionBtnDalej(ActionEvent event) {
        pageNo++;
        loadPage();
    }

    @FXML
    private void onActionBtnWstecz(ActionEvent event) {
        if (pageNo > 0) {
            pageNo--;
            loadPage();
        }
    }

    @FXML
    private void onActionBtnPierwsza(ActionEvent event) {
        pageNo = 0;
        loadPage();
    }

    @FXML
    private void onActionBtnOstatnia(ActionEvent event) {
        int rows;

        if (search4 != null && !search4.isEmpty()) {
            if (search4.matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                rows = projektDAO.getRowsNumberWhereDataOddaniaIs(LocalDate.parse(search4, dateFormatter));
            } else {
                rows = projektDAO.getRowsNumberWhereNazwaLike(search4);
            }
        } else {
            rows = projektDAO.getRowsNumber();
        }

        pageNo = Math.max(0, (rows - 1) / pageSize);
        loadPage();
    }

    @FXML
    private void onActionBtnDodaj(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dodawanie projektu");
        dialog.setHeaderText("Podaj nazwę projektu");
        dialog.setContentText("Nazwa:");

        dialog.showAndWait().ifPresent(nazwa -> {
            Projekt projekt = new Projekt(nazwa, "Opis projektu", LocalDate.now());
            projektDAO.setProjekt(projekt);
            pageNo = 0;
            loadPage();
        });
    }

    private void edytujProjekt(Projekt projekt) {
        TextInputDialog dialog = new TextInputDialog(projekt.getNazwa());
        dialog.setTitle("Edycja projektu");
        dialog.setHeaderText("Zmień nazwę projektu");
        dialog.setContentText("Nazwa:");

        dialog.showAndWait().ifPresent(nazwa -> {
            projekt.setNazwa(nazwa.trim());
            projektDAO.setProjekt(projekt);
            loadPage();
        });
    }
    
    private void usunProjekt(Projekt projekt) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie projektu");
        alert.setHeaderText("Czy na pewno chcesz usunąć projekt?");
        alert.setContentText(projekt.getNazwa());

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                projektDAO.deleteProjekt(projekt.getProjektId());
                loadPage();
            }
        });
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void shutdown() {
    }
}