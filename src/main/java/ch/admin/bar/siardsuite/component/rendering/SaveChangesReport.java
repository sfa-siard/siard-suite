package ch.admin.bar.siardsuite.component.rendering;

import lombok.Value;

import java.util.Optional;

@Value
public class SaveChangesReport {
    Optional<String> failedMessage;

    public SaveChangesReport(String failedMessage) {
        this.failedMessage = Optional.of(failedMessage);
    }

    public SaveChangesReport() {
        this.failedMessage = Optional.empty();
    }
}
