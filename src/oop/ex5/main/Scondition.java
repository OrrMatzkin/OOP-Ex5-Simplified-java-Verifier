package oop.ex5.main;

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
        for (Variable variable: this.variables.values()) {
            variable.delete();
        }
        checkConditionValidity();
    }

    /**
     * Extracts the if/while condition, in order to check its validity.
     * @return The condition (as String).
     * @throws MissingCondition If the condition is missing (an empty string).
     */
    private String extractCondition() throws MissingCondition {
        Pattern pattern = Pattern.compile("^\\s*(if|while)(\\s*)*\\((.*)\\)\\s*$");
        Matcher matcher = pattern.matcher(this.declaration.substring(0, this.declaration.length()-1).trim());
        matcher.find();
        String condition = matcher.group(3).trim();
        System.out.println("// condition is: " + condition + " //\n");
        if (condition.isEmpty()) throw new MissingCondition();
        return condition;
    }

    /**
     * Checks if the condition is valid.
     * @throws InvalidConditionException If the condition is invalid
     * @throws VariableDoesNotExist If the variable in the condition does not exist.
     * @throws MissingCondition If the condition is missing (an empty string).
     */
    private void checkConditionValidity() throws InvalidConditionException, VariableDoesNotExist, MissingCondition {
        String condition = extractCondition().trim();
        if (checkBooleanReservedWord(condition) || checkStringCondition(condition)
        || checkVariableType(condition)) return;
        throw new InvalidConditionException(condition);
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
    private boolean checkVariableType(String variable) throws VariableDoesNotExist {
        if (Variable.existingVariables.containsKey(variable)) {
            if (Variable.existingVariables.get(variable).isInitialized()) {
                return (Variable.existingVariables.get(variable).getType().equals("INT") ||
                        Variable.existingVariables.get(variable).getType().equals("DOUBLE"));
            }
        }
        throw new VariableDoesNotExist(variable);
    }

    /**
     * Checks if the condition is valid.
     * @param condition The string condition
     * @return true if the condition is valid, false elsewhere.
     */
    private boolean checkStringCondition(String condition)  {
        Variable intVar;
        Variable doubleVar;
        Variable booleanVar;
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
        catch (VariableError e) {
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
