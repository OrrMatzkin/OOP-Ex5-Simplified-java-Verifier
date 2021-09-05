package oop.ex5.main;

public class CallsHandler {

    public static CallsHandler singleInstance = new CallsHandler();

    private CallsHandler() {

    }

    public static CallsHandler getSingleInstance() {
        return singleInstance;
    }
}

