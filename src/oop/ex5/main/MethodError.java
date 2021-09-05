package oop.ex5.main;

public class MethodError  extends Exception {

    /**
     * super constructor for a Method Error
     *
     * @param errorMessage The error message.
     */
    public MethodError(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * Bad Method declaration, used only when there is a general fault.
 */
class BadMethodDeclaration extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodDeclaration() {
        super("Declaration line is invalid");
    }
}

/**
 * Bad Method Name error, starts with a digit.
 */
class BadMethodNameDigit extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameDigit(String name) {
        super("'" + name + "' is an invalid Method Name, Can't start with a digit.");
    }
}

/**
 * Bad Method Name error, underscore.
 */
class BadMethodNameUnderscore extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameUnderscore(String name) {
        super("'" + name + "' is an invalid Method Name, can't be only an underscore.");
    }
}

/**
 * Bad Method Name error, contains illegal characters .
 */
class BadMethodNameIllegal extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameIllegal(String name) {
        super("'" + name + "' is an invalid Method Name, contains illegal characters.");
    }
}

/**
 * Bad Method Name error, saved keyword.
 */
class BadMethodNameSavedKeyword extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameSavedKeyword(String name) {
        super("'" + name + "' is an invalid Method Name, This name is a saved keyword.");
    }
}

/**
 * Bad Method Name error, already exists.
 */
class BadMethodNameAlreadyExists extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameAlreadyExists(String name) {
        super("'Method '" + name + "' is already defined in the scope.");
    }
}

/**
 * Bad Method Type error.
 */
class BadMethodType extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodType(String type) {
        super("" + type + " is an invalid Method type, Sjava allows only void methods.");
    }
}

/**
 * Method does not exist.
 */
class MethodDoesNotExist extends MethodError {
    /**
     * The Error constructor.
     */
    public MethodDoesNotExist(String name) {
        super("Cannot resolve symbol '" + name + "'");
    }
}

/**
 * Method does not exist.
 */
class MissingReturnStatement extends MethodError {
    /**
     * The Error constructor.
     */
    public MissingReturnStatement(Method method) {
        super("Cannot resolve '" + method.getName() + "'");
    }
}

