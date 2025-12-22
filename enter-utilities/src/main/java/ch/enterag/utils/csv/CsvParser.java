package ch.enterag.utils.csv;

import java.io.*;

/**
 * A very simple CSV parser released under a commercial-friendly license.
 * This just implements splitting a single line into fields.
 *
 * The purpose of the CSVParser is to take a single string and parse it into
 * its elements based on the delimiter, quote and escape characters.
 *
 * The CSVParser has grown organically based on user requests and does not truely match
 * any current requirements (though it can be configured to match or come close).  There
 * is no plans to change this as it will break existing requirements.  Consider using
 * the RFC4180Parser for less configurablility but closer match to the RFC4180 requirements.
 *
 * @author Glen Smith
 * @author Rainer Pruy
 * Adapted from http://opencsv.sourceforge.net/ by
 * @Autor Hartwig Thomas
 */

/**
 * Enumeration used to tell the CSVParser what to consider null.
 * <ul>
 * <li>EMPTY_SEPARATORS - two sequential separators are null.</li>
 * <li>EMPTY_QUOTES - two sequential quotes are null</li>
 * <li>BOTH - both are null</li>
 * <li>NEITHER - default.  Both are considered empty string.</li>
 * </ul>
 */
public interface CsvParser
{
  public enum CSVReaderNullFieldIndicator {
    EMPTY_SEPARATORS,
    EMPTY_QUOTES,
    BOTH,
    NEITHER;
  }

  /**
   * The default separator to use if none is supplied to the constructor.
   */
  char DEFAULT_SEPARATOR = ',';

  /**
   * The average size of a line read by opencsv (used for setting the size of StringBuilders).
   */
  int INITIAL_READ_SIZE = 1024;

  /**
   * In most cases we know the size of the line we want to read.  In that case we will set the initial read
   * to that plus an buffer size.
   */
  int READ_BUFFER_SIZE = 128;

  /**
   * The default quote character to use if none is supplied to the
   * constructor.
   */
  char DEFAULT_QUOTE_CHARACTER = '"';

  /**
   * The default escape character to use if none is supplied to the
   * constructor.
   */
  char DEFAULT_ESCAPE_CHARACTER = '\\';

  /**
   * The default strict quote behavior to use if none is supplied to the
   * constructor.
   */
  boolean DEFAULT_STRICT_QUOTES = false;

  /**
   * The default leading whitespace behavior to use if none is supplied to the
   * constructor.
   */
  boolean DEFAULT_IGNORE_LEADING_WHITESPACE = true;

  /**
   * If the quote character is set to null then there is no quote character.
   */
  boolean DEFAULT_IGNORE_QUOTATIONS = false;

  /**
   * This is the "null" character - if a value is set to this then it is ignored.
   */
  char NULL_CHARACTER = '\0';

  /**
   * Denotes what field contents will cause the parser to return null:  EMPTY_SEPARATORS, EMPTY_QUOTES, BOTH, NEITHER (default)
   */
  CSVReaderNullFieldIndicator DEFAULT_NULL_FIELD_INDICATOR = CSVReaderNullFieldIndicator.NEITHER;

  /**
   * @return The default separator for this parser.
   */
  char getSeparator();

  /**
   * @return The default quotation character for this parser.
   */
  char getQuotechar();

  /**
   * @return True if something was left over from last call(s)
   */
  boolean isPending();

  /**
   * Parses an incoming String and returns an array of elements.
   * This method is used when the data spans multiple lines.
   *
   * @param nextLine Current line to be processed
   * @return The comma-tokenized list of elements, or null if nextLine is null
   * @throws IOException If bad things happen during the read
   */
  String[] parseLineMulti(String nextLine) throws IOException;

  /**
   * Parses an incoming String and returns an array of elements.
   * This method is used when all data is contained in a single line.
   *
   * @param nextLine Line to be parsed.
   * @return The list of elements, or null if nextLine is null
   * @throws IOException If bad things happen during the read
   */
  String[] parseLine(String nextLine) throws IOException;

  /**
   * @return The null field indicator.
   */
  CSVReaderNullFieldIndicator nullFieldIndicator();
}
