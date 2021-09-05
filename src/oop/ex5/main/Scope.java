package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * this class represents a scope in the s-Java program. each instance
 * of this class is an individual scope (starts with a valid s-Java
 * code line with an opening bracket at its end, followed by a
 * single closing bracket). in this program, an instance of this class
 * may be one of the three: a general scope, a method defined scope or a
 * condition (if/while) defined scope.
 */
public class Scope {

    /**
     * the scope's data. each line is preserved in a single
     * list cell.
     */
    protected List<String> scopeData;

    /**
     * a list of the scope's inner scopes.
     */
    protected List<Scope> innerScopes = new ArrayList<>();

    /**
     * a list of the scope's outer scopes.
     */
    protected List<Scope> outerScopes = new ArrayList<>();


    /**
     * a list of the scope's variables.
     */
    protected List<Variable> variables = new ArrayList<>();

    /**
     * the scope's length (number of code lines).
     */
    protected int length;


    /**
     * the Scope Class constructor.
     * @param scopeData the scope's code lines.
     */
    Scope(List<String> scopeData, Scope outerScope) {
        System.out.println("----------------");
        for (String line: scopeData) {
            System.out.println(line);
        }
        System.out.println("----------------\n");
        this.length = scopeData.size();
        this.scopeData = scopeData;
        if (outerScope != null) this.outerScopes.add(outerScope);
    }

    /**
     * the scope's data decoder.
     * this method iterates over the scope's data (each iteration scans an
     * individual code line). according to the exercise's instructions, and the
     * s-Java coding specifications, this method matched each line to it's
     * appropriate cipher.
     * @throws Exception
     */
    protected void scan() throws Exception {
        System.out.println("// CURRENTLY SCANNING SCOPE " + this.toString() + " //\n");
        int maxLineNum = this.scopeData.size();
        String line;
        for (int lineNum = 0; lineNum < maxLineNum; lineNum++) {

            line = this.scopeData.get(lineNum);

            // in case of a declaration or assigment
            if (line.endsWith(";")) declarationOrAssigment(line);

            // in case of a new scope creation
            else if (line.endsWith("{")) lineNum += scopeCreation(line, lineNum, maxLineNum);

            // in case the current line is empty
            else if (line.trim().isEmpty()) continue;

            // in case of invalid line syntax
            else {
                System.out.println("// invalid syntax in line " + lineNum + "//\n");
                throw new Exception();
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
     * @throws Exception
     */
    protected int scopeCreation(String line, int lineNum, int maxLineNum) throws Exception{
        System.out.println("// line ends with '{' //");

        Pattern pattern1 = Pattern.compile("^ *(if|while)( *)*\\(.+\\) *$");
        Pattern pattern2 = Pattern.compile("^ *void( *)\\w+ *\\(\\w* *.*\\) *$");

        // matcher (without the "{")
        Matcher matcher1 = pattern1.matcher(line.substring(0, line.length() - 1));
        Matcher matcher2 = pattern2.matcher(line.substring(0, line.length() - 1));

        // if/while statement
        if (matcher1.find()) {
            System.out.println("// creates new if/while scope //");
            int innerScopeSize = scopeCreationAUX(lineNum, maxLineNum, "ifWhile");
            System.out.println("// if/while scope's size is : "+ innerScopeSize + "//\n");
            return innerScopeSize;
        }
        // a method declaration statement
        else if (matcher2.find()) {
            System.out.println("// creates new method scope //");
            int innerScopeSize = scopeCreationAUX(lineNum, maxLineNum, "method");
            System.out.println("// method's size is : "+ innerScopeSize + "//\n");
            return innerScopeSize;
        }
        // in case the line ends with "{" but no void/if/while with a
        // valid s-java declaration structure was found
        else {
            System.out.println("// invalid method/if/else scope declaration //");
            System.out.println("// problem in line: " + lineNum);
            throw new Exception();
        }
    }

    /**
     * this method 'creates' a new Method or Scondition Class, corresponding
     * to the given type.
     * @param lineNum the number of the first line in this new declared scope
     *      *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @param type "method" or "ifWhile" - to determine the new Class's identity.
     * @return the new scope's size (the number of lines in it).
     * @throws Exception
     */
    protected int scopeCreationAUX(int lineNum, int maxLineNum, String type) throws Exception {
        int innerScopeSize = findInnerScopeSize(lineNum, maxLineNum);
        if (innerScopeSize == 0) {
            System.out.println("// new scope's size is 0 //");
            throw new Exception();
        }
        List<String> innerScopeData = new ArrayList<>();
        // -1 for not including the closing bracket
        for (int i = lineNum; i < innerScopeSize + lineNum - 1; i++) {
            innerScopeData.add(this.scopeData.get(i));
        }
        if (type.equals("method")) {
            Method method = new Method(innerScopeData, this);
            this.innerScopes.add(method);
        }
        else {
            Scondition scondition = new Scondition(innerScopeData, this);
            this.innerScopes.add(scondition);
        }
        return innerScopeSize;
    }


    /**
     * this method helps to determine whether the scanned line decodes for a
     * new variable declaration on an old variable assigment.
     * @param line the scope's declaration line
     * @throws Exception
     */
    protected void declarationOrAssigment(String line) throws Exception {
        if (isDeclaration(line)) {
            System.out.println("// creates new variable //");
            Variable variable = new Variable(line.trim().substring(0, line.length() - 1), false);
            this.variables.add(variable);
            printVariable(variable);
        }
        else {
            System.out.println("// checking for possible value assigment //");
            checkPossibleAssigment(line.substring(0, line.length() - 1));
        }
    }

    /**
     * this method checks if the given line starts with one of the s-Java's reserved
     * keywords, which indicates a new variable declaration.
     * @param line the line to be checked.
     * @return true in case the line decodes for a variable declaration, false otherwise.
     * @throws Exception
     */
    protected boolean isDeclaration(String line) throws Exception {
        return (line.trim().startsWith("final") || line.trim().startsWith("int") ||
                line.trim().startsWith("double") || line.trim().startsWith("String")
                || line.trim().startsWith("boolean") || line.trim().startsWith("char"));
    }


    /**
     * this method finds the size of a scope (according to it's starting line number).
     * by iteration, it scans for a new closing bracket to appear, to signify the end
     * of the scope.
     * @param lineNum the number of the first line in this new declared scope
     *      *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @return the size of the scope (the number of lines in it).
     * @throws Exception
     */
    protected int findInnerScopeSize(int lineNum, int maxLineNum) throws Exception {
        int scopeSize = 1;
        int openBracketsNum = 1;
        int closedBracketsNum = 0;
        while (lineNum < this.scopeData.size() && ((openBracketsNum - closedBracketsNum) != 0)) {
            lineNum++;
            if (this.scopeData.get(lineNum).trim().endsWith("{")) openBracketsNum++;
            if (this.scopeData.get(lineNum).trim().equals("}")) closedBracketsNum++;
            scopeSize++;
        }

        if ((openBracketsNum - closedBracketsNum) != 0) {
            System.out.println("// no closing bracket was found //");
            throw new Exception();
        }

        return scopeSize;
    }

    /**
     * this method checks if the given line indicates for a valid
     * value assigment.
     * @param line the line to be checked.
     * @throws Exception
     */
    protected void checkPossibleAssigment(String line) throws Exception {
        // TODO: check for multiple variables single line declaration
        Pattern pattern = Pattern.compile("^(.*)( *= *)(.+)");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            System.out.println("// value assigment invalid //\n");
            throw new Exception();
        }
        else {
            if (checkPossibleAssigmentAUX(line)) return;
        }
        System.out.println("// value assigment invalid //\n");
        throw new Exception();
    }

    /**
     * in case of a regex match (a valid 'value assigment' syntax), this method
     * searches for the variable to be assigned in the current scope's variables list,
     * as well as in the variables lists of its outer scopes.
     * @param line the assigment line.
     * @return true, in case a variable with the same name was found in relevant scopes,
     * false otherwise.
     * @throws Exception
     */
    protected boolean checkPossibleAssigmentAUX (String line) throws Exception {
        String[] splitted = line.trim().split("=");
        String name = splitted[0].trim();

        // adding the current scope to its outer scopes list, in order to
        // search in it as well
        this.outerScopes.add(this);
        for (Scope scope: this.outerScopes) {
            for (Variable variable : scope.variables) {
                if (variable.getName().equals(name)) {
                    System.out.println("// setting argument '" + variable.getName() + "'" + " value to '" +
                            splitted[1].trim() + "' //\n");
                    variable.setData(splitted[1].trim());
                    this.outerScopes.remove(this);
                    return true;
                }
            }
        }
        // removing the current scope from its outer scopes list
        this.outerScopes.remove(this);
        return false;
    }


    void printVariable(Variable variable) {
            System.out.println(variable.getType() + " " + variable.getName() + "\n");
    }

}
