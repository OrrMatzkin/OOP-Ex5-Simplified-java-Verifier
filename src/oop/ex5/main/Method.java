package oop.ex5.main;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class represents a single method scope.
 */
public class Method extends Scope {

    public static HashMap<String, Method> allMethods = new HashMap<>();


    /**
     * a list of all given arguments for the method (can be empty).
     */
    List<Variable> givenArguments = new ArrayList<>();

    /**
     * a String which holds the first line of the method (the method's
     * declaration line).
     */
    protected String declaration;

    /**
     * the Method Class constructor.
     * @param scopeData the scope's code lines.
     * @param outerScope a scope instance from which this constructor was
     *                   called.
     * @throws Exception
     */
    Method(List<String> scopeData, Scope outerScope, String name) throws Exception {
        super(scopeData, outerScope, name);
        this.declaration = this.scopeData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.scopeData.remove(0);
        checkNameValidity();
        processArguments();
        if (!this.scopeData.isEmpty()) scan();
        for (Variable variable: this.variables) {
            variable.delete();
        }
        allMethods.put(name, this);
    }

    /**
     * this method returns 2 kinds of information about the method (corresponding
     * to a given String): the method's name, or the method's arguments - both
     * in a String form.
     * @param kind the kind of information needed
     * @return a String which holds the method's name or the method's arguments.
     */
    private String getInfo(String kind) {
        Pattern pattern = Pattern.compile("^ *void( *)(\\w+) *(\\(\\w* *.*\\)) *$");
        Matcher matcher = pattern.matcher(this.declaration.substring(0, this.declaration.length()-1).trim());
        matcher.find();
        switch (kind) {
            case "name":
                return matcher.group(2);
            case "arguments":
                return matcher.group(3);
            default:
                return null;
        }
    }

    /**
     * this method decomposes the arguments String into individual
     * arguments.
     * @throws Exception
     */
    private void processArguments() throws Exception{
        String arguments = getInfo("arguments");
        arguments = arguments.substring(1, arguments.length()-1).trim();
        String[] splitted = arguments.split(",");
        System.out.println(arguments);
        for (String argument: splitted) {
            if (argument.equals("")) continue;
            System.out.println("// checking argument " + argument.trim()+ " //");
            this.givenArguments.add(new Variable(argument.trim(), true));
        }
    }

//    /**
//     * this method checks if the given argument is a valid s-Java argument.
//     * @param argument the argument to be checked.
//     * @throws Exception
//     */
//    private void addArgument(String argument) throws Exception {
//        this.givenArguments.add(new Variable(argument, true));
//    }

    /**
     * this method checks if the name of the current method is an s-Java
     * valid method name.
     * @throws Exception
     */
    private void checkNameValidity() throws Exception {
        String name = getInfo("name");
        if (Pattern.compile("^\\d").matcher(name).find()) {
            System.err.println("// method's starts with a digit //");
            throw new Exception();
        } else if (Pattern.compile("^_").matcher(name).find()) {
            System.err.println("// method's starts with a single underscore //");
            throw new Exception();
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(name).find()) {
            System.err.println("// method's contains illegal chars //");
            throw new Exception();
        }
    }

//    private void checkArgument (String argument) throws Exception {
//        System.out.println("argument: " + argument);
//        if (Pattern.compile("^\\d").matcher(argument).find()) {
//            System.err.println("starts with a digit");
//            throw new Exception();
//        } if (Pattern.compile("^_{1}$").matcher(argument).find()) {
//            System.err.println("starts with a single underscore");
//            throw new Exception();
//        } if (Pattern.compile("(?=\\D)(?=\\W)").matcher(argument).find()) {
//            System.err.println("contains illegal chars");
//            throw new Exception();
//        }
//    }



}
