package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class FileChooserFormField extends FormField<HBox> {

    private static final I18nKey FILE_CHOOSER_TITLE = I18nKey.of("export.choose-location.text");

    private final TextField pathField;
    private final TranslatableText fileChooserTitle;
    private final Collection<FileChooser.ExtensionFilter> fileChooserExtensionFilters;

    @Builder
    public FileChooserFormField(
            @NonNull final TranslatableText title,
            @NonNull final TranslatableText fileChooserTitle,
            @Singular final Collection<FileChooser.ExtensionFilter> fileChooserExtensionFilters,
            @Nullable final File initialValue,
            @Nullable final Double prefWidth
    ) {
        super(title, new HBox());

        this.fileChooserTitle = fileChooserTitle;
        this.fileChooserExtensionFilters = Optional.ofNullable(fileChooserExtensionFilters).orElse(new ArrayList<>());

        pathField = new TextField();
        pathField.getStyleClass().add(FIELD_STYLE_CLASS);
        pathField.setStyle("" +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 0; " +
                "-fx-max-height: 48; " +
                "-fx-min-height: 48; " +
                "-fx-background-color: -transparent, -fx-text-box-border, -fx-control-inner-background; "
        );

        val searchFileButton = new IconButton(IconButton.Icon.SELECT_FILE);
        searchFileButton.setOnAction(() -> showFileChooser()
                .ifPresent(file -> pathField.setText(file.getPath())));

        HBox.setHgrow(pathField, Priority.ALWAYS);
        value.getChildren().addAll(searchFileButton, pathField);
        Optional.ofNullable(prefWidth).ifPresent(value::setPrefWidth);

        value.setStyle("" +
                "-fx-border-color: #b0afaf; " +
                "-fx-border-width: 1; " +
                "-fx-max-height: 48; " +
                "-fx-min-height: 48; " +
                "-fx-background-color: -fx-shadow-highlight-color, -fx-text-box-border, -fx-control-inner-background; " +
                "-fx-background-insets: 0, 1, 2; " +
                "-fx-background-radius: 3, 2, 2;");
//        value.setStyle("-fx-border-color: #b0afaf; -fx-border-width: 1; -fx-max-height: 48; -fx-min-height: 48");

        /*
        .form-field {
    -fx-padding: 2 2 2 10;
    -fx-max-height: 48;
    -fx-min-height: 48;
    -fx-border-color: #b0afaf;
    -fx-border-width: 1;
    -fx-border-radius: 0;
    -fx-text-fill: siard-text-color-black;
}
         */
    }

    private Optional<File> showFileChooser() {
        @NonNull val stage = value.getScene().getWindow();

        val fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle.getText());

        Optional.ofNullable(pathField.getText())
                .map(File::new)
                .filter(File::isFile)
                .ifPresent(file -> {
                    fileChooser.setInitialFileName(file.getName());
                    fileChooser.setInitialDirectory(file.getParentFile());
                });

        fileChooser.getExtensionFilters().addAll(fileChooserExtensionFilters);

        return Optional.ofNullable(fileChooser.showOpenDialog(stage));
    }
}
