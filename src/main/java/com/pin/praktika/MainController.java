package com.pin.praktika;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML private TableView<Employee> allEmployeesTable;
    @FXML private TableColumn<Employee, String> lastNameCol;
    @FXML private TableColumn<Employee, String> firstNameCol;
    @FXML private TableColumn<Employee, String> middleNameCol;
    @FXML private TableColumn<Employee, LocalDate> birthDateCol;
    @FXML private TableColumn<Employee, String> departmentCol;
    @FXML private TableColumn<Employee, String> positionCol;

    @FXML private DatePicker startDatePicker;
    @FXML private TextField daysField;
    @FXML private TextField endDateField;

    @FXML private TableView<Employee> jubilariansTable;
    @FXML private TableColumn<Employee, String> lastNameJubCol;
    @FXML private TableColumn<Employee, String> firstNameJubCol;
    @FXML private TableColumn<Employee, String> middleNameJubCol;
    @FXML private TableColumn<Employee, LocalDate> birthDateJubCol;
    @FXML private TableColumn<Employee, Integer> ageJubCol; // Столбец возраста
    @FXML private TableColumn<Employee, String> departmentJubCol;
    @FXML private TableColumn<Employee, String> positionJubCol;

    @FXML private TableView<Employee> semiJubilariansTable;
    @FXML private TableColumn<Employee, String> lastNameSemiJubCol;
    @FXML private TableColumn<Employee, String> firstNameSemiJubCol;
    @FXML private TableColumn<Employee, String> middleNameSemiJubCol;
    @FXML private TableColumn<Employee, LocalDate> birthDateSemiJubCol;
    @FXML private TableColumn<Employee, Integer> ageSemiJubCol; // Столбец возраста
    @FXML private TableColumn<Employee, String> departmentSemiJubCol;
    @FXML private TableColumn<Employee, String> positionSemiJubCol;

    private EmployeeDAO employeeDAO;

    public void initialize() {
        employeeDAO = new EmployeeDAO();

        // Настройка колонок таблицы "Все сотрудники"
        initAllEmployeesTable();
        initJubilariansTable();      // Настройка таблицы юбиляров
        initSemiJubilariansTable();  // Настройка таблицы полюбиляров

        // Устанавливаем начальные значения
        startDatePicker.setValue(LocalDate.now());
        daysField.setText("14");

        // Загружаем всех сотрудников
        loadAllEmployees();

        // Загружаем данные с фильтром по умолчанию
        applyFilterOnStartup(LocalDate.now());
        // Автоматически загружаем данные при старте
        loadJubilariansAndSemiJubilarians(LocalDate.now());

        // Слушатели для автоматического обновления конечной даты
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateEndDate());
        daysField.textProperty().addListener((obs, oldVal, newVal) -> updateEndDate());
        updateEndDate();
    }

    // Настройка колонок таблицы "Все сотрудники"
    private void initAllEmployeesTable() {
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        birthDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
    }

    // Метод: загрузить и отобразить всех сотрудников
    private void loadAllEmployees() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        ObservableList<Employee> observableList = FXCollections.observableArrayList(employees);
        allEmployeesTable.setItems(observableList);
    }

    // Настройка таблицы "Юбиляры"
    private void initJubilariansTable() {
        lastNameJubCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameJubCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameJubCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        birthDateJubCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        ageJubCol.setCellValueFactory(cellData -> {
            LocalDate today = LocalDate.now();
            int age = Period.between(cellData.getValue().getBirthDate(), today).getYears();
            return new SimpleIntegerProperty(age).asObject();
        });
        departmentJubCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionJubCol.setCellValueFactory(new PropertyValueFactory<>("position"));
    }

    // Настройка таблицы "Полюбиляры"
    private void initSemiJubilariansTable() {
        lastNameSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        birthDateSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        ageSemiJubCol.setCellValueFactory(cellData -> {
            LocalDate today = LocalDate.now();
            int age = Period.between(cellData.getValue().getBirthDate(), today).getYears();
            return new SimpleIntegerProperty(age).asObject();
        });
        departmentSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionSemiJubCol.setCellValueFactory(new PropertyValueFactory<>("position"));
    }

    // Метод для загрузки юбиляров и полюбиляров
    private void loadJubilariansAndSemiJubilarians(LocalDate referenceDate) {
        List<Employee> allEmployees = employeeDAO.getAllEmployees();
        List<Employee> jubilarians = new ArrayList<>();
        List<Employee> semiJubilarians = new ArrayList<>();

        for (Employee employee : allEmployees) {
            LocalDate birthDate = employee.getBirthDate();

            // Рассчитываем возраст относительно заданной даты
            int age = Math.abs(birthDate.getYear() - referenceDate.getYear());

            if (age % 10 == 0) {
                jubilarians.add(employee); // Юбиляр
            } else if (age % 5 == 0) {
                semiJubilarians.add(employee); // Полюбиляр
            }
        }

        // Устанавливаем данные в таблицы
        jubilariansTable.setItems(FXCollections.observableArrayList(jubilarians));
        semiJubilariansTable.setItems(FXCollections.observableArrayList(semiJubilarians));

        // Применяем цвета к строкам
        applyRowColoring(jubilariansTable, referenceDate);
        applyRowColoring(semiJubilariansTable, referenceDate);
    }

    // Обработчик кнопки "Применить фильтр"
    @FXML
    public void applyFilter() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            int days = Integer.parseInt(daysField.getText());

            List<Employee> filteredList = employeeDAO.getEmployeesByBirthDatePeriod(startDate, days);

            allEmployeesTable.setItems(FXCollections.observableArrayList(filteredList));
            applyRowColoring(allEmployeesTable, startDate);

            // Обновляем таблицы юбиляров и полюбиляров с учётом новой даты
            loadJubilariansAndSemiJubilarians(startDate);

        } catch (NumberFormatException e) {
            showErrorDialog("Ошибка", "Введите корректное число дней.");
        }
    }

    private void applyFilterOnStartup(LocalDate referenceDate) {
        // Применяем цвета к строкам во всех таблицах
        applyRowColoring(allEmployeesTable, referenceDate);
        applyRowColoring(jubilariansTable, referenceDate);
        applyRowColoring(semiJubilariansTable, referenceDate);
    }

    // Метод: выделение строк цветом
    private void applyRowColoring(TableView<Employee> table, LocalDate referenceDate) {
        table.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    int age = Math.abs(item.getBirthDate().getYear() - referenceDate.getYear());

                    if (age % 10 == 0) {
                        setStyle("-fx-background-color: #00ea07;"); // Зелёный
                    } else if (age % 5 == 0) {
                        setStyle("-fx-background-color: #ffe30d;"); // Жёлтый
                    } else {
                        setStyle(""); // Без цвета
                    }
                }
            }
        });
    }

    private void updateEndDate() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            int days = Integer.parseInt(daysField.getText());

            if (startDate != null && days >= 0) {
                LocalDate endDate = startDate.plusDays(days);
                endDateField.setText(endDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        } catch (NumberFormatException e) {
            endDateField.setText("");
        }
    }

    @FXML
    public void showAddEmployeeDialog() {
        showEmployeeDialog(null);
    }

    @FXML
    public void showEditEmployeeDialog() {
        Employee selected = allEmployeesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEmployeeDialog(selected);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Внимание");
            alert.setHeaderText(null);
            alert.setContentText("Выберите сотрудника для редактирования.");
            alert.showAndWait();
        }
    }

    private void showEmployeeDialog(Employee employeeToEdit) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(employeeToEdit == null ? "Добавить сотрудника" : "Редактировать сотрудника");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Поля формы
        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        DatePicker birthDatePicker = new DatePicker();
        ComboBox<String> departmentComboBox = new ComboBox<>();
        ComboBox<String> positionComboBox = new ComboBox<>();

        // 🔒 Блокируем дату рождения при редактировании
        if (employeeToEdit != null) {
            birthDatePicker.setValue(employeeToEdit.getBirthDate());
            birthDatePicker.setDisable(true);
        }

        // Загрузка данных в ComboBox
        departmentComboBox.setItems(FXCollections.observableArrayList(employeeDAO.getAllDepartments()));
        positionComboBox.setItems(FXCollections.observableArrayList(employeeDAO.getAllPositions()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Фамилия:"), lastNameField);
        grid.addRow(1, new Label("Имя:"), firstNameField);
        grid.addRow(2, new Label("Отчество:"), middleNameField);
        grid.addRow(3, new Label("Дата рождения:"), birthDatePicker);
        grid.addRow(4, new Label("Отдел:"), departmentComboBox);
        grid.addRow(5, new Label("Должность:"), positionComboBox);

        dialog.getDialogPane().setContent(grid);

        // Заполнение данными при редактировании
        if (employeeToEdit != null) {
            lastNameField.setText(employeeToEdit.getLastName());
            firstNameField.setText(employeeToEdit.getFirstName());
            middleNameField.setText(employeeToEdit.getMiddleName());
            departmentComboBox.setValue(employeeToEdit.getDepartment());
            positionComboBox.setValue(employeeToEdit.getPosition());
        }

        // Преобразование в объект Employee
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Employee(
                        employeeToEdit != null ? employeeToEdit.getId() : 0,
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText(),
                        birthDatePicker.getValue(),
                        departmentComboBox.getValue(),   // Получаем значение из ComboBox
                        positionComboBox.getValue()      // Получаем значение из ComboBox
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (employeeToEdit == null) {
                int newId = employeeDAO.insertEmployee(result);
                result.setId(newId);
                refreshAllTables();
            } else {
                employeeDAO.updateEmployee(result);
                refreshAllTables();
            }
        });
    }

    @FXML
    public void deleteSelectedEmployee() {
        Employee selected = allEmployeesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите удалить сотрудника " + selected.getLastName() + " " + selected.getFirstName() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Удаляем из БД
                    employeeDAO.deleteEmployee(selected.getId());

                    // Обновляем все таблицы
                    refreshAllTables();
                }
            });
        } else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Внимание");
            warning.setHeaderText(null);
            warning.setContentText("Выберите сотрудника для удаления.");
            warning.showAndWait();
        }
    }

    private void refreshAllTables() {
        LocalDate startDate = startDatePicker.getValue();
        loadAllEmployees();
        loadJubilariansAndSemiJubilarians(startDate);
        applyFilterOnStartup(startDate);
    }

    // Обработчик кнопки "Сбросить"
    @FXML
    public void resetFilter() {
        startDatePicker.setValue(LocalDate.now());
        daysField.setText("14");
        loadAllEmployees(); // Возвращаем полный список
        updateEndDate(); // Обновляем конечную дату
    }

    // Вспомогательный метод: вывод ошибки
    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
