package creativeeconomy.beacon.com.creativeeconomy_app.common;

import android.util.Log;

import java.util.Calendar;

import creativeeconomy.beacon.com.creativeeconomy_app.main.Constants;

/**
 * Created by chihong on 2017-01-17.
 */

public class MappingCheckClass {

    public boolean isReceivedMappingContents(String mappingId) {

        boolean isMappingedOk = false;
        int i = 0;

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis() / 1000;

        Log.d("Jinwoo", "MappingCHeckClass.currentTime : " + currentTime);
        Log.d("Jinwoo", "MappingCHeckClass.mappingId : " + mappingId);

        for (i = 0; i < Constants.commonVOList.size(); i++) {

            if (mappingId.equals(Constants.commonVOList.get(i).maappingID)) {
                isMappingedOk = true;
                break;
            }
            isMappingedOk = false;
        }

        if (isMappingedOk) {

            if( Constants.commonVOList.get(i).mappingTime + Constants.delayTime <  currentTime){

                Constants.commonVOList.remove(i);

                CommonVO vo = new CommonVO();

                vo.mappingTime = currentTime;
                vo.maappingID = mappingId;

                Constants.commonVOList.add(vo);

                return true;

            } else {

                return false;
            }

        } else {
            CommonVO vo = new CommonVO();

            vo.mappingTime = currentTime;
            vo.maappingID = mappingId;
            Constants.commonVOList.add(vo);

            return true;
        }
    }
}
