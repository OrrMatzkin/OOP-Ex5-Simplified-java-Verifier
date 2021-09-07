package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class represents a scope in the s-Java program. each instance
 * of this class is an individual scope (starts with a valid s-Java
 * code line with an opening bracket at its end, followed by a
 * single closing bracket). in this program, an instance of this class
 * may be one of the three: a general scope, a method defined scope or a
 * condition (if/while) defined scope.
 */
public class Scope {

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
//        System.out.println("----------------");
//        for (String line: scopeData) {
//            System.out.println(line); }
//        System.out.println("----------------\n");
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
//        System.out.println("// CURRENTLY SCANNING SCOPE ***" + this.getName() + "*** //\n");
        int maxLineNum = this.rawData.size();
        String line;
        for (int lineNum = 0; lineNum < maxLineNum; lineNum++) {
            line = this.rawData.get(lineNum);
            // in case the current is a comment line or an empty line.
            if (line.startsWith("//") || line.trim().isEmpty()) continue;
            // in case of a declaration or assigment
            else if (line.trim().endsWith(";")) singleLineCommand(line);
            // in case of a new scope creation
            else if (line.trim().endsWith("{")) lineNum += scopeCreation(line, lineNum, maxLineNum) -1;
            // in case the current line is empty
            // in case of invalid line syntax
            else {
                throw new InvalidSyntax(line);
            }
        }
    }

    /**
     * this method helps to determine whether the scanned line creates a new
     * method scope or a new if/while scope.
     * @param line the scope's declaration line.
     * @param lineNum the number of the first line in the new declared scope
     *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @return the new declared scope's size (the number of lines in it).
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    protected int scopeCreation(String line, int lineNum, int maxLineNum)
            throws ScopeError, MethodError, VariableError {
        System.out.println("// line ends with '{' //");

        Pattern pattern1 = Pattern.compile("^\\s*(if|while)(\\s*)*\\(.*\\)\\s*$");
        Pattern pattern2 = Pattern.compile("^\\s*(\\w+)(\\s+)(\\w+)\\s*\\(\\w* *.*\\)\\s*$");

        // matcher (without the "{")
        Matcher matcher1 = pattern1.matcher(line.substring(0, line.length() - 1));
        Matcher matcher2 = pattern2.matcher(line.substring(0, line.length() - 1));

        // if/while statement
        if (matcher1.find()) {
            System.out.println("// creates new if/while scope //");
            int innerScopeSize = scopeCreationAUX(lineNum, maxLineNum, "ifWhile", line);
            System.out.println("// if/while scope's size is : "+ innerScopeSize + "//\n");
            return innerScopeSize;
        }
        // a method declaration statement
        else if (matcher2.find()) {
            if (matcher2.group(1).equals("void")) {
                if (this.outerScope != null) {
                    throw new InvalidMethodCreation(matcher2.group(3));
                }
                System.out.println("// creates new method scope //");
                int innerScopeSize = scopeCreationAUX(lineNum, maxLineNum, "method", matcher2.group(3));
                System.out.println("// method's size is : "+ innerScopeSize + "//\n");
                return innerScopeSize;
            } else throw new BadMethodType(matcher2.group(1));
        }
        // in case the line ends with "{" but no void/if/while with a
        // valid s-java declaration structure was found
        else {
            throw new InvalidScopeDeclaration();
        }
    }

    /**
     * this method 'creates' a new Method or Scondition Class, corresponding
     * to the given type.
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @param type "method" or "ifWhile" - to determine the new Class's identity.
     * @return the new scope's size (the number of lines in it).
     * @throws ScopeError If there is Scope error.
     * @throws MethodError If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    protected int scopeCreationAUX(int lineNum, int maxLineNum, String type, String name)
            throws ScopeError, MethodError, VariableError {
        int innerScopeSize = findInnerScopeSize(lineNum, maxLineNum);
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
            Scondition scondition = new Scondition(innerScopeData, this, name);
            this.innerScopes.add(scondition);
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
     * @throws InvalidSyntax If there is an invalid syntax in one of the scope lines.
     * @throws InvalidCommand If there is an invalid command in one of the scope lines.
     */
    private void singleLineCommand(String line) throws VariableError, InvalidCommand {
        String trimmedLine = line.substring(0,line.length()-1).trim();
        // New Variable declarations
        if (possibleVariableDeclaration(line)) {
            System.out.println("// creates new variables //");
            declareNewVariables(trimmedLine);
        }
        // A Method call
        else if (possibleMethodCall(line)) {
            System.out.println("// added a new possible call //");
            CallsHandler.addCall(line);
        }
        // A return statement
        else if (isReturnLine(line)) {
            this.variables.forEach((k, v) -> v.delete());
            this.arguments.forEach((k, a) -> a.delete());
        }
        // A Variable assignments
        else {
            System.out.println("// assign variables //");
            assignExistingVariable(trimmedLine);
        }
    }

    /**
     * Checks if the given line is a valid s-Java return statement line.
     * @param line The line to be checked.
     * @return True in case the given line is a valid s-Java return statement inside a method, false otherwise.
     */
    private boolean isReturnLine(String line) {
        Pattern pattern = Pattern.compile("^\\s*return\\s*;\\s*$");
        Matcher matcher = pattern.matcher(line);
        return matcher.find() && this instanceof Method;
    }

    /**
     * Checks if the method call is valid.
     * @param line A String in which a method is to be called.
     * @return True in case the line holds a valid s-Java method call, false otherwise.
     */
    public boolean possibleMethodCall(String line) {
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
        Pattern pattern = Pattern.compile("^(\\S+) *= *(\\S+)$");
        String[] assignmentsStr = line.split(",");
        for (String possibleAssignment: assignmentsStr){
            Matcher matcher = pattern.matcher(possibleAssignment);
            if (matcher.find()) {
                Scope curScope = this;
                while (curScope != null) {
                    if (curScope.arguments.containsKey(matcher.group(1))) {
                        curScope.arguments.get(matcher.group(1)).setData(matcher.group(2), false);
                        return;
                    }
                    if (curScope.variables.containsKey(matcher.group(1))) {
                        curScope.variables.get(matcher.group(1)).setData(matcher.group(2), false);
                        return;
                    }
                    curScope = curScope.outerScope;
                }
                throw new VariableDoesNotExist(matcher.group(1));
            } else
                throw new InvalidCommand(line);
        }

    }

    /**
     * Tries to create new variables.
     * @param line The line to be checked.
     * @throws VariableError If one of the possible deceleration fails.
     */
    private void declareNewVariables(String line) throws VariableError {
        String configStr = "";
        String[] separatedWords = line.split(" ");
        if (separatedWords.length >= 2) {
            configStr += separatedWords[0] + " ";
            if (separatedWords[0].equals("final"))
                configStr += separatedWords[1] + " ";
        }
        line = line.replaceFirst(configStr, "");
        String[] variablesStr = line.split(",");
        for (String variableStr: variablesStr) {
            Variable variable = new Variable(configStr + variableStr.trim(), false, this);
            this.variables.put(variable.getName(), variable);
        }
    }

    /**
     * Finds the size of a scope (according to it's starting line number).
     * by iteration, it scans for a new closing bracket to appear, to signify the end
     * of the scope.
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @return the size of the scope (the number of lines in it).
     * @throws BadBracketsStructure If there is a bad brackets structure.
     */
    protected int findInnerScopeSize(int lineNum, int maxLineNum) throws BadBracketsStructure {
        int scopeSize = 1;
        int openBracketsNum = 1;
        int closedBracketsNum = 0;
        while (lineNum < this.rawData.size() && ((openBracketsNum - closedBracketsNum) != 0)) {
            lineNum++;
            try {
                if (this.rawData.get(lineNum).trim().endsWith("{")) openBracketsNum++;
                if (this.rawData.get(lineNum).trim().equals("}")) closedBracketsNum++;
            }
            catch (IndexOutOfBoundsException e) {
                throw new BadBracketsStructure(this.name);
            }
            scopeSize++;
        }
        if ((openBracketsNum - closedBracketsNum) != 0) {
            throw new BadBracketsStructure(this.name);
        }
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

//TODO: is maxLineNum is necessary? looks like it is never used!
