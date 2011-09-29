package com.ACStache.RangedWolves;

public class RWDebug
{
    private static boolean debug;
    
    //set debug mode to true or false
    public static void setDebug(Boolean debugTF)
    {
        debug = debugTF;
    }
    
    //get the state of debug mode
    public static boolean getDebug()
    {
        return debug;
    }
}