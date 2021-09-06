package oop.ex5.main;

/**
 * a class of Scope errors exceptions.
 */
public class ScopeError extends Exception{

    /**
     * super constructor for a Scope Error
     *
     * @param errorMessage The error message.
     */
    public ScopeError(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * in case of a wrong use of the s-Java code syntax.
 */
class InvalidSyntax extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidSyntax(String scopeName) {
        super("Invalid s-Java syntax in scope '"
                + scopeName +"'.");
    }
}

/**
 * in case the user tries to create a method/if/while s-Java
 * scope, with a bad scope declaration structure.
 */
class InvalidScopeDeclaration extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidScopeDeclaration() {
        super("Invalid s-Java scope declaration.");
    }
}

/**
 * in case a scope is missing a closing bracket.
 */
class BadBracketsStructure extends ScopeError {
    /**
     * The Error constructor.
     */
    public BadBracketsStructure(String scopeName) {
        super("Invalid brackets structure in scope " +
                "'" + scopeName + "'.");
    }
}

/**
 * in case of an invalid condition.
 */
class InvalidConditionException extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidConditionException() {
        super("Invalid If/While s-Java condition.");
    }
}
