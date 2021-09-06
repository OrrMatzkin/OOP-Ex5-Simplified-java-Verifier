package oop.ex5.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scondition extends Scope {

    /**
     * a String which holds the first line of the Scondition scope (the scopes's
     * declaration line).
     */
    protected String declaration;

    /**
     * the Scondition Class constructor.
     * @param scopeData the scope's code lines.
     * @param outerScope a scope instance from which this constructor was
     *      *            called.
     * @throws Exception
     */
    Scondition(List<String> scopeData, Scope outerScope, String name) throws Exception {
        super(scopeData,outerScope, name);
        this.declaration = scopeData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.scopeData.remove(0);
        extractCondition();
        if (!this.scopeData.isEmpty()) scan();
        for (Variable variable: this.variables.values()) {
            variable.delete();
        }
        checkConditionValidity();
    }

    /**
     * this method extracts the if/while condition, in order to check its
     * validity.
     * @return
     */
    protected String extractCondition() {
        Pattern pattern = Pattern.compile("^\\s*(if|while)(\\s*)*\\((.+)\\)\\s*$");
        Matcher matcher = pattern.matcher(this.declaration.substring(0, this.declaration.length()-1).trim());
        matcher.find();
        String condition = matcher.group(3).trim();
        System.out.println("// condition is: " + condition + " //\n");
        return condition;
    }

    protected void checkConditionValidity() throws Exception {
        String condition = extractCondition().trim();
        if (checkBooleanReservedWord(condition) || checkStringCondition(condition)
        || checkVariableType(condition)) return;
        throw new InvalidConditionException();
    }

    protected boolean checkBooleanReservedWord(String condition) {
        return condition.equals("true") || condition.equals("false");
    }

    protected boolean checkVariableType(String variable) throws Exception {
        if (Variable.existingVariables.containsKey(variable)) {
            if (Variable.existingVariables.get(variable).isInitialized()) {
                return (Variable.existingVariables.get(variable).getType().equals("INT") ||
                        Variable.existingVariables.get(variable).getType().equals("FLOAT"));
            }
        }
        throw new VariableDoesNotExist(variable);
    }

    protected boolean checkStringCondition(String condition) throws Exception {
        Variable intVar = new Variable("int check_int", false, this);
        Variable doubleVar = new Variable("double check_double", false, this);
        Variable booleanVar = new Variable("boolean check_boolean", false, this);
        boolean[] checkArr = {true, true, true};
        try {
            intVar.setData(condition, false);
        }
        catch (Exception e) {
            checkArr[0] = false;
        }
        try {
            doubleVar.setData(condition, false);
        }
        catch (Exception e) {
            checkArr[1] = false;
        }
        try {
            booleanVar.setData(condition,false);
        }
        catch (Exception e) {
            checkArr[2] = false;
        }

        Variable.existingVariables.remove("int check_int");
        Variable.existingVariables.remove("double check_double");
        Variable.existingVariables.remove("boolean check_boolean");

        for (boolean bool: checkArr) {
            if (bool) return true;
        }

        return false;
    }


}
