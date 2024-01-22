package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
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
import java.util.function.Consumer;

public class FileChooserFormField extends FormField<File> {

    private final TextField pathField;
    private final DisplayableText fileChooserTitle;
    private final Collection<FileChooser.ExtensionFilter> fileChooserExtensionFilters;

    @Builder
    public FileChooserFormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @NonNull final DisplayableText fileChooserTitle,
            @Nullable final DisplayableText prompt,
            @Singular final Collection<FileChooser.ExtensionFilter> fileChooserExtensionFilters,
            @Singular final Collection<Validator<File>> validators,
            @Nullable final File initialValue,
            @Nullable final Double prefWidth,
            @Nullable final Consumer<File> onNewUserInput
    ) {
        super(title, hint, validators);

        this.fileChooserTitle = fileChooserTitle;
        this.fileChooserExtensionFilters = Optional.ofNullable(fileChooserExtensionFilters).orElse(new ArrayList<>());

        pathField = new TextField();
        Optional.ofNullable(prompt).ifPresent(displayableText -> pathField.setPromptText(displayableText.getText()));
        pathField.getStyleClass().add(FIELD_STYLE_CLASS);
        pathField.setStyle("" +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 0; " +
                "-fx-max-height: 48; " +
                "-fx-min-height: 48; " +
                "-fx-background-color: -transparent, -fx-text-box-border, -fx-control-inner-background; "
        );
        Optional.ofNullable(onNewUserInput)
                .ifPresent(stringConsumer -> this.pathField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue && oldValue && !hasInvalidValueAndIfSoShowValidationMessage()) {
                                stringConsumer.accept(getValue());
                            }
                        }
                ));

        val searchFileButton = new IconButton(IconButton.Icon.SELECT_FILE);
        searchFileButton.setOnAction(() -> showFileChooser()
                .ifPresent(file -> {
                    pathField.setText(file.getPath());

                    Optional.ofNullable(onNewUserInput)
                            .ifPresent(stringConsumer -> {
                                        if (!hasInvalidValueAndIfSoShowValidationMessage()) {
                                            stringConsumer.accept(getValue());
                                        }
                                    }
                            );
                }));

        val value = new HBox();
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

        this.getChildren().setAll(this.title, value, validationMsg);
    }

    public File getValue() {
        return new File(pathField.getText());
    }

    @Override
    public void setValue(File newValue) {
        pathField.setText(newValue.getAbsolutePath());
    }

    private Optional<File> showFileChooser() {
        @NonNull val stage = title.getScene().getWindow();

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
