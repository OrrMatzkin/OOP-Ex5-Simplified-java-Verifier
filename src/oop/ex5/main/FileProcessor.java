package oop.ex5.main;

import java.io.IOError;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor {


    final static String INT = "int";
    final static String DOUBLE = "double";
    final static String BOOLEAN = "boolean";
    final static String CHAR = "char";
    final static String STRING = "String";
    final static String VOID = "void";
    final static String FINAL = "final";
    final static String IF = "if";
    final static String WHILE = "while";
    final static String RETURN = "return";



//    private enum Form {
//        CLOSING_BRACKET("^ *} *$"),
//        IF_CONDITION("^ if *( *\\. *) *")
//        DECLARATION("^([_a-zA-Z]+)(\\d*\\w*)");
//
//        private final Pattern pattern;
//
//        Form(String regex){
//            this.pattern = Pattern.compile(regex);
//        }
//    }

    private List<String> fileContent;


    FileProcessor(List<String> fileContent) {
        this.fileContent = fileContent;
    }


    public void process() throws Exception {
        // extract the line's first word (or {, })
        Pattern pattern = Pattern.compile("^ *(\\w+|\\{+|\\}+)");
        Matcher matcher;


        for (String line: this.fileContent) {
            if (line.equals("")) {
                continue;
            }
            if (!line.endsWith(";")) {
                System.err.println(line + " ; is missing");
                continue;
            }

            // trim the ';' from the end of line
            line = line.substring(0, line.length()-1);

            matcher = pattern.matcher(line);
            // in case the first word matches an appropriate sjava saved word
            if (matcher.find()) {
                switch (matcher.group(1)) {
                    case INT, DOUBLE, STRING, CHAR, BOOLEAN:
                        checkDeclaration(line);
                        break;
                }
            }
        }
    }

    private boolean checkDeclaration (String line) throws Exception {
        // split the command into words (without spaces)
        String[] splitted = line.trim().split("\\s+");

        if (splitted.length != 2 && splitted.length != 4) {
            System.err.println(line + " - wrong declaration structure");
            return false;
        }

        String type = splitted[0];
        String argument = splitted[1];
        // check argument's validity
        checkArgument(argument);
        if (splitted.length == 2) {
            // good!
        }
        else {
            String operator = splitted[2];
            if (!operator.equals("=")) {
                System.err.println("missing =");
                return false;
            }
            ch
        }
        return true;
    }

    private boolean checkArgument (String argument) {
        if (Pattern.compile("^\\d").matcher(argument).find()) {
            System.err.println("starts with a digit");
            return false;
        } else if (Pattern.compile("^_{1}$").matcher(argument).find()) {
            System.err.println("starts with a single underscore");
            return false;
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(argument).find()) {
            System.err.println("contains illegal chars");
            return false;
        } else {
            return true;
        }
    }











}
