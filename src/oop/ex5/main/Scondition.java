package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A S-java condition (if/while).
 */
public class Scondition extends Scope {

    /**
     * A String which holds the first line of the Scondition scope (the scopes's
     * declaration line).
     */
    private final String declaration;


    private List<String> conditions = new ArrayList<>();


    /**
     * the Scondition Class constructor.
     * @param scopeData the scope's code lines.
     * @param outerScope a scope instance from which this constructor was called.
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    public Scondition(List<String> scopeData, Scope outerScope, String name)
            throws ScopeError, MethodError, VariableError  {
        super(scopeData,outerScope, name);
        this.declaration = scopeData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.rawData.remove(0);
        extractCondition();
        if (!this.rawData.isEmpty()) scan();
        checkConditionValidity();
        for (Variable variable: this.variables.values()) variable.delete();
    }

    /**
     * Extracts the if/while condition, in order to check its validity.
     * @throws MissingCondition If the condition is missing (an empty string).
     */
    private void extractCondition() throws MissingCondition, EmptyCondition {
        Pattern pattern = Pattern.compile("^\\s*(if|while)(\\s*)*\\((.*)\\)\\s*$");
        Matcher matcher = pattern.matcher(this.declaration.substring(0, this.declaration.length()-1).trim());
        matcher.find();
        String condition = matcher.group(3).trim();
        // System.out.println("// condition is: " + condition + " //\n");
        if (condition.isEmpty()) throw new MissingCondition();
        checkMultipleCondition(condition);
    }

    /**
     * This method checks if the given condition contains multiple conditions, according
     * to the s-Java's syntax.
     * @param condition The condition to be checked.
     * @throws EmptyCondition In case any of the given conditions is empty.
     */
    private void checkMultipleCondition(String condition) throws EmptyCondition{
        if (condition.contains("||")) splitCondition(condition, "\\|\\|");
        else if (condition.contains("&&")) splitCondition(condition, "\\&\\&");
        else this.conditions.add(condition);
    }


    /**
     * This method splits a String of multiple conditions into individual conditions,
     * according to a given buffer (the OR / AND s-Java operators).
     * @param condition a String of multiple conditions.
     * @param buffer OR / AND operators.
     * @throws EmptyCondition In case any of the given conditions is empty.
     */
    private void splitCondition(String condition, String buffer) throws EmptyCondition{
        String[] splitted = condition.split(buffer);
        // check if all conditions are valid
        for (String splitCondition: splitted) {
            // System.out.println("splitted " + splitCondition);
            if (splitCondition.trim().isEmpty()) throw new EmptyCondition();
            this.conditions.add(splitCondition);
        }
    }


    /**
     * Checks if the condition is valid.
     * @throws InvalidConditionException If the condition is invalid
     * @throws VariableDoesNotExist If the variable in the condition does not exist.
     * @throws MissingCondition If the condition is missing (an empty string).
     */
    private void checkConditionValidity() throws InvalidConditionException, VariableDoesNotExist, MissingCondition, UninitializedVariable {
        for (String condition: this.conditions) {
            condition = condition.trim();
            if (!checkBooleanReservedWord(condition) && !checkVariableType(condition) &&
                    !checkStringCondition(condition))
                throw new InvalidConditionException(condition);
        }
    }

    /**
     * Checks if the condition string is "true" or "false".
     * @param condition The condition string.
     * @return True if the condition is a boolean keyword, false elsewhere.
     */
    private boolean checkBooleanReservedWord(String condition) {
        return condition.equals("true") || condition.equals("false");
    }

    /**
     * Checks if the given string variable name is an existing int or double variable.
     * @param variable The string varaible name.
     * @return True if the existing variable is an int or a double
     * @throws VariableDoesNotExist If the varible does not exist.
     */
    private boolean checkVariableType(String variable) throws VariableDoesNotExist, UninitializedVariable {
        if (Variable.existingVariables.containsKey(variable)) {
            if (!Variable.existingVariables.get(variable).isInitialized())
                throw new UninitializedVariable(variable);
            else {
                return (Variable.existingVariables.get(variable).getType().equals("INT") ||
                        Variable.existingVariables.get(variable).getType().equals("DOUBLE"));
            }
        } else if (Variable.existingArguments.containsKey(variable)) {
            return (Variable.existingArguments.get(variable).getType().equals("INT") ||
                    Variable.existingArguments.get(variable).getType().equals("DOUBLE"));
        }
        throw new VariableDoesNotExist(variable);
    }

    /**
     * Checks if the condition is valid.
     * @param condition The string condition
     * @return true if the condition is valid, false elsewhere.
     */
    private boolean checkStringCondition(String condition)  {
        Variable intVar, doubleVar, booleanVar;
        boolean[] checkArr = {true, true, true};
        // try to create the test variables
        try {
            intVar = new Variable("int check_int", false, this);
            doubleVar = new Variable("double check_double", false, this);
            booleanVar = new Variable("boolean check_boolean", false, this);
        } catch (VariableError e){
            return false;
        }
        // try to set the given condition
        try {
            intVar.setData(condition, false);
        }
        catch (VariableError  e) {
            checkArr[0] = false;
        }
        try {
            doubleVar.setData(condition, false);
        }
        catch (VariableError e) {
            checkArr[1] = false;
        }
        try {
            booleanVar.setData(condition,false);
        }
        catch (VariableError e) {
            checkArr[2] = false;
        }
        // remove the variables from existence
        Variable.existingVariables.remove("int check_int");
        Variable.existingVariables.remove("double check_double");
        Variable.existingVariables.remove("boolean check_boolean");

        // if one if the data set is valid (true) it will return true;
        return checkArr[0] || checkArr[1] || checkArr[2];
    }


}
