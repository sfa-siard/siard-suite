package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
    private final HBox content;
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
            @Nullable final Consumer<File> onNewUserInput,
            @Nullable final Boolean deactivable
    ) {
        super(title, hint, validators, deactivable);

        this.fileChooserTitle = fileChooserTitle;
        this.fileChooserExtensionFilters = Optional.ofNullable(fileChooserExtensionFilters).orElse(new ArrayList<>());

        pathField = new TextField();
        pathField.getStyleClass().addAll(FIELD_STYLE_CLASS, TRANSPARENT_STYLE_CLASS);
        Optional.ofNullable(prompt).ifPresent(displayableText -> pathField.setPromptText(displayableText.getText()));
        Optional.ofNullable(initialValue).ifPresent(file -> pathField.setText(file.getAbsolutePath()));

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

        content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.getStyleClass().add(FIELD_STYLE_CLASS);

        HBox.setHgrow(pathField, Priority.ALWAYS);
        content.getChildren().addAll(searchFileButton, pathField);
        Optional.ofNullable(prefWidth).ifPresent(content::setPrefWidth);

        this.getChildren().setAll(this.title, content, validationMsg);
    }

    public File getValue() {
        return new File(pathField.getText());
    }

    @Override
    public void setValue(File newValue) {
        pathField.setText(newValue.getAbsolutePath());
    }

    @Override
    protected Node getContentNode() {
        return content;
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
