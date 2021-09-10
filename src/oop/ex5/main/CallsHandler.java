package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A singleton Class which holds references to the existing methods
 * in the program, alongside their names.
 */
public class CallsHandler {

    /**
     * A list of String, which holds all the code lines in which
     * a method is to be called.
     */
    public static List<String> calls = new ArrayList<>();

    /**
     * The Class's single instance.
     */
    public static CallsHandler singleInstance = new CallsHandler();

    /**
     * The Class constructor.
     */
    private CallsHandler() {
    }

    /**
     * The single instance getter.
     * @return a reference to the Class's single instance.
     */
    public static CallsHandler getSingleInstance() {
        return singleInstance;
    }

    /**
     * This method add a call to the list of calls.
     * @param call The call to be addded.
     */
    public static void addCall(String call) {
        calls.add(call);
    }

    /**
     * This method checks if the calls (in the list of calls) are valid.
     * A valid s-Java call must refer to an existing method, with an appropriate
     * number of arguments, with a matching type.
     * @throws BadArgumentsNum In case the number of arguments given in the call
     *                         don't match the actual method number of arguments.
     * @throws VariableError In case the given arguments type don't match the actual
     *                       method arguments type.
     * @throws MethodDoesNotExist In case of a call to a non-existing method.
     */
    public void callValidity() throws BadArgumentsNum, VariableError, MethodDoesNotExist {
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\s*(\\(.*\\))\\s*");
        Matcher matcher;
        String methodName;
        String arguments;
        for (String call: calls) {
            matcher = pattern.matcher(call.substring(0, call.length()-1));
            matcher.find();
            methodName = matcher.group(1);
            arguments = matcher.group(2).substring(1, matcher.group(2).length()-1);
            if (Method.allMethods.containsKey(methodName))
                checkPossibleArguments(Method.allMethods.get(methodName), arguments);
            else throw new MethodDoesNotExist(methodName);
        }
    }

    /**
     * This method checks if the arguments given in the method call line, matches the
     * actual arguments of the method (by amount and by type).
     * @param scope The relevant method.
     * @param arguments A String which holds the arguments given in the call line.
     * @throws BadArgumentsNum In case of an inappropriate number of arguments.
     * @throws VariableError In case of an inappropriate arguments type.
     */
    public void checkPossibleArguments(Method scope, String arguments) throws BadArgumentsNum, VariableError {
        String[] splitted = arguments.split(",");
        // in case no arguments needed
        if (splitted[0].equals("") && scope.arguments.size() == 0) return;
        // in case of a wrong number of arguments
        if (splitted.length != scope.arguments.size()) throw new BadArgumentsNum(scope.getName());
        List<Variable> orderdArguments = new ArrayList<>(scope.arguments.values());
        for (int i = 0; i < splitted.length; i++) {
            orderdArguments.get(i).setData(splitted[i].trim(), true, Scope.globalScope);
        }
    }
}


