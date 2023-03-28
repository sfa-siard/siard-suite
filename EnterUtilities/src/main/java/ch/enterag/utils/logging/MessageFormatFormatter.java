/*== MessageFormatFormatter.java =======================================
MessageFormatFormatter implements a simple customizable formatter. 
Version     : $Id: MessageFormatFormatter.java 461 2015-12-18 09:47:55Z hartwig $
Application : Logging Utilities
Description : MessageFormatFormatter implements a simple customizable formatter.
              It retrieves its format from the property
              ch.enterag.utils.logging.MessageFormatFormatter.pattern.
              It is inspired by the example by R. J. Lorimer at
              http://www.javalobby.org/java/forums/t18515.html.
------------------------------------------------------------------------
Copyright  : 2010, Enter AG, Zurich, Switzerland
Created    : 16.04.2010, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.logging;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/*====================================================================*/

/**
 * MessageFormatFormatter implements a simple customizable formatter.
 * It retrieves its format from the property
 * ch.enterag.utils.logging.MessageFormatFormatter.pattern
 * containing a pattern for MessageFormat.
 * <p>
 * It is inspired by the example by R. J. Lorimer at
 * http://www.javalobby.org/java/forums/t18515.html.
 *
 * @author Hartwig Thomas
 */
public class MessageFormatFormatter extends Formatter {
    /**
     * default format
     */
    private final String sDEFAULT_FORMAT = "{1,choice,300#D|700#C|800#I|900#W|1000#E} {3,date,yyyy.MM.dd HH:mm:ss}: {4}\n";

    /*------------------------------------------------------------------*/

    /**
     * constructor
     */
    public MessageFormatFormatter() {
        super();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * format consults LogManager property for message format.
     */
    @Override
    public String format(LogRecord record) {
        Object[] arguments = new Object[5];
        arguments[0] = record.getLoggerName();
        arguments[1] = Integer.valueOf(record.getLevel().intValue());
        arguments[2] = Thread.currentThread().getName();
        arguments[3] = new Date(record.getMillis());
        arguments[4] = record.getMessage();
        /* message format must always be compiled "on the fly" to permit changes at run time */
        String sFormat = LogManager.getLogManager().getProperty(MessageFormatFormatter.class.getName() + ".pattern");
        /* if the property does not exist then use the default format */
        if ((sFormat == null) || (sFormat.trim().length() == 0))
            sFormat = sDEFAULT_FORMAT;
        MessageFormat mf = new MessageFormat(sFormat);
        String s = mf.format(arguments);
        return s;
    } /* format */

} /* class MessageFormatFormatter */
