package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


/*import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;*/

import java.io.IOException;
import java.util.List;

import ch.loway.oss.ari4java.ARI;
import ch.loway.oss.ari4java.AriVersion;
import ch.loway.oss.ari4java.generated.models.AsteriskInfo;
import ch.loway.oss.ari4java.generated.models.Channel;
import ch.loway.oss.ari4java.tools.ARIException;
import ch.loway.oss.ari4java.tools.RestException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Button but1;
    //public static ManagerConnection managerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        but1 = findViewById(R.id.but1);
        but1.setOnClickListener(newclick);
    }

    View.OnClickListener newclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ARI ari = null;
            try {
                ari = ARI.build("http://192.168.0.254:8088/", "test-app", "restuser", "pr09ramm1$t", AriVersion.IM_FEELING_LUCKY);
                //String s = "";
                List<Channel> channels = ari.channels().list().execute();
                for(Channel chan:channels){
                    Log.d("aster","chan.getName() = "+chan.getName().toString());
                }

               Channel chan1 = ari.channels().originate("Local/89145077248@from-internal")
                        .setExtension("334485").setContext("from-internal").setPriority(1).setCallerId("89145077248").execute();

                Log.d("aster","Channel:" + chan1.getId() + " in state " + chan1.getState());
            } catch (ARIException e) {
                e.printStackTrace();
            }

        }
        };


}