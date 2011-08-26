package com.ACStache.RangedWolves;

public class RWDebug
{
    private static Boolean debug;
    
    //set debug mode to true or false
    public static void setDebug(Boolean debugTF)
    {
        debug = debugTF;
    }
    
    //get the state of debug mode
    public static Boolean getDebug()
    {
        return debug;
    }
}