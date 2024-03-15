package ch.admin.bar.siardsuite.framework.view;

import ch.admin.bar.siardsuite.SiardApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FXMLLoadHelper {
    public static <C> LoadedView<C> load(final String view) {
        val loader = new FXMLLoader(SiardApplication.class.getResource(view));
        try {
            Node node = loader.load();
            C controller = loader.getController();

            return new LoadedView<>(node, controller);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Failed to load view '%s'", view),
                    e);
        }
    }
}
