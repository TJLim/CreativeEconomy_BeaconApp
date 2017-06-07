package creativeeconomy.beacon.com.creativeeconomy_app.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by TJ on 2017-02-28.
 */

public class RestartService extends BroadcastReceiver {
    public static final String ACTION_RESTART_PERSISTENTSERVICE
            = "ACTION.Restart.BackgroundService";


    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO Auto-generated method stub
        Log.d("Jinwoo", "RestartService called!@!@@@@@#$@$@#$@#$@#");

        if(intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {
            Intent i = new Intent(context, BackgroundService.class);
//Intent i = new Intent(this, PersistentService.class);
            context.startService(i);
        }
    }
}
