import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        // Update logArea with real-time traffic data
        Thread logUpdater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Update every second
                    // Fetch latest logs from database and append to logArea
                    String latestLogs = fetchLatestLogs();
                    logArea.appendText(latestLogs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        logUpdater.setDaemon(true); // So it exits when main app exits
        logUpdater.start();
    }

    private static String fetchLatestLogs() {
        // Implement logic to fetch latest logs from the database
        return "Latest Log Entry";
    }

    public static void updateAlerts(String message) {
        alertArea.appendText(message + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
