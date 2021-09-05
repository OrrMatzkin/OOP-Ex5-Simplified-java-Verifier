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
    Scondition(List<String> scopeData, Scope outerScope) throws Exception {
        super(scopeData,outerScope);
        this.declaration = scopeData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.scopeData.remove(0);
        extractCondition();
        if (!this.scopeData.isEmpty()) scan();
        for (Variable variable: this.variables) {
            variable.delete();
        }
    }

    /**
     * this method extracts the if/while condition, in order to check its
     * validity.
     * @return
     */
    protected String extractCondition() {
        Pattern pattern = Pattern.compile("^ *(if|while)( *)*\\((.+)\\) *$");
        Matcher matcher = pattern.matcher(this.declaration.substring(0, this.declaration.length()-1).trim());
        matcher.find();
        String condition = matcher.group(3).trim();
        System.out.println("// condition is: " + condition + " //\n");
        return condition;
    }

    protected void checkConditionValidity() {
        String condition = extractCondition();

    }

    protected boolean checkBooleanReservedWord(String condition) {
        return condition.equals("true") || condition.equals("false");
    }


}
