// IAidlInterface.aidl
package com.pgc.testaidldemo1;

// Declare any non-default types here with import statements
import com.pgc.testaidldemo1.IAidlCallBack;
interface IAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallBack(IAidlCallBack iAidlCallBack);
    void unregisterCallBack(IAidlCallBack iAidlCallBack);
    void sendMessage(String message);
    List<String> getMessages();
}
