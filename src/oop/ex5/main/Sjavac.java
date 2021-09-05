package oop.ex5.main;


import java.io.FileNotFoundException;
import java.io.IOError;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.zip.DataFormatException;

public class Sjavac {
    public static void main(String[] args) throws Exception {

        // check if one argument was given
        if (args.length != 1) {
            throw new IllegalAccessException();
        }

        Reader reader = new Reader(args[0]);
        reader.readFile();
        List<String> fileContent = reader.getFileContent();
        // System.out.println(fileContent);
//        FileProcessor f = new FileProcessor(fileContent);
//        f.process();
//        Method method = new Method(fileContent, null);
        Scope scope = new Scope(fileContent, null);
        scope.scan();
    }


}


