package oop.ex5.main;

import java.lang.invoke.VarHandle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method extends Scope {

    private int argumentsNum;
    // List<Variable> givenArguments;
    Method(List<String> scopeData) throws Exception {
        super(scopeData);

        String declaration = scopeData.get(0);
        declaration = declaration.substring(0, declaration.length() - 1);
        checkDeclaration(declaration);
    }


    protected void checkDeclaration(String declaration) throws Exception {
        // String[] splitted = declaration.trim().split("\\s+");
        Pattern pattern = Pattern.compile("^ *(void) *([^ ].+) *(\\(.*\\)) *");
        Matcher matcher = pattern.matcher(declaration);
        if (!matcher.find()) throw new Exception();
        if (!matcher.group(1).equals("void")) throw new Exception();
        checkNameValidity(matcher.group(2).trim());
        checkArgumentsValidity(matcher.group(3));

    }

    private void checkNameValidity(String name) throws Exception {
        System.out.println("name1: " + name);

        if (Pattern.compile("^\\d").matcher(name).find()) {
            System.err.println("starts with a digit");
            throw new Exception();
        } else if (Pattern.compile("^_").matcher(name).find()) {
            System.err.println("starts with a single underscore");
            throw new Exception();
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(name).find()) {
            System.err.println("contains illegal chars");
            throw new Exception();
        }
    }


    private void checkArgument (String argument) throws Exception {
        System.out.println("argument: " + argument);

        if (Pattern.compile("^\\d").matcher(argument).find()) {
            System.err.println("starts with a digit");
            throw new Exception();
        } if (Pattern.compile("^_{1}$").matcher(argument).find()) {
            System.err.println("starts with a single underscore");
            throw new Exception();
        } if (Pattern.compile("(?=\\D)(?=\\W)").matcher(argument).find()) {
            System.err.println("contains illegal chars");
            throw new Exception();
        }
    }


//    private void checkArgumentsValidityAUX(String argument) throws Exception {
//        Set<String> possibleTypes;
//        possibleTypes.add("int");
//        possibleTypes.add("double");
//        possibleTypes.add("boolean");
//        possibleTypes.add("char");
//        possibleTypes.add("String");
//        String[] splitted = argument.split(" ");
//        if (splitted.length == 1) {
//            // in case the argument is missing a type
//            throw new Exception();
//        } else if (splitted.length == 3) {
//            if (!splitted[0].equals("final")) {
//                throw new Exception();
//            }
//        } else if (splitted.length == 2) {
//
//        }
//    }


    private void checkArgumentsValidity(String name) throws Exception {
        name = name.substring(1, name.length()-1);
        System.out.println("arguments: " + name);
        String[] arguments = name.split(",");
        int i = 0;
        for (String argument : arguments) {
            i++;
            System.out.println("arg" + i + ": " + argument);
        }
    }
}
