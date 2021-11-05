package com.asteriskmanager.util;

import com.asteriskmanager.telnet.AmiState;

public interface ConnectionCallback {

    void onBegin();

    void onSuccess(AmiState amistate);

    void onFailure(AmiState amiState);

    void onEnd();
}
