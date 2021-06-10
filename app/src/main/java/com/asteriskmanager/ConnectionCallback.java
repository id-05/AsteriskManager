package com.asteriskmanager;

import com.asteriskmanager.telnet.AmiState;

interface ConnectionCallback <V> {

    void onBegin(); //Асинхронная операция началась

    void onSuccess(AmiState amistate); //Получили результат

    void onFailure(AmiState amiState); //Получили ошибку

     void onEnd(); //Операция закончилась
}
