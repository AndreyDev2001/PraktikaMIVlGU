package com.pin.praktika;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Загрузка FXML файла
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
            Parent root = loader.load();

            // Создание сцены
            Scene scene = new Scene(root, 1000, 600); // Указываем размеры окна

            // Настройки окна
            primaryStage.setTitle("Список сотрудников с днями рождения");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);  // Минимальная ширина окна
            primaryStage.setMinHeight(500); // Минимальная высота окна
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Ошибка загрузки FXML-файла");
            e.printStackTrace();
        }
    }
}
