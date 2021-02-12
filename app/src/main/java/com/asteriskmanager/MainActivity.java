package com.asteriskmanager;

import androidx.appcompat.app.AppCompatActivity;

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

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;

import java.io.IOException;
import java.util.List;

import ch.loway.oss.ari4java.ARI;
import ch.loway.oss.ari4java.AriVersion;
import ch.loway.oss.ari4java.generated.models.Application;
import ch.loway.oss.ari4java.generated.models.AsteriskInfo;
import ch.loway.oss.ari4java.generated.models.Channel;
import ch.loway.oss.ari4java.generated.models.Module;
import ch.loway.oss.ari4java.tools.ARIException;
import ch.loway.oss.ari4java.tools.RestException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Button but1,but2;
    //public static ManagerConnection managerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        but1 = findViewById(R.id.but1);
        but1.setOnClickListener(newclick);
        but2 = findViewById(R.id.agibut);
        but2.setOnClickListener(agiclick);
    }

    View.OnClickListener newclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ARI ari = null;
            try {
                ari = ARI.build("http://188.75.221.200:30888"/*"http://188.75.221.200:30888/"*/, "test-app", "restuser", "pr09ramm1$t", AriVersion.IM_FEELING_LUCKY);
                //String s = "";
                List<Channel> channels = ari.channels().list().execute();
                for(Channel chan:channels){
                    Log.d("aster","chan.getName() = "+chan.getName().toString());
                }
               // ari.get
                ari.applications().list().execute();
                List<Application> lm = ari.applications().list().execute();
                for(Application lmodule:lm){
                    Log.d("aster","Application = "+lmodule.getName() );

                }
               // ApplicationList applicationList=ari.applications().list().execute();

               Channel chan1 = ari.channels().originate("Local/89145077248@from-internal")
                       .setExtension("334485").setContext("from-internal").setPriority(1).setCallerId("89145077248").execute();

          //      Log.d("aster","Channel:" + chan1.getId() + " in state " + chan1.getState());
            } catch (ARIException e) {
                e.printStackTrace();
            }

        }
        };

    View.OnClickListener agiclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ManagerConnectionFactory factory = new
                    ManagerConnectionFactory("192.168.0.254", "amiuser",
                    "0mMelaN0f0n");
            ManagerConnection managerConnection=factory.createManagerConnection();
            OriginateAction originateAction=new OriginateAction();

            final String randomUUID=java.util.UUID.randomUUID().toString();

            System.out.println("ID random:_"+randomUUID);

            originateAction.setChannel("Local/89145077248@from-internal");
            originateAction.setContext("from-internal");
            originateAction.setCallerId("89145077248"); // what will be showed on the phone screen (in most cases your phone)
            originateAction.setExten("334485"); //where to call.. the target extension... internal extension or the outgoing number.. the 0[nomberToCall]
            originateAction.setPriority(1);// priority of the call
           // originateAction.setTimeout(1000); // the time that a pickup event will be waited for
            originateAction.setVariable("UUID", randomUUID); // asigning a unique ID in order to be able to hangup the call.


            try {
                managerConnection.login();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticationFailedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            // send the originate action and wait for a maximum of 30 seconds for Asterisk
                // to send a reply
              //  managerConnection.sendAction(originateAction, 30000);

                // print out whether the originate succeeded or not
                //System.out.println("getResponse");
                //System.out.println(originateResponse.getResponse());

                // and finally log off and disconnect
              //  managerConnection.logoff();



        }
    };
}