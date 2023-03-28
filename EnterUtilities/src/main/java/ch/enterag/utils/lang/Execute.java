/*== Execute.java ======================================================
Execute implements execution of an external command.
Version     : $Id: Execute.java 1445 2012-02-23 14:35:21Z hartwig $
Application : Siard2
Description : Execute implements execution of an external command.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2007
Created    : 27.11.2007, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.lang;

import ch.enterag.utils.logging.IndentLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*====================================================================*/

/**
 * Execute implements execution of an external command.
 *
 * @author Hartwig Thomas
 */
public class Execute {
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * logger
     */
    private static IndentLogger _il = IndentLogger.getIndentLogger(Execute.class.getName());
    /**
     * stdOut result of last execute
     */
    private String _sStdOut = null;

    /**
     * @return stdOut from last execute
     */
    public String getStdOut() {
        return _sStdOut;
    }

    /**
     * stdErr result of last execute
     */
    private String _sStdErr = null;

    /**
     * @return stdErr from last execute
     */
    public String getStdErr() {
        return _sStdErr;
    }

    /**
     * result of last execute
     */
    private int _iResult = -1;

    /**
     * @return result of last execute.
     */
    public int getResult() {
        return _iResult;
    }

    /*------------------------------------------------------------------*/

    /**
     * split a version string into parts separated by non-alphanumeric
     * characters.
     *
     * @param sVersion: version string.
     * @return list of parts.
     */
    private static List<String> splitVersion(String sVersion) {
        List<String> listVersion = new ArrayList<String>();
        String sPart = "";
        for (int i = 0; i < sVersion.length(); i++) {
            char c = sVersion.charAt(i);
            if (Character.isLetterOrDigit(c))
                sPart = sPart + c;
            else {
                if (sPart.length() > 0)
                    listVersion.add(sPart);
                sPart = "";
            }
        }
        if (sPart.length() > 0)
            listVersion.add(sPart);
        return listVersion;
    } /* splitVersion */

    /*------------------------------------------------------------------*/

    /**
     * leVersion returns true, if sVersion1 is less than sVersion2.
     * The version parts are compared as integers, if they are integers and
     * as strings otherwise.
     *
     * @param sVersion1 first version string to be compared.
     * @param sVersion2 second version string to be compared.
     * @return true, if sVersion1 is less than sVersion2.
     */
    public static boolean ltVersion(String sVersion1, String sVersion2) {
        _il.enter(sVersion1, sVersion2);
        boolean bEqual = true;
        boolean bLess = false;
        List<String> listVersion1 = splitVersion(sVersion1);
        List<String> listVersion2 = splitVersion(sVersion2);
        for (int i = 0; bEqual && (i < listVersion1.size()) && (i < listVersion2.size()); i++) {
            String s1 = listVersion1.get(i);
            String s2 = listVersion2.get(i);
            int comp = s1.compareTo(s2);
            try {
                int i1 = Integer.parseInt(s1);
                int i2 = Integer.parseInt(s2);
                comp = Integer.compare(i1, i2);
            } catch (NumberFormatException nfe) {
            }
            if (comp < 0) {
                bLess = true;
                bEqual = false;
            } else {
                bLess = false;
                if (comp != 0)
                    bEqual = false;
            }
        }
        _il.exit(Boolean.valueOf(bLess));
        return bLess;
    } /* ltVersion */

    /*------------------------------------------------------------------*/

    /**
     * compares the current run-time JAVA version with the given version.
     * N.B.: The leading "1." was dropped with JAVA 9!
     *
     * @param sVersion: version to be compared.
     * @return true, if the runtime JAVA version is less than the given one
     */
    public static boolean isJavaVersionLessThan(String sVersion) {
        String sJavaVersion = System.getProperty("java.version");
        if (sJavaVersion.startsWith("1."))
            sJavaVersion = sJavaVersion.substring(2);
        if (sVersion.startsWith("1."))
            sVersion = sVersion.substring(2);
        return ltVersion(sJavaVersion, sVersion);
    } /* isJavaVersionLessThan */

    /*------------------------------------------------------------------*/

    /**
     * @return true, if the OS is Windows.
     */
    public static boolean isOsWindows() {
        boolean bIsOsWindows = false;
        String sOsName = System.getProperty("os.name");
        if (sOsName != null)
            bIsOsWindows = sOsName.startsWith("Windows");
        return bIsOsWindows;
    } /* isOsWindows */

    /*------------------------------------------------------------------*/

    /**
     * @return true, if the OS is LINUX.
     */
    public static boolean isOsLinux() {
        boolean bIsOsLinux = false;
        String sOsName = System.getProperty("os.name");
        if (sOsName != null)
            bIsOsLinux = sOsName.startsWith("Linux");
        return bIsOsLinux;
    } /* isOsLinux */

    /*------------------------------------------------------------------*/

    /**
     * capture the output from the given input stream and return it as
     * as string.
     *
     * @param is input stream.
     * @return captured output.
     * @throws IOException if an I/O error occurred.
     */
    private String captureOutput(InputStream is)
            throws IOException {
        InputStreamReader isrdrOut = new InputStreamReader(is);
        StringWriter swOut = new StringWriter();
        for (int i = isrdrOut.read(); i != -1; i = isrdrOut.read())
            swOut.write(i);
        swOut.close();
        return swOut.toString();
    } /* captureOutput */

    /*------------------------------------------------------------------*/

    /**
     * redirect the characters from the given reader to the given
     * output stream.
     *
     * @param rdr redirected input.
     * @param os  output stream to receive redirected input.
     * @throws IOException if an I/O error occurred.
     */
    private void redirectInput(Reader rdr, OutputStream os)
            throws IOException {
        _il.event("stdin redirected");
        OutputStreamWriter oswr = new OutputStreamWriter(os);
        for (int i = rdr.read(); i != -1; i = rdr.read())
            oswr.write(i);
        oswr.close();
    } /* redirectInput */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param sCommand external command line to be executed.
     * @return return code from command.
     */
    private int run(String sCommand) {
        _il.enter(sCommand);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(sCommand);
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (IOException ie) {
            _il.exception(ie);
        } catch (InterruptedException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param sCommand external command line to be executed.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String sCommand) {
        Execute ex = new Execute();
        ex._iResult = ex.run(sCommand);
        return ex;
    } /* execute */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param sCommand external command line to be executed.
     * @param rdrInput redirected input to be used.
     * @return return code from command.
     */
    private int run(String sCommand, Reader rdrInput) {
        _il.enter(sCommand, rdrInput);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(sCommand);
            redirectInput(rdrInput, proc.getOutputStream());
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (InterruptedException ie) {
            _il.exception(ie);
        } catch (IOException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param sCommand external command line to be executed.
     * @param rdrInput redirected input to be used.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String sCommand, Reader rdrInput) {
        Execute ex = new Execute();
        ex._iResult = ex.run(sCommand, rdrInput);
        return ex;
    } /* execute */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command with arguments.
     *
     * @param asCommand external command and arguments to be executed.
     * @return return code from command.
     */
    private int run(String[] asCommand) {
        _il.enter((Object[]) asCommand);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(asCommand);
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (InterruptedException ie) {
            _il.exception(ie);
        } catch (IOException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param asCommand external command and arguments to be executed.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String[] asCommand) {
        Execute ex = new Execute();
        ex._iResult = ex.run(asCommand);
        return ex;
    } /* execute */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command with arguments.
     *
     * @param asCommand            external command and arguments to be executed.
     * @param fileWorkingDirectory working directory for external command.
     * @return return code from command.
     */
    private int run(String[] asCommand, File fileWorkingDirectory) {
        _il.enter((Object[]) asCommand);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(asCommand, null, fileWorkingDirectory);
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (InterruptedException ie) {
            _il.exception(ie);
        } catch (IOException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param asCommand            external command and arguments to be executed.
     * @param fileWorkingDirectory working directory for external command.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String[] asCommand, File fileWorkingDirectory) {
        Execute ex = new Execute();
        ex._iResult = ex.run(asCommand, fileWorkingDirectory);
        return ex;
    } /* execute */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command with arguments.
     *
     * @param asCommand            external command and arguments to be executed.
     * @param fileWorkingDirectory working directory for external command.
     * @param rdrInput             redirected input to be used.
     * @return return code from command.
     */
    private int run(String[] asCommand, File fileWorkingDirectory, Reader rdrInput) {
        _il.enter((Object[]) asCommand);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(asCommand, null, fileWorkingDirectory);
            redirectInput(rdrInput, proc.getOutputStream());
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (InterruptedException ie) {
            _il.exception(ie);
        } catch (IOException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line.
     *
     * @param asCommand            external command and arguments to be executed.
     * @param fileWorkingDirectory working directory for external command.
     * @param rdrInput             redirected input to be used.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String[] asCommand, File fileWorkingDirectory, Reader rdrInput) {
        Execute ex = new Execute();
        ex._iResult = ex.run(asCommand, fileWorkingDirectory, rdrInput);
        return ex;
    } /* execute */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command with arguments.
     *
     * @param asCommand external command and arguments to be executed.
     * @param rdrInput  redirected input to be used.
     * @return return code from command.
     */
    private int run(String[] asCommand, Reader rdrInput) {
        _il.enter((Object[]) asCommand, rdrInput);
        int iReturn = -1;
        try {
            Process proc = Runtime.getRuntime().exec(asCommand);
            redirectInput(rdrInput, proc.getOutputStream());
            _sStdOut = captureOutput(proc.getInputStream());
            _il.event("stdout: " + _sStdOut.toString());
            _sStdErr = captureOutput(proc.getErrorStream());
            _il.event("stderr: " + _sStdErr.toString());
            iReturn = proc.waitFor();
        } catch (InterruptedException ie) {
            _il.exception(ie);
        } catch (IOException ie) {
            _il.exception(ie);
        }
        _il.exit(String.valueOf(iReturn));
        return iReturn;
    } /* run */

    /*------------------------------------------------------------------*/

    /**
     * executes an external command line with redirected input from
     * a reader.
     *
     * @param asCommand command and arguments to be executed.
     * @param rdrInput  redirected input to be used.
     * @return execution context with result, stdout, and stderr.
     */
    public static Execute execute(String[] asCommand, Reader rdrInput) {
        Execute ex = new Execute();
        ex._iResult = ex.run(asCommand, rdrInput);
        return ex;
    } /* execute */

} /* Execute */
