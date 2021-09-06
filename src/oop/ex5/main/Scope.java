package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
     * a list of all given arguments for the method (can be empty).
     */
    protected LinkedHashMap<String, Variable> arguments = new LinkedHashMap<>();

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
    protected Scope outerScope;


    /**
     * a map of the scope's variables.
     */
    protected HashMap<String, Variable>  variables = new HashMap<>();

    /**
     * the scope's length (number of code lines).
     */
    protected int length;

    /**
     * the scope's name.
     */
    protected String name;


    /**
     * the Scope Class constructor.
     * @param scopeData the scope's code lines.
     */
    Scope(List<String> scopeData, Scope outerScope, String name) {
        this.name = name;

        System.out.println("----------------");
        for (String line: scopeData) {
            System.out.println(line); }
        System.out.println("----------------\n");

        this.length = scopeData.size();
        this.scopeData = scopeData;
        this.outerScope = outerScope;
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
        System.out.println("// CURRENTLY SCANNING SCOPE ***" + this.getName() + "*** //\n");
        int maxLineNum = this.scopeData.size();
        String line;
        for (int lineNum = 0; lineNum < maxLineNum; lineNum++) {
            line = this.scopeData.get(lineNum);

            // in case of a declaration or assigment
            if (line.endsWith(";")) declarationOrAssigment(line);

            // in case of a new scope creation
            else if (line.endsWith("{")) lineNum += scopeCreation(line, lineNum, maxLineNum) -1;

            // in case the current line is empty
            else if (line.trim().isEmpty()) continue;

            // in case of invalid line syntax
            else {
                throw new InvalidSyntax(this.name);
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

        Pattern pattern1 = Pattern.compile("^\\s*(if|while)(\\s*)*\\(.+\\)\\s*$");
        Pattern pattern2 = Pattern.compile("^\\s*void(\\s*)(\\w+)\\s*\\(\\w* *.*\\)\\s*$");

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
            System.out.println("// creates new method scope //");
            int innerScopeSize = scopeCreationAUX(lineNum, maxLineNum, "method", matcher2.group(2));
            System.out.println("// method's size is : "+ innerScopeSize + "//\n");
            return innerScopeSize;
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
     *      *                (with respect to the original scope line counter).
     * @param maxLineNum the number of the last line in the original scope.
     * @param type "method" or "ifWhile" - to determine the new Class's identity.
     * @return the new scope's size (the number of lines in it).
     * @throws Exception
     */
    protected int scopeCreationAUX(int lineNum, int maxLineNum, String type, String name) throws Exception {
        int innerScopeSize = findInnerScopeSize(lineNum, maxLineNum);
//        if (innerScopeSize == 0) {
//            System.out.println("// new scope's size is 0 //");
//            throw new Exception();
//        }
        List<String> innerScopeData = new ArrayList<>();
        // -1 for not including the closing bracket
        for (int i = lineNum; i < innerScopeSize + lineNum - 1; i++) {
            innerScopeData.add(this.scopeData.get(i));
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
     * this method helps to determine whether the scanned line decodes for a
     * new variable declaration on an old variable assigment.
     * @param line the scope's declaration line
     * @throws Exception
     */
    protected void declarationOrAssigment(String line) throws Exception {
        if (isDeclaration(line)) {
            System.out.println("// creates new variable //");
            line = line.substring(0,line.length()-1).trim(); // gets rid of the ;
            String configStr = "";
            String[] allWords = line.split(" ");
            if (allWords.length >= 2) {
                configStr += allWords[0] + " ";
                if (allWords[0].equals("final"))
                    configStr += allWords[1] + " ";
            }
            line = line.replaceFirst(configStr, "");
            String[] variablesStr = line.split(",");
            for (String variableStr: variablesStr) {
                this.variables.put(variableStr.trim(), new Variable(configStr + variableStr.trim(), false, this));
            }
        }
        else if (isPossibleCall(line)) {
            System.out.println("// added a new possible call //");
            CallsHandler.addCall(line);
        }
        else if (isReturnLine(line)) {
            return;
        }
        else {
            System.out.println("// checking for possible value assigment //");
            checkPossibleAssigment(line.substring(0, line.length() - 1));
        }
    }

    /**
     * this method checks if the given line is a valid s-Java return
     * statement line.
     * @param line the line to be checked.
     * @return true in case the given line is a valid s-Java return statement,
     * false otherwise.
     */
    public boolean isReturnLine(String line) {
        Pattern pattern = Pattern.compile("\\s*return\\s*;\\s*");
        Matcher matcher = pattern.matcher(line);
        //TODO: Orr did you like the use of instanceof? let me
        // know if you have a better solution
        return matcher.find() && (this instanceof Method);
    }



    /**
     * this method checks if the method call is valid.
     * @param line a String in which a method is to be called.
     * @return true in case the line holds a valid s-Java method call,
     * false otherwise.
     */
    public boolean isPossibleCall(String line) {
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\s*(\\(.*\\))\\s*");
        // removing the '}'
        Matcher matcher = pattern.matcher(line.substring(0, line.length()-1));
        return matcher.find();
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
            try {
                if (this.scopeData.get(lineNum).trim().endsWith("{")) openBracketsNum++;
                if (this.scopeData.get(lineNum).trim().equals("}")) closedBracketsNum++;
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
     * this method checks if the given line indicates for a valid
     * value assigment.
     * @param line the line to be checked.
     * @throws Exception
     */
    // TODO: check if this method is still relevant
    protected void checkPossibleAssigment(String line) throws Exception {
        // TODO: check for multiple variables single line declaration
        Pattern pattern = Pattern.compile("^(.*)(\\s*=\\s*)(.+)");
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

        Variable varToAdd = new Variable(line.trim(), false, this);
        return true;
    }


    void printVariable(Variable variable) {
            System.out.println(variable.getType() + " " + variable.getName() + "\n");
    }

    public String getName() {
        return this.name;
    }
}
