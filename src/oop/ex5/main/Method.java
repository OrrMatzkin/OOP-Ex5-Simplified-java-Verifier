package oop.ex5.main;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a single method scope.
 */
public class Method extends Scope {

    /**
     * A HashMap holding all the existing Methods.
     */
    public static HashMap<String, Method> allMethods = new HashMap<>();

    /**
     * A String which holds the first line of the method (the method's declaration line).
     */
    protected String declaration;

    /**
     * The Method Class constructor.
     * @param scopeData the scope's code lines.
     * @param outerScope a scope instance from which this constructor was called.
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    Method(List<String> scopeData, Scope outerScope, String name) throws ScopeError, MethodError, VariableError {
        super(scopeData, outerScope, name);
        this.declaration = this.rawData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.rawData.remove(0);
        checkNameValidity();
        processArguments();
        if (!this.rawData.isEmpty()) scan();
//        for (Variable variable: this.variables.values()) {
//            if (!variable.isArgument())
//                variable.delete();
//        }
        if (allMethods.containsKey(this.name)) {
            throw new BadMethodNameAlreadyExists(this.name);
        }
        allMethods.put(name, this);
        checkReturnAtEnd();
    }

    /**
     * This method returns 2 kinds of information about the method (corresponding
     * to a given String): the method's name, or the method's arguments - both
     * in a String form.
     * @param kind The kind of information needed
     * @return A String which holds the method's name or the method's arguments.
     */
    private String getInfo(String kind) {
        Pattern pattern = Pattern.compile("^\\s*void(\\s*)(\\w+)\\s*(\\(.*\\))\\s*");
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
     * Decomposes the arguments String into individual arguments.
     * @throws VariableError If there is Variable error.
     */
    private void processArguments() throws VariableError, BadArgumentsNum {
        String arguments = getInfo("arguments");
        arguments = arguments.substring(1, arguments.length()-1).trim();
        String[] splitted = arguments.split(",");
        // System.out.println(arguments);
        for (String argument: splitted) {
            if (argument.isEmpty() && splitted.length != 1) throw new BadArgumentsNum(this.name);
            else if (argument.isEmpty()) return;
            // System.out.println("// checking argument " + argument.trim()+ " //");
            Variable variable =  new Variable(argument.trim(), true, this);
            this.arguments.put(variable.getName(), variable);
        }
    }

    /**
     * Checks if the name of the current method is an s-Java valid method name.
     * @throws MethodError If there is Method error.
     */
    private void checkNameValidity() throws MethodError {
        String name = getInfo("name");
        if (Pattern.compile("^\\d").matcher(name).find()) {
            throw new BadMethodNameDigit(this.name);
        } else if (Pattern.compile("^_").matcher(name).find()) {
            throw new BadMethodNameUnderscore(this.name);
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(name).find()) {
            throw new BadMethodNameIllegal(this.name);
        } else if (Pattern.compile("^(int|double|String|char|boolean|final|if|while|true|false|void|return)$")
                .matcher(name).find()) {
            throw new BadMethodNameSavedKeyword(name);
        }

    }

    /**
     * Checks if there is return statement and the end of the method.
     * @throws MissingReturnStatement If there is a missing return statement.
     */
    private void checkReturnAtEnd() throws MissingReturnStatement {
        String lastLine = this.rawData.get(rawData.size()-1);
        Pattern pattern = Pattern.compile("\\s*return\\s*;\\s*");
        Matcher matcher = pattern.matcher(lastLine);
        if (!matcher.find()) {
            throw new MissingReturnStatement(this);
        }
    }

}
