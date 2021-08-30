package oop.ex5.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class Reader {

    private final String sourceFilePath;
    private BufferedReader bufferedReader = null;
    private List<String> fileContent;


    Reader(String sourceFilePath) throws DataFormatException, FileNotFoundException {
        this.sourceFilePath = sourceFilePath;
        openReader();
    }

    public void openReader() throws DataFormatException, FileNotFoundException {
        BufferedReader reader = null;
        // opening a reader with the given sourceFile path
        reader = new BufferedReader(new FileReader(this.sourceFilePath));
        this.bufferedReader = reader;

    }

    /**
     * this method extracts data from the commands file into an
     * array, line by line
     * @throws DataFormatException in case of invalid names of subsections,
     * or in case of invalid section structure
     */
    public void readFile() throws DataFormatException {

        List<String> fileContent = new ArrayList<>();

        // reading the first line
        String line = null;
        try {
            line = this.bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("USAGE: Can't read the " +
                    "file's first line.\n");
        }

        // Go over the rest of the file
        while (line != null) {
            // Add the line to the list
            fileContent.add(line);
            // Read the next line
            try {
                line = this.bufferedReader.readLine();
            } catch (IOException e) {
                System.out.println("USAGE: Can't read the " +
                        "file's first line.\n");
            }
        }
        this.fileContent = fileContent;
    }

    public List<String> getFileContent() {
        return fileContent;
    }

}
