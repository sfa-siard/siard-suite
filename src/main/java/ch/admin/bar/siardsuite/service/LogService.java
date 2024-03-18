package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.util.CastHelper;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import lombok.val;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Service providing functionality related to logging.
 */
public class LogService {

    /**
     * Retrieves the log file associated with the current logging configuration.
     *
     * @return The log file.
     * @throws IllegalStateException If no log file is available.
     */
    public File getLogFile() {
        return findLogFile();
    }

    private static File findLogFile() {
        val fileAppender = findFileAppender();

        val file = new File(fileAppender.getFile());

        return file;
    }

    private static FileAppender findFileAppender() {
        val context = (LoggerContext) LoggerFactory.getILoggerFactory();

        return context.getLoggerList().stream()
                .flatMap(logger -> toStream(logger.iteratorForAppenders()))
                .flatMap(CastHelper.tryCastInStream(FileAppender.class))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No file-appender available"));
    }

    private static <T> Stream<T> toStream(final Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
