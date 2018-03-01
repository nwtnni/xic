package xic;

import java.io.File;
import java.io.IOException;

/**
 * Filename and filepath manipulation methods for xic.
 */
public class FilenameUtils {
    private static final int NOT_FOUND = -1;
    public static final char SYSTEM_SEPARATOR = File.separatorChar;
    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * Returns true if character is the directory separator character:
     *  '/' on Unix
     *  '\\' on Windows
     */
    public static boolean isSeparator(char ch) {
        return ch == SYSTEM_SEPARATOR;
    }

    /**
     * Returns true if character is the extension separator '.'
     */
    public static boolean isExtSeparator(char ch) {
        return ch == EXTENSION_SEPARATOR;
    }

    /**
     * Returns the index of the last system separator character in 
     * filename. Returns -1 if none exists.
     */
    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        return filename.lastIndexOf(SYSTEM_SEPARATOR);
    }

    /**
     * Returns the index of the last extension separator in a filename. 
     * This method checks that there is no directory separator after the last 
     * dot in filename. Returns -1 if none exists.
     */
    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int ext = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSep = indexOfLastSeparator(filename);
        return lastSep < ext ? ext : NOT_FOUND;
    }

    /**
     * Gets the extension from a filename. This method checks that there 
     * is no directory separator after the last dot in filename.
     */
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }

        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * Removes the extension from a filename. This method checks that there 
     * is no directory separator after the last dot in filename.
     */
    public static String removeExtension(final String filename) {
        if (filename == null) {
            return null;
        }

        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Changes the file extension of filename to be ext, removing an
     * existing extension on filename if necessary.
     * 
     * If the first character of ext is not the extension separator '.',
     * then a '.' will be inserted in between the filename and the ext.
     */
    public static String setExtension(String filename, final String ext) {
        filename = removeExtension(filename);
        if (ext.length() == 0) {
            return filename;
        } 
        final char ch = ext.charAt(0);
        if (isExtSeparator(ch)) {
            return filename + ext;
        } else {
            return filename + EXTENSION_SEPARATOR + ext;
        }
    }

    /**
     * Gets the path to a file including the last directory separator.
     */
    public static String getFullPath(final String filename) {
        if (filename == null) {
            return null;
        }
        final int prefix = indexOfLastSeparator(filename);
        if (prefix == NOT_FOUND) {
            return "";
        }
        return filename.substring(0, prefix + 1);
    }

    /**
     * Prepends a basePath to a filename without normalizing the paths.
     */
    public static String concat(final String basePath, final String filename) {
        final int len = basePath.length();
        if (len == 0) {
            return filename;
        }
        final char ch = basePath.charAt(len - 1);
        if (isSeparator(ch)) {
            return basePath + filename;
        } else {
            return basePath + SYSTEM_SEPARATOR + filename;
        }
    }

    /**
     * Returns a File located at filename, making directories as needed.
     * Throws IOException if operation fails.
     */
    public static File makeFile(String filename) throws IOException {
        try {
            File file = new File(filename);
            if (file.getParentFile().mkdirs()) {
                file.createNewFile();
                // file.setWritable(true);
            }
            return file;
        } catch (NullPointerException e) {
            throw new IOException("Failed to create output file.");
         }
    }
    
}