package ch.admin.bar.siardsuite.presenter.connection;

import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.presenter.connection.fields.FileChooserFormField;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.val;

public class SelectFileConnectionPropertiesForm extends VBox {

    private static final I18nKey MS_ACCESS_FILE_TITLE = I18nKey.of("connection.view.msaccess.title");
    private static final I18nKey MS_ACCESS_FILE_CHOOSER_TITLE = I18nKey.of("connection.view.msaccess.filechooser.title");

    public SelectFileConnectionPropertiesForm(final DbmsRegistry.FileBasedDbms fileBasedDbms) {
        val fileChooserField = FileChooserFormField.builder()
                .title(TranslatableText.of(MS_ACCESS_FILE_TITLE))
                .fileChooserTitle(TranslatableText.of(MS_ACCESS_FILE_CHOOSER_TITLE))
                .prompt(DisplayableText.of(fileBasedDbms.getFile().getAbsolutePath()))
                .fileChooserExtensionFilter(new FileChooser.ExtensionFilter("Microsoft Access Files", "*.mdb", "*.accdb"))
                .build();

        VBox.setMargin(fileChooserField, new Insets(25));

        this.getChildren().addAll(fileChooserField);
    }
}
