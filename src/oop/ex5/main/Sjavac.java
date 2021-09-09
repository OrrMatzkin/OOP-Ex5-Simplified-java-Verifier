package oop.ex5.main;

import java.io.FileNotFoundException;
import java.io.IOError;
import java.util.List;
import java.util.zip.DataFormatException;

public class Sjavac {
    public static void main(String[] args) {

        try {
            if (args.length < 1) throw new IllegalArgumentException("Missing s-Java file name.");
            else if (args.length > 1) throw new IllegalArgumentException("Too many arguments.");
            List<String> fileContent = getSjavaLines(args[0]);
            CallsHandler callsHandler = CallsHandler.getSingleInstance();
            Scope scope = new Scope(fileContent, null, "Global Scope");
            scope.scan();
            callsHandler.callValidity();
            GlobalVariablesChecker.checkGlobalAssignments();
            GlobalVariablesChecker.checkGlobalDeclaration();
        }
        catch (DataFormatException | FileNotFoundException | IllegalArgumentException e ) {
            System.out.println("2");
            System.err.println(e);
            return;
        }
        catch (VariableError | ScopeError | MethodError e) {
            System.out.println("1");
            e.printStackTrace();
            System.err.println(e);
            return;

        } finally {
            Variable.existingVariables.clear();
            Variable.existingArguments.clear();
            Method.allMethods.clear();
            CallsHandler.calls.clear();
            GlobalVariablesChecker.globalVariablesAssignments.clear();
            Scope.globalScope = null;
        }

        System.out.println("0");
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