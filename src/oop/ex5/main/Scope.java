package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * This class represents a scope in the s-Java program. each instance
 * of this class is an individual scope (starts with a valid s-Java
 * code line with an opening bracket at its end, followed by a
 * single closing bracket). In this program, an instance of this class
 * may be one of the three: a general scope, a method defined scope or a
 * condition (if/while) defined scope.
 */
public class Scope {

    /**
     * The value used in the regex group operation.
     */
    private final static int  REGEX_VARIABLE = 1, REGEX_VALUE = 2;

    /**
     * A static variable which hold a reference to the program's global scope.
     */
    public static Scope globalScope;

    /**
     * A HashMap of all the scope variables.
     */
    protected HashMap<String, Variable>  variables = new HashMap<>();

    /**
     * A HashMap of all the scope arguments (used by the method).
     */
    protected LinkedHashMap<String, Variable> arguments = new LinkedHashMap<>();

    /**
     * The scope's data. Each line is preserved in a single list cell.
     */
    protected List<String> rawData;

    /**
     * A list of the scope's inner scopes.
     */
    protected List<Scope> innerScopes = new ArrayList<>();

    /**
     * The outer scope of this scope (if this scope is the global scope it will be null).
     */
    protected Scope outerScope;

    /**
     * The scope length (number of lines).
     */
    protected int length;

    /**
     * The scope name.
     */
    protected String name;


    /**
     * The Scope Class constructor.
     * @param scopeData The scope code lines.
     * @param outerScope The scope which wraps this scope.
     * @param name The scope name.
     */
    public Scope(List<String> scopeData, Scope outerScope, String name) {
        this.name = name;
        if (this.name.equals("Global Scope")) Scope.globalScope = this;
        this.length = scopeData.size();
        this.rawData = scopeData;
        this.outerScope = outerScope;
    }

    /**
     * The scope's data decoder.
     * This method iterates over the scope's data (each iteration scans an
     * individual code line). according to the exercise's instructions, and the
     * s-Java coding specifications, this method matched each line to it's
     * appropriate cipher.
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    protected void scan() throws ScopeError, MethodError, VariableError {
        int maxLineNum = this.rawData.size();
        String line;
        for (int lineNum = 0; lineNum < maxLineNum; lineNum++) {
            line = this.rawData.get(lineNum);
            // in case the current is a comment line or an empty line.
            if (line.startsWith("//") || line.trim().isEmpty()) {}
            // in case of a declaration or assigment
            else if (line.trim().endsWith(";")) singleLineCommand(line);
            // in case of a new scope creation
            else if (line.trim().endsWith("{")) lineNum += scopeCreation(line, lineNum) -1;
            // in case of invalid line syntax
            else {
                throw new InvalidSyntax(line);
            }
        }
    }

    /**
     * This method helps to determine whether the scanned line creates a new
     * method scope or a new if/while scope.
     * @param line the scope's declaration line.
     * @param lineNum the number of the first line in the new declared scope
     *                (with respect to the original scope line counter).
     * @return the new declared scope's size (the number of lines in it).
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    private int scopeCreation(String line, int lineNum) throws ScopeError, MethodError, VariableError {
        Pattern pattern1 = Pattern.compile("^\\s*(if|while)\\s*\\(.*\\)\\s*");
        Pattern pattern2 = Pattern.compile("^\\s*(\\w+)(\\s+)(\\w+)\\s*\\(.*\\)\\s*");

        // matcher (without the "{")
        Matcher matcher1 = pattern1.matcher(line.substring(0, line.length() - 1));
        Matcher matcher2 = pattern2.matcher(line.substring(0, line.length() - 1));

        // if/while statement
        if (matcher1.find()) {
            return scopeCreationAUX(lineNum,"ifWhile", line);
        }
        // a method declaration statement
        else if (matcher2.find()) {
            if (matcher2.group(REGEX_VARIABLE).equals("void")) {
                if (this.outerScope != null) throw new InvalidMethodCreation(matcher2.group(REGEX_VALUE));
                else return scopeCreationAUX(lineNum, "method", matcher2.group(REGEX_VALUE));
            } else throw new BadMethodType(matcher2.group(REGEX_VARIABLE));
        }
        // in case the line ends with "{" but no void/if/while with a valid s-java declaration
        else throw new InvalidScopeDeclaration();
    }

    /**
     * this method 'creates' a new Method or Scondition Class, corresponding
     * to the given type.
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @param type "method" or "ifWhile" - to determine the new Class's identity.
     * @param name the scope's name.
     * @return the new scope's size (the number of lines in it).
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    private int scopeCreationAUX(int lineNum, String type, String name)
            throws ScopeError, MethodError, VariableError {
        int innerScopeSize = findInnerScopeSize(lineNum);
        List<String> innerScopeData = new ArrayList<>();
        // -1 for not including the closing bracket
        for (int i = lineNum; i < innerScopeSize + lineNum - 1; i++) {
            innerScopeData.add(this.rawData.get(i));
        }
        if (type.equals("method")) {
            Method method = new Method(innerScopeData, this, name);
            this.innerScopes.add(method);
        }
        else {
            if (callFromMethod()) {
                Scondition scondition = new Scondition(innerScopeData, this, name);
                this.innerScopes.add(scondition);
            }
            else throw new ConditionDeclarationNotFromMethod();
        }
        return innerScopeSize;
    }


    /**
     * Processes all the single line command:
     * 1. new Variable declarations.
     * 2. a Method call.
     * 3. a return statement.
     * 4. existing Variable assignments.
     * @param line The single line command.
     * @throws VariableError Possible when trying to declare or assign a variable.
     * @throws InvalidCommand If there is an invalid command in one of the scope lines.
     * @throws InvalidMethodCall If a method is called not from the global scope.
     * @throws InvalidSyntax In case of an invalid s-Java syntax.
     */
    public void singleLineCommand(String line)
            throws VariableError, InvalidCommand, InvalidMethodCall, InvalidSyntax {
        String trimmedLine = line.trim();
        trimmedLine = trimmedLine.substring(0,trimmedLine.length()-1);
        // New Variable declarations
        if (possibleVariableDeclaration(trimmedLine)) declareNewVariables(trimmedLine);
        // A Method call
        else if (possibleMethodCall(line)) {
            if (!callFromMethod()) throw new InvalidMethodCall(line);
            MethodCallsChecker.addCall(line);
        }
        // A return statement
        else if (isReturnLine(line)) {}
        // A Variable assignments
        else assignExistingVariable(trimmedLine);
    }

    /**
     * Check if this scope or any of his outer scope is a method.
     * @return True if this scope is a method or wrapped by one, false elsewhere.
     */
    protected boolean callFromMethod(){
        Scope outerScope = this;
        while (outerScope != null){
            if (outerScope instanceof Method) return true;
            outerScope = outerScope.outerScope;
        }
        return false;
    }

    /**
     * Checks if the given line is a valid s-Java return statement line.
     * @param line The line to be checked.
     * @return True in case the given line is a valid s-Java return statement inside a method, false otherwise.
     */
    private boolean isReturnLine(String line) {
        Pattern pattern = Pattern.compile("^\\s*return\\s*;\\s*$");
        Matcher matcher = pattern.matcher(line);
        return matcher.find() && callFromMethod();
    }

    /**
     * Checks if the method call is valid.
     * @param line A String in which a method is to be called.
     * @return True in case the line holds a valid s-Java method call, false otherwise.
     */
    private boolean possibleMethodCall(String line) {
        Pattern pattern = Pattern.compile("^\\s*([a-zA-Z0-9_]+)\\s*(\\(.*\\))\\s*$");
        Matcher matcher = pattern.matcher(line.substring(0, line.length()-1)); // removing the '}'
        return matcher.find();
    }

    /**
     * Checks if the given line starts with one of the s-Java's reserved keywords,
     * which indicates a new variable declaration.
     * @param line The line to be checked.
     * @return True in case the line decodes for a variable declaration, false otherwise.
     */
    private boolean possibleVariableDeclaration(String line) {
        return (line.trim().startsWith("final") || line.trim().startsWith("int") ||
                line.trim().startsWith("double") || line.trim().startsWith("String")
                || line.trim().startsWith("boolean") || line.trim().startsWith("char"));
    }

    /**
     * Tries to assign an existing argument or variable a new value.
     * @param line The line to be checked.
     * @throws VariableError If one of the possible assignments fails.
     * @throws InvalidCommand If there is an invalid command (not an assignment).
     */
    private void assignExistingVariable(String line) throws VariableError, InvalidCommand{
        Pattern pattern = Pattern.compile("^(\\S+)\\s*=\\s*(\\S+)$");
        String[] assignmentsStr = line.split(",");
        for (String possibleAssignment: assignmentsStr){
            Matcher matcher = pattern.matcher(possibleAssignment);
            if (matcher.find()) {
                Scope curScope = this;
                while (curScope != null) {
                    if (curScope.arguments.containsKey(matcher.group(REGEX_VARIABLE))) {
                        curScope.arguments.get(matcher.group(REGEX_VARIABLE)).
                                setData(matcher.group(REGEX_VALUE), false, this);
                        return;
                    }
                    if (curScope.variables.containsKey(matcher.group(REGEX_VARIABLE))) {
                        curScope.variables.get(matcher.group(REGEX_VARIABLE)).
                                setData(matcher.group(REGEX_VALUE), false, this);
                        return;
                    }
                    curScope = curScope.outerScope;
                }
                if (callFromMethod()) {
                GlobalVariablesChecker.addAssignment(possibleAssignment); }
                else throw new VariableDoesNotExist(matcher.group(REGEX_VARIABLE));
            } else throw new InvalidCommand(line);
        }

    }

    /**
     * Tries to create new variables.
     * @param line The line to be checked.
     * @throws VariableError If one of the possible deceleration fails.
     */
    private void declareNewVariables(String line) throws VariableError, InvalidSyntax {
        String configStr = "";
        String[] separatedWords = line.split(" ");
        if (separatedWords.length >= 2) {
            configStr += separatedWords[0] + " ";
            if (separatedWords[0].equals("final")) configStr += separatedWords[1] + " ";
        }
        try {
            line = line.replaceFirst(configStr, "");
        } catch (PatternSyntaxException e){
            throw new BadVariableDeclaration(line,false);
        }
        if (line.endsWith(",")) throw new BadVariableDeclaration(line,false);
        String[] variablesStr = line.split(",");
        for (String variableStr: variablesStr) {
            if (variableStr.isEmpty()) throw new InvalidSyntax(line);
            Variable variable = new Variable(configStr +
                    variableStr.trim(), false, this);
            this.variables.put(variable.getName(), variable);
        }
    }

    /**
     * Finds the size of a scope (according to it's starting line number).
     * by iteration, it scans for a new closing bracket to appear, to signify the end
     * of the scope.
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @return the size of the scope (the number of lines in it).
     * @throws BadBracketsStructure If there is a bad brackets structure.
     */
    private int findInnerScopeSize(int lineNum) throws BadBracketsStructure {
        int scopeSize = 1;
        int openBracketsNum = 1;
        int closedBracketsNum = 0;
        while (lineNum < this.rawData.size() && ((openBracketsNum - closedBracketsNum) != 0)) {
            lineNum++;
            try {
                if (this.rawData.get(lineNum).trim().endsWith("{")) openBracketsNum++;
                if (this.rawData.get(lineNum).trim().equals("}")) closedBracketsNum++;
            } catch (IndexOutOfBoundsException e) {
                throw new BadBracketsStructure(this.name);
            }
            scopeSize++;
        }
        if ((openBracketsNum - closedBracketsNum) != 0) throw new BadBracketsStructure(this.name);
        return scopeSize;
    }

    /**
     * Getter for the scope name.
     * @return This scope name.
     */
    public String getName() {
        return this.name;
    }
}

