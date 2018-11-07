package com.agileengine.util;

public class Validator {
    private static int VALID_ARGS_NUMBER=3;
    public static boolean validateMainArgs(String [] args){
        if (args.length<VALID_ARGS_NUMBER) {
            return false;
        }
        return true;
    }
}
