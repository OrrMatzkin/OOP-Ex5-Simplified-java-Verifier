package oop.ex5.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scope {
    protected List<String> scopeData;
    private List<Scope> innerScopes;
    private List<Variable> variables;
    private int length;

    Scope(List<String> scopeData) {
        this.length = scopeData.size();
        this.scopeData = scopeData;
    }


    protected void scan() throws Exception {
        int lineNum = 0;
        int maxLineNum = this.length;
        for (String line: this.scopeData) {
            // skip the scopes declaration
            if (lineNum == 0) {
                continue;
            }
            lineNum++;

            // in case of a declaration or assigment
            if (line.endsWith(";")) {
                if (line.trim().startsWith("final") || line.trim().startsWith("int") ||
                        line.trim().startsWith("double") || line.trim().startsWith("String")
                || line.trim().startsWith("boolean") || line.trim().startsWith("char")) {
                    // Variable variable = new Variable(line.trim().substring(0, line.length() - 1), false);
                    // this.variables.add(variable);
                }
                else {
                    checkPossibleAssigment(line.substring(0, line.length() - 1));
                }


            } else if (line.endsWith("{")) {

                Pattern pattern1 = Pattern.compile("^ *(if|while) *([^ ].+) *(\\(.*\\)) *");
                Pattern pattern2 = Pattern.compile("^ *(void) *([^ ].+) *(\\\\(.*\\\\)) *\"");

                // matcher (without the "{")
                Matcher matcher1 = pattern1.matcher(line.substring(0, line.length() - 1));
                Matcher matcher2 = pattern2.matcher(line.substring(0, line.length() - 1));

                // in case the line ends with "{" but no void/if/while with a
                // valid s-java declaration structure was found
                if (!matcher1.find() && !matcher2.find()) throw new Exception();
                // if/while statement
                else if (matcher1.group(1).equals("if") || matcher1.group(1).equals("while")) {
                    int innerScopeSize = scopeCreation(lineNum, maxLineNum, "ifWhile");
                    lineNum += innerScopeSize - 1;
                }
                // a method declaration statement
                else if (matcher2.group(1).equals("void")) {
                    int innerScopeSize = scopeCreation(lineNum, maxLineNum, "method");
                    lineNum += innerScopeSize - 1;
                }
            }
            else {
                throw new Exception();
            }
        }
    }


    protected int scopeCreation(int lineNum, int maxLineNum, String type) throws Exception {
        int innerScopeSize = findInnerScopeSize(lineNum, maxLineNum);
        if (innerScopeSize == 0) throw new Exception();
        List<String> innerScopeData = null;
        for (int i = lineNum; i < innerScopeSize; i++) {
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



    protected int findInnerScopeSize(int lineNum, int maxLineNum) {
        int methodSize = 0;
        while (!this.scopeData.get(lineNum).trim().equals("}") && lineNum < maxLineNum) {
            maxLineNum++;
            methodSize++;
        }
        return methodSize;
    }


    protected void checkPossibleAssigment(String line) throws Exception {
        Pattern pattern = Pattern.compile("^(.*)( *= *)(\\w+)");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            throw new Exception();
        }
        else {
            String[] splitted = line.trim().split("=");
            String name = splitted[0];
            for (Variable variable: this.variables) {
                if (variable.getName.equals(name)) {
                        variable.setData(splitted[1]);
                        break;
                }
            }
        }
    }

}
