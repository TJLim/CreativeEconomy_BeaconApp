package creativeeconomy.beacon.com.creativeeconomy_app.main;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;

import com.kakao.oreum.tamra.base.Spot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import creativeeconomy.beacon.com.creativeeconomy_app.common.CommonVO;

public class Constants {

    public static Set<Spot> spotSet = new LinkedHashSet<>();
    public static String bluetoothState;
    public static String pushState;
    public static String gps1 = "";
    public static String gps2 = "";
    public static double lat;
    public static double lon;

    public static int delayTime = 60;

    public static ArrayList<CommonVO> commonVOList = new ArrayList<>();

    public static BroadcastReceiver scrOnReceiver;
    public static BroadcastReceiver scrOffReceiver;
    public static boolean mIsReceiverRegistered = false;

    public static BroadcastReceiver btOnReceiver;
    public static boolean btIsReceiverRegistered = false;
    public static BluetoothAdapter btAdapter;

    public static boolean backGroundState = false;
}
