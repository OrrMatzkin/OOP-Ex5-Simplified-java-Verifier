package oop.ex5.main;

/**
 * Variable Error class.
 */
public class VariableError extends Exception {

    /**
     * super constructor for a  Variable Error
     *
     * @param errorMessage The error message.
     */
    public VariableError(String errorMessage) {
        super(errorMessage);
    }
}

class BadVariableNameDigit extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameDigit(String name) {
        super("'" + name + "' is an invalid Variable Name, Can't start with a digit.");
    }
}

/**
 * Bad Variable Name error, underscore.
 */
class BadVariableNameUnderscore extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameUnderscore(String name) {
        super("'" + name + "' is an invalid Variable Name, can't be only an underscore.");
    }
}

/**
 * Bad Variable Name error, contains illegal characters .
 */
class BadVariableNameIllegal extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameIllegal(String name) {
        super("'" + name + "' is an invalid Variable Name, contains illegal characters.");
    }
}

/**
 * Bad Variable Name error, saved keyword.
 */
class BadVariableNameSavedKeyword extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameSavedKeyword(String name) {
        super("'" + name + "' is an invalid Variable Name, This name is a saved keyword.");
    }
}

/**
 * Bad Variable Type error.
 */
class BadVariableType extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableType(String type) {
        super("" + type + " is an invalid Variable type.");
    }
}

/**
 * Bad Variable data error.
 */
class BadVariableData extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableData(Variable variable, String data) {
        super(data + " is an invalid value for a " +
                variable.getType().toLowerCase() + " variable.");
    }
}

/**
 * Trying to change a final Variable.
 */
class IllegalFinalDataChange extends VariableError {
    /**
     * The Error constructor.
     */
    public IllegalFinalDataChange(Variable variable) {
        super("Cannot assign a value to final variable '" + variable.getName() + "'.");
    }
}


