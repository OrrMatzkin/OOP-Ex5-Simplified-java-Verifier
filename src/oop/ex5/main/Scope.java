package oop.ex5.main;

import java.util.List;

public class Scope {
    protected List<String> scopeData;
    private List<Scope> innerScopes;
    // private List<Variable> variables;
    private int length;

    Scope(List<String> scopeData) {
        this.scopeData = scopeData;
    }


}
