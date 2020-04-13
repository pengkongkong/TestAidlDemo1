// IAidlCallBack.aidl
package com.pgc.testaidldemo1;

// Declare any non-default types here with import statements

interface IAidlCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onMessageSuccess(String message);
}
