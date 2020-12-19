// MusicCentralApi.aidl
package com.example.musiccommon;
import java.util.Map;

// Declare any non-default types here with import statements

interface MusicCentralApi {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


    String getTitle(int index);

    Map getInfo(int index);


}
