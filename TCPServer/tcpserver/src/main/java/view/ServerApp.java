package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ServerCore;
import model.Config;

import java.util.List;

public class ServerApp extends Application {

    private ListView<String> usersList;
    private TextArea logArea;

    @Override
    public void start(Stage stage) {
        usersList = new ListView<>();

        // Random background color per username (server requirement)
        java.util.Map<String, String> userColor = new java.util.HashMap<>();
        java.util.Random rnd = new java.util.Random();

        usersList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);

                userColor.computeIfAbsent(item, k -> {
                    int r = 80 + rnd.nextInt(156);
                    int g = 80 + rnd.nextInt(156);
                    int b = 80 + rnd.nextInt(156);
                    return String.format("-fx-background-color: rgb(%d,%d,%d);", r,g,b);
                });

                setStyle(userColor.get(item));
            }
        });
logArea = new TextArea();
        logArea.setEditable(false);

        Label usersLabel = new Label("Connected Users");
        Label logLabel = new Label("Server Log");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(10));

        grid.add(usersLabel, 0, 0);
        grid.add(usersList, 0, 1);

        grid.add(logLabel, 1, 0);
        grid.add(logArea, 1, 1);

        Scene scene = new Scene(grid, 700, 400);
        stage.setTitle("TCP Server");
        stage.setScene(scene);
        stage.show();

        // Start server in background thread (UI must not freeze)
        ServerCore server = new ServerCore(Config.serverPort());
        server.setListener(new ServerCore.Listener() {
            @Override
            public void onLog(String line) {
                Platform.runLater(() -> logArea.appendText(line + "\n"));
            }

            @Override
            public void onUsersChanged(List<String> users) {
                Platform.runLater(() -> usersList.getItems().setAll(users));
            }
        });

        Thread t = new Thread(server::start);
        t.setDaemon(true);
        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
