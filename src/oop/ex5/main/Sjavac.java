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


//TODO: In a case of an un-initialized global variable
// (meaning it is not assigned a value anywhere outside a method),
// all methods may refer to it (regardless of their location in relation to its declaration),
// but every method using it (in an assignment, as an argument to a method call)
// must first assign a value to the global variable itself
// (even if it was assigned a value in some other method).


