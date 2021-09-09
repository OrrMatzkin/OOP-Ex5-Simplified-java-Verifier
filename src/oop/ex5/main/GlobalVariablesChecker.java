package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalVariablesChecker {

    /**
     * A list of Strings which holds all possible variables assignments from Method scopes.
     */
    public static List<String> globalVariablesAssignments = new ArrayList<>();

    public static List<String> globalVariablesDeclaration = new ArrayList<>();

    public static HashMap<String, Scope> globalVariablesCondition = new HashMap<>();

    /**
     * The Class's single instacne.
     */
    public static GlobalVariablesChecker singleInstance = new GlobalVariablesChecker();

    /**
     * the Class constructor.
     */
    private GlobalVariablesChecker() {}

    /**
     * the single instance getter
     * @return a reference to the Class's single instance
     */
    public static GlobalVariablesChecker getSingleInstance() {
        return singleInstance;
    }

    /**
     * This method adds a possible global scope assignment to the relevant
     * list
     * @param assignment A string contains the relevant assignment.
     */
    public static void addAssigment(String assignment) {
        globalVariablesAssignments.add(assignment);
    }

    public static void addDeclaration(String declaration) {
        globalVariablesDeclaration.add(declaration);
    }

    public static void addCondition(String condition, Scope scope) {
        globalVariablesCondition.put(condition, scope);
    }


    /**
     * This method iterates over all possible variables assignments (from Method scopes),
     * and checks if the variable was declared in the global scope after the assignments.
     * @throws VariableError
     */
    public static void checkGlobalAssignments() throws VariableError {
        Scope curScope = Scope.globalScope;
        Pattern pattern = Pattern.compile("^(\\S+) *= *(\\S+)$");
        for (String possibleAssignment : globalVariablesAssignments) {
            Matcher matcher = pattern.matcher(possibleAssignment);
            if (matcher.find()) {
                if (curScope.variables.containsKey(matcher.group(1))) {
                    curScope.variables.get(matcher.group(1)).setData(matcher.group(2), false, Scope.globalScope);
                }
                else throw new VariableDoesNotExist(matcher.group(1));
            }
        }
    }

    public static void checkGlobalDeclaration() throws InvalidCommand, InvalidMethodCall, VariableError, InvalidSyntax {
        for (String declaration: globalVariablesDeclaration) {
            Scope.globalScope.singleLineCommand(declaration + ";");
        }
    }

    public static void checkGlobalCondition() throws VariableError, MethodError, ScopeError {
        for (String variableStr : globalVariablesCondition.keySet()){
            Variable variable = Variable.existingVariables.get(variableStr);
            Variable argument = Variable.existingArguments.get(variableStr);
            if (variable != null) {
                if (variable.initializedScope != Scope.globalScope) throw new InvalidConditionException(variableStr);
                if (!variable.isInitialized()) throw new UninitializedVariable(variableStr);
                else if (!variable.getType().equals("BOOLEAN") && !variable.getType().equals("INT") && !variable.getType().equals("DOUBLE"))
                        throw new InvalidConditionException(variableStr);
                else return;
                }

            if (argument != null) {
                if (!argument.getType().equals("BOOLEAN") && !argument.getType().equals("INT") && !argument.getType().equals("DOUBLE"))
                    throw new InvalidConditionException(variableStr);
                else return;
            }
            throw new InvalidConditionException(variableStr);
        }

    }


    public static void print() {
        for (String dec: globalVariablesAssignments) {
            // System.out.println("GLOBAL: " + dec);
        }
    }

}
