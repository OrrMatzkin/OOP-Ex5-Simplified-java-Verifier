package oop.ex5.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {

    private enum Type {
        INT("^int$", "^(-\\d+)$|^(\\d+)$"),
        DOUBLE("^double$", "^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|^(\\d*\\.\\d+)$|" +
                                              "^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$"),
        STRING("^String$", "^\"(.*)\"$)"),
        CHAR("^char$","^\'(.)\'$"),
        BOOLEAN("^boolean$", "^true$|^false$|^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|" +
                                                "^(\\d*\\.\\d+)$|^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$");

        Pattern typePattern;
        Pattern valuePattern;

        Type(String regexType, String regexValue){
            this.typePattern = Pattern.compile(regexType);
            this.valuePattern = Pattern.compile(regexValue);
        }
    }

    private String name;

    private int dataInt;

    private double dataDouble;

    private String dataString;

    private char dataChar;

    private boolean dataBoolean;

    private Type type;

    private boolean initialized;

    private final boolean isFinal;

    private final boolean isArgument;

    /**
     * The Contractor of the Variable.
     *
     * @param initializeLine The initializing line (Without any reserved keyword, for example: final, int..)
     * @param isArgument        True if this variable should is, else false.
     */
    Variable(String initializeLine, boolean isArgument) throws Exception {
        this.isArgument = isArgument;
        this.isFinal = findFinal(initializeLine);
        updateParameters(isFinal ? initializeLine.replaceFirst("final", "") : initializeLine);
    }

    private boolean findFinal(String initializeLine) {
        return initializeLine.startsWith("final");
    }

    private void updateParameters(String initializeLine) throws Exception {
        String[] splitted = initializeLine.split(" ");
        //TODO: check if splitted has extra spaces
        if (splitted.length >= 2) {     // without initialization only <Type> <Name>
            this.type = extractType(splitted[0]);
            this.name = extractName(splitted[1]);
        } if (splitted.length == 4) {     // with initialization <Type> <Name> <=> <Data>
            if (!splitted[2].equals("=")) {
                System.err.println("No = sign");
                throw new Exception();
            } else {
                updateData(splitted[3]);
            }
        } if (splitted.length != 2 && splitted.length !=4) throw new Exception();
    }


    /**
     * Finds the Variable Type.
     * @param typeStr The String that should hold the variable type.
     * @return the Type of the Variable.
     * @throws Exception If no type found.
     */
    private Type extractType(String typeStr) throws Exception{
        Matcher matcher;
        for (Type type : Type.values()) {
            matcher = type.typePattern.matcher(typeStr);
            if (matcher.find()) {
                return type;
            }
        }
        System.err.println("Not a variable, invalid Type");
        throw new Exception();
    }

    /**
     * gets
     * @param nameStr
     * @return
     * @throws Exception
     */
    private String extractName(String nameStr) throws Exception{
        // if the name starts with a digit
        if (Pattern.compile("^\\d").matcher(nameStr).find()) {
            System.err.println("starts with a digit");
            throw new Exception();
        // if the name is a only a single underscore
        } else if (Pattern.compile("^_$").matcher(nameStr).find()) {
            System.err.println("name with only a single underscore");
            throw new Exception();
        // if the name contains illegal characters (not letters or digits)
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(nameStr).find()) {
            System.err.println("contains illegal chars");
            throw new Exception();
        // if the name is one of the reserved keyword
        } else if (Pattern.compile("^(int|double|String|char|boolean)$").matcher(nameStr).find()){
            System.err.println("You cant use a reserved keyword");
            throw new Exception();
        } else return nameStr;
    }

    public void updateData(String dataStr) throws Exception {
        Matcher matcher = this.type.valuePattern.matcher(dataStr);
        if (!matcher.find()) {
            System.err.println("not a valid value for " + this.type.name());
            throw new Exception();
        }
        switch (this.type) {
            case INT:
                this.dataInt = Integer.parseInt(dataStr);
                break;
            case DOUBLE:
                this.dataDouble = Double.parseDouble(dataStr);
                break;
            case STRING:
                this.dataString = matcher.group(1);
                break;
            case CHAR:
                this.dataChar = matcher.group(1).charAt(0);
                break;
            case BOOLEAN:
                //TODO: check if 0 is false.
                if (dataStr.equals("true") || dataStr.equals("false"))
                    this.dataBoolean = Boolean.parseBoolean(dataStr);
                else
                    this.dataBoolean = !(Double.parseDouble(dataStr) == 0);
                break;
        }
    }
}

