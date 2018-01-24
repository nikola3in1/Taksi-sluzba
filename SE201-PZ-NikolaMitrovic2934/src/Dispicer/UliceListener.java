package Dispicer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

class UliceListener implements ChangeListener<String> {
    public ComboBox<String> comboBox;
    public FilteredList<String> filteredItems;
    public UliceListener(ComboBox<String> comboBox, FilteredList<String> filteredItems) {
        this.comboBox = comboBox;
        this.filteredItems = filteredItems;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        final TextField editor = comboBox.getEditor();
        final String selected = comboBox.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            if (selected == null || !selected.equals(editor.getText())) {
                filteredItems.setPredicate(item -> {
                    if (item.toUpperCase().startsWith(newValue.toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }
        });
    }
}
