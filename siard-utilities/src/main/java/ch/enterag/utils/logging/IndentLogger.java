/**
Description : IndentLogger wraps an SLF4J logger and adds automatic
              indentation, automatic method name detection on entry and exit.
------------------------------------------------------------------------
Copyright  : 2010, 2012, 2016 Enter AG, Rüti ZH, Switzerland
             2026 Puzzle ITC GmbH, Switzerland
Created    : 15.04.2010, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.logging;

import ch.enterag.utils.EU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLWarning;
import java.util.Enumeration;
import java.util.Properties;

/** IndentLogger wraps an SLF4J logger and adds automatic
 * indentation, automatic method name detection on entry and exit.
 * @author Hartwig Thomas
 */
public class IndentLogger {
    /** amount to increase/decrease indentation */
    private static final int INDENT_AMOUNT = 2;
    /** tag for logged "events" */
    private static final String TAG_EVENT = "-- ";
    /** tag for logged method entry */
    private static final String TAG_ENTER = ">> ";
    /** tag for logged method exit */
    private static final String TAG_EXIT = "<< ";

    /** static indent property. */
    private static StringBuilder stringBuilder = new StringBuilder();

    /** wrapped SLF4J logger */
    private final Logger logger;

    /**
     * constructor
     * @param loggerName logger loggerName
     */
    protected IndentLogger(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * returns current indent amount.
     @return indent amount.
     */
    public int getIndent() {
        return stringBuilder.toString()
                            .length();
    }

    /**
     * sets indent amount.
     @param indent indent amount.
     */
    public synchronized void setIndent(int indent) {
        int previousIndent = getIndent();
        if (indent < 0) indent = 0;
        stringBuilder.setLength(indent);
        for (int i = previousIndent; i < indent; i++)
            stringBuilder.setCharAt(i, ' ');
    }

    /** returns the parent of the java.util.logging logger with the same name.
     @return parent logger.
     */
    public java.util.logging.Logger getParent() {
        return java.util.logging.Logger.getLogger(logger.getName())
                                       .getParent();
    }

    /** logs an indented message object with the DEBUG level.
     @param message the message to log.
     */
    public synchronized void event(String message) {
        logger.debug("{}{}{}", stringBuilder.toString(), TAG_EVENT, message);
    }

    /** logs an indented method and its parameters with the TRACE level
     * and increases indentation.
     * @param params method parameter values to be logged.
     */
    public synchronized void enter(Object... params) {
        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder(getCallingMethod(3));
            sb.append("(");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(params[i]);
            }
            sb.append(")");
            logger.trace("{}{}{}", stringBuilder.toString(), TAG_ENTER, sb);
            setIndent(getIndent() + INDENT_AMOUNT);
        }
    }

    /** decreases indentation and logs an indented method and its return
     * value with the TRACE level.
     */
    public synchronized void exit() {
        if (logger.isTraceEnabled()) {
            setIndent(getIndent() - INDENT_AMOUNT);
            logger.trace("{}{}{}", stringBuilder.toString(), TAG_EXIT, getCallingMethod(3));
        }
    }

    /** decreases indentation and logs an indented method and its return
     * value with the TRACE level.
     @param object return value to be logged.
     */
    public synchronized void exit(Object object) {
        if (logger.isTraceEnabled()) {
            setIndent(getIndent() - INDENT_AMOUNT);
            logger.trace("{}{}{}({})", stringBuilder.toString(), TAG_EXIT, getCallingMethod(3), object);
        }
    }

    /** logs the given properties.
     @param title properties' title in log.
     @param properties properties to be logged.
     */
    public final void properties(String title, Properties properties) {
        event(title + ":");
        setIndent(getIndent() + INDENT_AMOUNT);
        for (Enumeration<?> enumProperty = properties.propertyNames(); enumProperty.hasMoreElements(); ) {
            logger.info("  {}: {}", enumProperty.nextElement(), properties.getProperty((String) enumProperty.nextElement()));
        }
        setIndent(getIndent() - INDENT_AMOUNT);
    }

    /** logs the current system properties.
     */
    public final void systemProperties() {
        Runtime rt = Runtime.getRuntime();
        logger.info("free memory: {}", rt.freeMemory());
        logger.info("total memory: {}", rt.totalMemory());
        logger.info("maximum memory: {}", rt.maxMemory());
        properties("System properties", System.getProperties());
    }

    /** logs an error with the DEBUG level.
     @param e error to log.
     */
    public synchronized void error(Error e) {
        if (logger.isDebugEnabled()) event(EU.getErrorMessage(e));
    }

    /** logs an exception with the DEBUG level.
     @param e exception to log.
     */
    public synchronized void exception(Exception e) {
        if (logger.isDebugEnabled()) event(EU.getExceptionMessage(e));
    }

    /** logs an SQLWarning with the DEBUG level.
     @param sqlWarning warning to log.
     */
    public synchronized void sqlwarning(SQLWarning sqlWarning) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = null;
            for (; sqlWarning != null; sqlWarning = sqlWarning.getNextWarning()) {
                if (sb == null) sb = new StringBuilder();
                else sb.append("\n");
                sb.append("Warning " + sqlWarning.getMessage() + " / SQL State " + sqlWarning.getSQLState() + " / Error Code " + sqlWarning.getErrorCode());
            }
            if (sb != null) event(sb.toString());
        }
    }

    /** logs a message with the INFO level.
     @param message message to log.
     */
    public void info(String message) {
        logger.info(message);
    }

    /** logs a message with the WARN level.
     @param message message to log.
     */
    public void warning(String message) {
        logger.warn(message);
    }

    /** logs a message with the ERROR level.
     @param message message to log.
     */
    public void severe(String message) {
        logger.error(message);
    }

    /** logs a message with the DEBUG level.
     @param message message to log.
     */
    public void config(String message) {
        logger.debug(message);
    }

    /** logs a message with the DEBUG level.
     @param message message to log.
     */
    public void fine(String message) {
        logger.debug(message);
    }

    /** logs a message with the DEBUG level.
     @param message message to log.
     */
    public void finer(String message) {
        logger.debug(message);
    }

    /** logs a message with the TRACE level.
     @param message message to log.
     */
    public void finest(String message) {
        logger.trace(message);
    }

    /** returns the full name of the calling method which has given depth
     * on stack.
     * @param depth depth on stack.
     * @return full name of the calling method.
     */
    private String getCallingMethod(int depth) {

        StackTraceElement[] stackTraceElements = Thread.currentThread()
                                          .getStackTrace();
        return stackTraceElements[depth].getClassName() + "." + stackTraceElements[depth].getMethodName();
    }

    /** returns a new IndentLogger backed by an SLF4J logger.
     * @param loggerName name of the logger.
     * @return the logger
     */
    public static IndentLogger getIndentLogger(String loggerName) {
        return new IndentLogger(loggerName);
    }

}
