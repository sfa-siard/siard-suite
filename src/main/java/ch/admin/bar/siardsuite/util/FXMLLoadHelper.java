package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.SiardApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FXMLLoadHelper {
    public static LoadedFxml load(final String view) {
        val loader = new FXMLLoader(SiardApplication.class.getResource(view));
        try {
            Node node = loader.load();
            Object controller = loader.getController();

            return new LoadedFxml(node, controller);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Failed to load view '%s'", view),
                    e);
        }
    }

    @Value
    public static class LoadedFxml {
        Node node;
        Object controller;
    }
}
