package ch.admin.bar.siardsuite.presenter.tree;

import javafx.beans.property.BooleanProperty;
import lombok.Value;

import java.util.Optional;

public interface ChangeableDataPresenter {

    BooleanProperty hasChanged();
    SaveChangesReport saveChanges();
    void dropChanges();

    @Value
    class SaveChangesReport {
        Optional<String> failedMessage;

        public SaveChangesReport(String failedMessage) {
            this.failedMessage = Optional.of(failedMessage);
        }

        public SaveChangesReport() {
            this.failedMessage = Optional.empty();
        }
    }
}
