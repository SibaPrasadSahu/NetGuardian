import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class NetworkMonitorGUI extends Application {
    private static TextArea logArea = new TextArea();
    private static TextArea alertArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Network Traffic Monitor");
        logArea.setEditable(false);
        logArea.setPrefHeight(200);
        alertArea.setEditable(false);
        alertArea.setPrefHeight(200);

        VBox root = new VBox(10);
        root.getChildren().addAll(title, logArea, new Label("Alerts:"), alertArea);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Network Monitor");
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread logUpdater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); 
                    String latestLogs = fetchLatestLogs();
                    logArea.appendText(latestLogs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        logUpdater.setDaemon(true); 
        logUpdater.start();
    }

    private static String fetchLatestLogs() {
        StringBuilder logs = new StringBuilder();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/network_logs", "siba", "siba01")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM network_traffic ORDER BY id DESC LIMIT 10");
            while (rs.next()) {
                logs.append(rs.getString("source_ip")).append(" -> ").append(rs.getString("destination_ip")).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs.toString();
    }

    public static void updateAlerts(String message) {
        alertArea.appendText(message + "\n");

        if (message.contains("DDoS") || message.contains("Unauthorized")) {
            Alert alert = new Alert(Alert.AlertType.ERROR); 
            alert.setTitle("Critical Alert");
            alert.setHeaderText("Network Event Detected");
            alert.setContentText(message);
            alert.showAndWait(); 
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
