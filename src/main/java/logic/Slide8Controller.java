package logic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Slide8Controller {

    @FXML
    private TextField inputField;

    @FXML
    private ListView<String> topicList;

    @FXML
    private TableView<FeatureRow> featureTable;

    @FXML
    private TableColumn<FeatureRow, String> featureColumn;

    @FXML
    private TableColumn<FeatureRow, String> reasonColumn;

    @FXML
    private Label countLabel;

    private final ObservableList<String> topics = FXCollections.observableArrayList(
            "Properties / Binding",
            "FXML + CSS",
            "Task / Service"
    );

    private final ObservableList<FeatureRow> rows = FXCollections.observableArrayList(
            new FeatureRow("ListView", "Сразу отражает изменения ObservableList"),
            new FeatureRow("TableView", "Показывает структурированные данные"),
            new FeatureRow("ObservableList", "Уведомляет UI об изменениях коллекции")
    );

    @FXML
    public void initialize() {
        topicList.setItems(topics);

        featureColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        reasonColumn.setCellValueFactory(data -> data.getValue().reasonProperty());
        featureTable.setItems(rows);

        countLabel.textProperty().bind(Bindings.size(topics).asString("Тем в списке: %d"));
    }

    @FXML
    private void addTopic() {
        String value = inputField.getText();
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        String trimmed = value.trim();
        topics.add(trimmed);
        rows.add(new FeatureRow(trimmed, "Добавлено во время живого демо"));
        inputField.clear();
        topicList.getSelectionModel().selectLast();
        topicList.scrollTo(topics.size() - 1);
    }

    @FXML
    private void removeSelected() {
        int index = topicList.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }

        String removed = topics.remove(index);
        rows.removeIf(row -> removed.equals(row.getName()));
    }

    @FXML
    private void resetDemo() {
        topics.setAll("Properties / Binding", "FXML + CSS", "Task / Service");
        rows.setAll(
                new FeatureRow("ListView", "Сразу отражает изменения ObservableList"),
                new FeatureRow("TableView", "Показывает структурированные данные"),
                new FeatureRow("ObservableList", "Уведомляет UI об изменениях коллекции")
        );
        inputField.clear();
    }

    public static class FeatureRow {
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty reason = new SimpleStringProperty();

        public FeatureRow(String name, String reason) {
            this.name.set(name);
            this.reason.set(reason);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty reasonProperty() {
            return reason;
        }
    }
}
