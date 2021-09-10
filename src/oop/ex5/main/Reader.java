package oop.ex5.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * This class 'reads' the given .sjava file, and extract it's content
 * into a list of String, in order to be able to process it easily.
 */
public class Reader {

    /**
     * The path to the sourceFile (the .sjava file).
     */
    private final String sourceFilePath;

    /**
     * A BufferedReader instance, used to read the file's content.
     */
    private BufferedReader bufferedReader = null;

    /**
     * A list of Strings, which holds the file's content (each line in
     * the .sjava file is reserved in a single list cell).
     */
    private List<String> fileContent;

    /**
     * The class's constructor.
     * @param sourceFilePath The path to the sourceFile (the .sjava file).
     * @throws FileNotFoundException In case the path to the file is invalid.
     */
    Reader(String sourceFilePath) throws FileNotFoundException {
        this.sourceFilePath = sourceFilePath;
        openReader();
    }

    /**
     * This method creates a new BufferedReader instance, and sets it as
     * a class data member.
     * @throws FileNotFoundException In case the path to the file is invalid.
     */
    public void openReader() throws FileNotFoundException {
        BufferedReader reader = null;
        // opening a reader with the given sourceFile path
        reader = new BufferedReader(new FileReader(this.sourceFilePath));
        this.bufferedReader = reader;

    }

    /**
     * this method extracts data from the commands file into an
     * array, line by line
     * or in case of invalid section structure
     */
    public void readFile() {
        List<String> fileContent = new ArrayList<>();
        // reading the first line
        String line = null;
        try {
            line = this.bufferedReader.readLine();
        } catch (IOException e) {

        }
        // Go over the rest of the file
        while (line != null) {
            // Add the line to the list
            fileContent.add(line);
            // Read the next line
            try {
                line = this.bufferedReader.readLine();
            } catch (IOException e) {
                // System.out.println("USAGE: Can't read the " +
                //        "file's first line.\n");
            }
        }
        this.fileContent = fileContent;
    }

    /**
     * A getter to the file content.
     * @return The list which holds the file content.
     */
    public List<String> getFileContent() {
        return fileContent;
    }

}
