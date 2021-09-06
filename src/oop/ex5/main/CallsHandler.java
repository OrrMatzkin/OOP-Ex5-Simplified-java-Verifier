package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a singleton Class which holds references to the existing methods
 * in the program, alongside their names.
 */
public class CallsHandler {

    /**
     * a list of String, which holds all the code lines in which
     * a method is to be called
     */
    public static List<String> calls = new ArrayList<>();

    /**
     * the Class's single instacne.
     */
    public static CallsHandler singleInstance = new CallsHandler();

    /**
     * the Class constructor.
     */
    private CallsHandler() {

    }

    /**
     * the single instance getter
     * @return a reference to the Class's single instance
     */
    public static CallsHandler getSingleInstance() {
        return singleInstance;
    }

    public static void addCall(String call) {
        calls.add(call);
    }

    public List<String> getCalls() {
        return calls;
    }

    public void print() {
        for (String call: calls) {
            System.out.println(call);
        }
    }


    public void callValidity() throws Exception{
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\s*(\\(.*\\))\\s*");
        Matcher matcher;
        String methodName;
        String arguments;
        for (String call: calls) {
            matcher = pattern.matcher(call.substring(0, call.length()-1));
            matcher.find();
            methodName = matcher.group(1);
            arguments = matcher.group(2).substring(1, matcher.group(2).length()-1);
            if (Method.allMethods.containsKey(methodName)) {
                checkPossibleArguments(Method.allMethods.get(methodName), arguments);
                System.out.println("// valid call //");
            }
            else {
                throw new MethodDoesNotExist(methodName);
            }
        }
    }

    public void checkPossibleArguments(Method scope, String arguments) throws Exception{

        String[] splitted = arguments.split(",");
        if (splitted[0].equals("") && scope.arguments.size() == 0) {
            return;
        }

        if (splitted.length != scope.arguments.size()) {
            throw new BadArgumentsNum(scope.getName());
        }


        List<Variable> orderdArguments = new ArrayList<>(scope.arguments.values());
        for (int i = 0; i < splitted.length; i++) {

//            if (Variable.existingVariables.containsKey(splitted[i])) {
//                if (!Variable.existingVariables.get(splitted[i]).isInitialized()) {
//                    System.out.println("// variable not initialized in scope: " + scope.getName() + "//");
//                    throw new Exception();
//                }
//                else
                orderdArguments.get(i).setData(splitted[i].trim(), true);

//            else {
//                System.out.println("// variable not exist " + splitted[i] + "//");
//                throw new Exception();
//            }


//            scope.givenArguments.get(i).setData(splitted[i].trim());
        }
    }



}


