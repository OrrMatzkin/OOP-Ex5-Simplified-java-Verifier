package oop.ex5.main;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Variable class.
 */
public class Variable {

    /**
     * All the current existing variables n the program sorted in a HashMap <name, Variable objects>.
     */
    public static HashMap<String, Variable> existingVariables = new HashMap<>();

    /**
     * A Type Enum.
     */
    private enum Type {
        /**
         * An int type.
         */
        INT("^int$", "^(-\\d+)$|^(\\d+)$"),

        /**
         * A double type.
         */
        DOUBLE("^double$", "^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|^(\\d*\\.\\d+)$|" +
                "^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$"),

        /**
         * a String type.
         */
        STRING("^String$", "^\"(.*)\"$"),

        /**
         * A char type.
         */
        CHAR("^char$", "^'(.)'$"),

        /**
         * A boolean type.
         */
        BOOLEAN("^boolean$", "^true$|^false$|^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|" +
                "^(\\d*\\.\\d+)$|^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$");

        /**
         * Type regular expression for finding the Variable type,
         * and a value regular expression for finding the value.
         */
        Pattern typePattern, valuePattern;

        /**
         * the Constructor of the Type.
         *
         * @param regexType  Regular expression for finding the Variable type.
         * @param regexValue Regular expression for finding the value of the variable.
         */
        Type(String regexType, String regexValue) {
            this.typePattern = Pattern.compile(regexType);
            this.valuePattern = Pattern.compile(regexValue);
        }
    }

    /**
     * A generic Data class for Variable.
     *
     * @param <T> The data type (int/double/String/char/boolean).
     */
    private static class Data<T> {

        /**
         * The value of the data.
         */
        private final T value;

        /**
         * The constructor of the Data.
         *
         * @param value The value of the Data.
         */
        Data(T value) {
            this.value = value;
        }

        /**
         * Converts and returns the value of the data to String.
         *
         * @return The value of the Data as a String.
         */
        @Override
        public String toString() {
            return this.value.toString();
        }
    }

    /**
     * The name of the Variable.
     */
    private String name;

    /**
     * The data of the variable.
     */
    private Data<?> data;

    /**
     * The Type of the Variable.
     */
    private Type type;

    /**
     * The scope who created this variable.
     */
    private final Scope scope;

    /**
     * A boolean representing if the variable is initialized.
     */
    private boolean isInitialized;

    /**
     * A boolean representing if the variable is final.
     */
    private final boolean isFinal;

    /**
     * A boolean representing if the variable is an argument of a method.
     */
    private final boolean isArgument;

    /**
     * The Contractor of the Variable.
     * @param initializeLine The initializing line (trimmed!)
     * @param isArgument True if this variable should is, else false.
     * @throws VariableError When the Variable declaration goes wrong.
     * @throws ClassNotFoundException When the initialize Line is only a new assignments.
     */
    public Variable(String initializeLine, boolean isArgument, Scope scope) throws VariableError, ClassNotFoundException {
        this.scope = scope;
        this.isArgument = isArgument;
        this.isFinal = initializeLine.startsWith("final");
        // if this is only an assignment (not a new declared variable)
        Matcher assignmentMatcher = Pattern.compile("^(\\S+) *= *(\\S+)$").matcher(initializeLine.trim());
        if (assignmentMatcher.find()) {
            assignVariable(assignmentMatcher);
        }
        // if this is a declaration (a new variable)
        else {
            updateParameters(isFinal ? initializeLine.replaceFirst("final", "")
                    : initializeLine);
            existingVariables.put(this.name, this);
        }
    }

    /**
     * Tries to assign an existing variable a new value.
     * @param matcher The matcher of the assignments.
     * @throws VariableError If the assignment is invalid.
     */
    private void assignVariable(Matcher matcher) throws VariableError {
        Scope curScope = this.scope;
        while (curScope != null){
            if (curScope.variables.contains(existingVariables.get(matcher.group(1)))){
                existingVariables.get(matcher.group(1)).setData(matcher.group(2));
                break;
            }
            curScope = curScope.outerScope;
        }
        throw new VariableDoesNotExist(matcher.group(1));
    }

    /**
     * extracts and updates the variable parameters (type, name and data) for the initialize line.
     *
     * @param initializeLine The initialize line without the final keyword.
     * @throws VariableError If updating the parameter is unsuccessful it throws a VariableError.
     */
    private void updateParameters(String initializeLine) throws VariableError {
        Matcher fullMatcher = Pattern.compile("^(\\S+) +(\\S+) *= *(\\S+)$").matcher(initializeLine.trim());
        Matcher partMatcher = Pattern.compile("^(\\S+) +(\\S+)$").matcher(initializeLine.trim());
        // with initialization (<Type> <Name> <=> <Data>)
        if (fullMatcher.find()) {
            this.type = extractType(fullMatcher.group(1));
            this.name = extractName(fullMatcher.group(2));
            this.data = extractData(fullMatcher.group(3));
            this.isInitialized = true;
        }
        // if this is variable is not going to be initialized yet (<Type> <Name>)
        else if (partMatcher.find()) {
            this.type = extractType(partMatcher.group(1));
            this.name = extractName(partMatcher.group(2));
            this.isInitialized = false;
        } else throw new BadVariableDeclaration();
    }

    /**
     * Finds the Variable Type.
     *
     * @param typeStr The String that should hold the variable type.
     * @return the Type of the Variable.
     * @throws VariableError If the given Type is invalid throws a VariableError.
     */
    private Type extractType(String typeStr) throws VariableError {
        Matcher matcher;
        for (Type type : Type.values()) {
            matcher = type.typePattern.matcher(typeStr);
            if (matcher.find()) {
                return type;
            }
        }
        throw new BadVariableType(typeStr);
    }

    /**
     * Finds the Variable name.
     *
     * @param nameStr The name String (from the initializing line).
     * @return The name of the Variable.
     * @throws VariableError If the given name is invalid throws a VariableError.
     */
    private String extractName(String nameStr) throws VariableError {
        // if the name is already taken in this scope
        if (this.scope.variables.contains(existingVariables.get(nameStr)))
            throw new BadVariableNameAlreadyExists(nameStr);
        // if the name starts with a digit
        if (Pattern.compile("^\\d").matcher(nameStr).find()) {
            throw new BadVariableNameDigit(nameStr);
            // if the name is a only a single underscore
        } else if (Pattern.compile("^_$").matcher(nameStr).find()) {
            throw new BadVariableNameUnderscore(nameStr);
            // if the name contains illegal characters (not letters or digits)
        } else if (Pattern.compile("(?=\\D)(?=\\W)").matcher(nameStr).find()) {
            throw new BadVariableNameIllegal(nameStr);
            // if the name is one of the reserved keyword
        } else if (Pattern.compile("^(int|double|String|char|boolean)$").matcher(nameStr).find()) {
            throw new BadVariableNameSavedKeyword(nameStr);
        } else return nameStr;
    }

    /**
     * Finds the data of the Variable
     *
     * @param dataStr the data String.
     * @return The matching Data class with the a data value.
     * @throws VariableError If the Variable data is invalid.
     */
    private Data<?> extractData(String dataStr) throws VariableError {
        if (existingVariables.containsKey(dataStr)) {
            Variable exitingVariable = existingVariables.get(dataStr);
            if (this.getType().equals(exitingVariable.getType())) {
                return exitingVariable.getDataObject();
            } else throw new IllegalVariableCasting(this, exitingVariable);
        }
        Matcher matcher = this.type.valuePattern.matcher(dataStr);
        if (!matcher.find()) throw new BadVariableData(this, dataStr);
        switch (this.type) {
            case INT:
                return new Data<>(Integer.parseInt(dataStr));
            case DOUBLE:
                return new Data<>(Double.parseDouble(dataStr));
            case STRING:
                return new Data<>(matcher.group(1));
            case CHAR:
                return new Data<>(matcher.group(1).charAt(0));
            case BOOLEAN:
                if (dataStr.equals("true") || dataStr.equals("false"))
                    return new Data<>(Boolean.parseBoolean(dataStr));
                else
                    return new Data<>(!(Double.parseDouble(dataStr) == 0)); //TODO: check if 0 is false.
        }
        return null;
    }

    /**
     * Sets the Variable data to the given data.
     *
     * @param dataStr The new Variable Data as a String.
     * @throws VariableError If the Variable data is invalid.
     */
    public void setData(String dataStr) throws VariableError {
        if (this.isFinal) throw new IllegalFinalDataChange(this);
        else this.data = extractData(dataStr);
    }

    /**
     * Gets the Variable name.
     *
     * @return The Variable name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the Variable type.
     *
     * @return the Variable Type.
     */
    public String getType() {
        return this.type.toString();
    }

    /**
     * Gets the Variable data.
     *
     * @return the Variable Data.
     */
    public String getData() {
        return this.data.toString();
    }

    /**
     * Gets the Variable Data object.
     *
     * @return the Variable Data object.
     */
    private Data<?> getDataObject() {
        return this.data;
    }

    /**
     * Gets the initialized status.
     *
     * @return True if the Variable is initialized, else false.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Gets the final status.
     *
     * @return True if the Variable is final, else false.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Gets the argument status.
     *
     * @return True if the Variable is an arguments of a method, else false.
     */
    public boolean isArgument() {
        return isArgument;
    }

    //TODO: call delete for all scope variable when the scope closes!!!!

    /**
     * Removes the Variable object from the existing variables hash set.
     */
    public void delete() {
        existingVariables.remove(this.name);
    }
}

