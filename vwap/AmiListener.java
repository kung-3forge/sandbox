import com.f1.ami.client.*;

import java.util.Map;


public class AmiListener implements AmiClientListener {


    @Override
    public void onMessageReceived(com.f1.ami.client.AmiClient amiClient, long l, long l1, int i, CharSequence charSequence) {
        //System.out.println("OnMsgReceive:"+ charSequence);
    }

    @Override
    public void onMessageSent(com.f1.ami.client.AmiClient amiClient, CharSequence charSequence) {
       // System.out.println("OnMsgSent:" + charSequence);

    }

    @Override
    public void onConnect(com.f1.ami.client.AmiClient amiClient) {
        System.out.println("onConnect");

    }

    @Override
    public void onDisconnect(com.f1.ami.client.AmiClient amiClient) {
        System.out.println("OnDisconnect");

    }

    @Override
    public void onCommand(com.f1.ami.client.AmiClient amiClient, String s, String s1, String s2, String s3, String s4, Map<String, Object> map) {
        System.out.println("OnCommand:" + s1);

    }

    @Override
    public void onLoggedIn(com.f1.ami.client.AmiClient amiClient) {
        System.out.println("onLoggedIn");

    }
}
