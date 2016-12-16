// IRemoteService.aidl
package com.example.doyun.mylifelogger;

import com.example.doyun.mylifelogger.IRemoteServiceCallback;

// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    boolean registerCallback(IRemoteServiceCallback callback);
    boolean unregisterCallback(IRemoteServiceCallback callback);

}
