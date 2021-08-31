package oop.ex5.main;

public class Variable<T> {

    private String name;

    private T data;

    private boolean initialized;

    private final boolean isFinal;

    /**
     * The Contractor of the Variable.
     *
     * @param initializeLine The initializing line (Without any reserved keyword, for example: final, int..)
     * @param isFinal        True if this variable should be final, else false.
     */
    Variable(String initializeLine, boolean isFinal) {
        this.isFinal = isFinal;
        this.name = findName(initializeLine);
        this.data = findData(initializeLine);
    }

}
