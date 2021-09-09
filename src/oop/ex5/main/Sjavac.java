package oop.ex5.main;

import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.zip.DataFormatException;

public class Sjavac {
    public static void main(String[] args) throws FileNotFoundException, DataFormatException, Exception {
        if (args.length < 1) throw new IllegalArgumentException("Please enter the source Sjava file name.");
        List<String> fileContent = getSjavaLines(args[0]);
        CallsHandler callsHandler = CallsHandler.getSingleInstance();
        Scope scope = new Scope(fileContent, null, "Global Scope");

        scope.scan();
        callsHandler.callValidity();
        GlobalVariablesChecker.checkGlobalAssignments();
    }

    /**
     * Gets the entire file lines in order.
     * @param filePath The source Sjava file path.
     * @return List of all the lines of the file.
     * @throws DataFormatException If the data format is not valid.
     * @throws FileNotFoundException If the file does not exist.
     */
    private static List<String> getSjavaLines(String filePath)
            throws DataFormatException, FileNotFoundException {
        Reader reader = new Reader(filePath);
        reader.readFile();
        return reader.getFileContent();
    }

}