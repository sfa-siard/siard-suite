/*== FU.java ===========================================================
File utilities.
Application : Utilities
Description : File utilities. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2016
Created    : 25.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/*====================================================================*/

/**
 * Utility for handling files.
 *
 * @author Hartwig
 */
public class FU {
    private static int iBUFSIZ = 8192;

    /*------------------------------------------------------------------*/

    /**
     * java.nio.Files.copy is problematic, because of unpredictable Windows
     * file locking. For some reason this happens
     * This will copy source to target, overwriting target if it exists.
     *
     * @param fileSource source file.
     * @param fileTarget target file.
     * @return number of bytes copied.
     * @throws IOException if an I/O error occurred.
     */
    public static long copy(File fileSource, File fileTarget)
            throws IOException {
        long lCopied = 0;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            byte[] buf = new byte[iBUFSIZ];
            fis = new FileInputStream(fileSource);
            fos = new FileOutputStream(fileTarget);
            for (int iRead = fis.read(buf); iRead != -1; iRead = fis.read(buf))
                fos.write(buf, 0, iRead);
            fos.flush();
        } finally {
            if (fos != null) fos.close();
            if (fis != null) fis.close();
        }
        return lCopied;
    } /* copy */

    /*------------------------------------------------------------------*/

    /**
     * copy a file or folder.
     * If it is a folder, copy it recursively.
     *
     * @param fileSource source file or folder.
     * @param fileTarget target file or folder.
     * @param bReplace   true, if existing target files are to be replaced.
     * @throws IOException in an I/O error occurs.
     */
    public static void copyFiles(File fileSource, File fileTarget, boolean bReplace)
            throws IOException {
        if (fileSource.isDirectory()) {
            if (!fileTarget.exists())
                fileTarget.mkdir();
            String[] as = fileSource.list();
            for (int i = 0; i < as.length; i++)
                copyFiles(new File(fileSource, as[i]), new File(fileTarget, as[i]), bReplace);
        } else {
            if (bReplace || (!fileTarget.exists()))
                copy(fileSource, fileTarget);
        }
    } /* copyFiles */

    /*------------------------------------------------------------------*/

    /**
     * delete the file or folder.
     * If it is a folder, delete it recursively.
     *
     * @param file or folder
     * @return true, if delete was successful.
     */
    public static boolean deleteFiles(File file) {
        if (file.isDirectory()) {
            File[] afile = file.listFiles();
            for (int i = 0; i < afile.length; i++)
                deleteFiles(afile[i]);
        }
        return file.delete();
    } /* delete */

    /*------------------------------------------------------------------*/

    /**
     * convert a File to a file-URI.
     * N.B.: Uses JAVA 1.7 java.nio.Paths for correct handling of UNC file
     * and folder names.
     *
     * @param file File
     * @return file URI
     */
    public static URI toUri(File file) {
        Path path = Paths.get(file.toString());
        return path.toUri();
    } /* toUri */

    /*------------------------------------------------------------------*/

    /**
     * convert a file-URI to a File.
     * N.B.: Uses JAVA 1.7 java.nio.Paths for correct handling of UNC file
     * and folder names.
     *
     * @param uri file URI
     * @return File
     */
    public static File fromUri(URI uri) {
        Path path = Paths.get(uri);
        return path.toFile();
    } /* fromUri */

} /* FU */
