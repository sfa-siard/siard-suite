package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.errors.FailureDisplay;
import ch.admin.bar.siardsuite.framework.errors.HandlingInstruction;
import ch.admin.bar.siardsuite.framework.errors.NewFailure;
import ch.admin.bar.siardsuite.framework.errors.TypeAndMessageMatcher;
import ch.admin.bar.siardsuite.framework.errors.TypeMatcher;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.ui.RootStage;
import ch.admin.bar.siardsuite.ui.View;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.IOException;
import java.util.Arrays;

public class ServicesFacadeBuilder {

    private static final I18nKey ACCESS_DENIED_TITLE = I18nKey.of("errors.accessDenied.title");
    private static final I18nKey ACCESS_DENIED_MESSAGE = I18nKey.of("errors.accessDenied.message");

    private static final I18nKey UNKNOWN_HOST_TITLE = I18nKey.of("errors.unknownHost.title");
    private static final I18nKey UNKNOWN_HOST_MESSAGE = I18nKey.of("errors.unknownHost.message");

    private static final I18nKey CORRUPTED_SIARD_ARCHIVE_TITLE = I18nKey.of("errors.corruptedSiardFile.title");
    private static final I18nKey CORRUPTED_SIARD_ARCHIVE_MESSAGE = I18nKey.of("errors.corruptedSiardFile.message");

    private static final HandlingInstruction ACCESS_DENIED = HandlingInstruction.builder()
            .matcher(TypeAndMessageMatcher.builder()
                    .expectedExceptionType(java.sql.SQLException.class)
                    .expectedTextFragment("Access denied for user")
                    .build())
            .title(DisplayableText.of(ACCESS_DENIED_TITLE))
            .message(DisplayableText.of(ACCESS_DENIED_MESSAGE))
            .build();

    private static final HandlingInstruction UNKNOWN_HOST = HandlingInstruction.builder()
            .matcher(TypeMatcher.builder()
                    .expectedExceptionType(java.net.UnknownHostException.class)
                    .build())
            .title(DisplayableText.of(UNKNOWN_HOST_TITLE))
            .message(DisplayableText.of(UNKNOWN_HOST_MESSAGE))
            .build();

    private static final HandlingInstruction CORRUPTED_SIARD_ARCHIVE = HandlingInstruction.builder()
            .matcher(TypeAndMessageMatcher.builder()
                    .expectedExceptionType(IOException.class)
                    .expectedTextFragment("Invalid SIARD file")
                    .build())
            .title(DisplayableText.of(CORRUPTED_SIARD_ARCHIVE_TITLE))
            .message(DisplayableText.of(CORRUPTED_SIARD_ARCHIVE_MESSAGE))
            .build();

    public ServicesFacade build(final RootStage stage) {
        val archiveHandler = new ArchiveHandler();
        val dbmsRegistry = new DbmsRegistry();
        val installationService = new InstallationService();
        val userPreferences = new UserPreferences();
        val filesService = new FilesService();
        val logService = new LogService();

        val dbInteractionService = new DbInteractionService(archiveHandler);

        val failureDisplay = new ShowDialogFailureDisplay();

        val services = new ServicesFacade(
                stage,
                stage,
                failureDisplay,
                Arrays.asList(
                        archiveHandler,
                        dbmsRegistry,
                        installationService,
                        userPreferences,
                        filesService,
                        dbInteractionService,
                        logService
                ));

        failureDisplay.setDialogs(services.dialogs());

        services.errorHandler()
                .register(ACCESS_DENIED)
                .register(UNKNOWN_HOST)
                .register(CORRUPTED_SIARD_ARCHIVE);

        return services;
    }

    @Getter
    @Setter
    private static class ShowDialogFailureDisplay implements FailureDisplay {

        private Dialogs dialogs;

        @Override
        public void displayFailure(NewFailure failure) {
            dialogs.open(View.ERROR, failure);
        }
    }
}
