package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallsHandler {

    public static List<String> calls = new ArrayList<>();
    public static CallsHandler singleInstance = new CallsHandler();

    private CallsHandler() {

    }

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
        Pattern pattern = Pattern.compile(" *([a-zA-Z0-9_]+) *(\\(.*\\)) *");
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
                System.out.println("// invalid call - method not found//");
                throw new Exception();
            }
        }
    }

    public void checkPossibleArguments(Method scope, String arguments) throws Exception{
        String[] splitted = arguments.split(",");

        if (splitted.length != scope.givenArguments.size()) {
            System.out.println("// incorrect arguments num //");
            throw new Exception();
        }
        for (int i = 0; i < splitted.length; i++) {
            scope.givenArguments.get(i).setData(splitted[i].trim());
        }
    }



}


