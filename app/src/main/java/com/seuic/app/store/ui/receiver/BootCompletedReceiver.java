package com.seuic.app.store.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seuic.app.store.AppStoreApplication;
import com.seuic.app.store.bean.AppInfo;
import com.seuic.app.store.greendao.DataUsageTable;
import com.seuic.app.store.greendao.GreenDaoManager;
import com.seuic.app.store.ui.service.DataUsageService;
import com.seuic.app.store.utils.Loger;
import com.seuic.app.store.utils.SpUtils;
import com.seuic.app.store.utils.TimesBytesUtils;

import java.util.List;

/**
 * Created on 2017/10/13.
 *
 * @author dpuntu
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Loger.e("----------开机广播----------");
            List<AppInfo> appInfos = AppStoreApplication.getApp().getAppInfos();
            for (AppInfo info : appInfos) {
                DataUsageTable mDataUsageTableFinal = GreenDaoManager.getInstance().queryDataUsageTable(info.getPackageName(), TimesBytesUtils.BytesType.FINAL);
                DataUsageTable mDataUsageTableOnce = GreenDaoManager.getInstance().queryDataUsageTable(info.getPackageName(), TimesBytesUtils.BytesType.ONCE);
                if (mDataUsageTableFinal == null) {
                    if (mDataUsageTableOnce != null) {
                        GreenDaoManager.getInstance().insertDataUsageTableDao(info.getPackageName(),
                                                                              mDataUsageTableOnce.getUidRxBytes(),
                                                                              mDataUsageTableOnce.getUidTxBytes(),
                                                                              TimesBytesUtils.BytesType.FINAL);
                    }
                } else {
                    if (mDataUsageTableOnce != null) {
                        GreenDaoManager.getInstance().insertDataUsageTableDao(info.getPackageName(),
                                                                              mDataUsageTableFinal.getUidRxBytes() + mDataUsageTableOnce.getUidRxBytes(),
                                                                              mDataUsageTableFinal.getUidTxBytes() + mDataUsageTableOnce.getUidTxBytes(),
                                                                              TimesBytesUtils.BytesType.FINAL);
                    }
                }
            }
            SpUtils.getInstance().putLong(SpUtils.MOBILE_RX_BYTES,
                                                      SpUtils.getInstance().getLong(SpUtils.MOBILE_RX_BYTES, 0)
                                                              + SpUtils.getInstance().getLong(SpUtils.MOBILE_RX_BYTES_ONCE, 0));
            SpUtils.getInstance().putLong(SpUtils.MOBILE_TX_BYTES,
                                                      SpUtils.getInstance().getLong(SpUtils.MOBILE_TX_BYTES, 0)
                                                              + SpUtils.getInstance().getLong(SpUtils.MOBILE_TX_BYTES_ONCE, 0));
            SpUtils.getInstance().putLong(SpUtils.TOTAL_RX_BYTES,
                                                      SpUtils.getInstance().getLong(SpUtils.TOTAL_RX_BYTES, 0)
                                                              + SpUtils.getInstance().getLong(SpUtils.TOTAL_RX_BYTES_ONCE, 0));
            SpUtils.getInstance().putLong(SpUtils.TOTAL_TX_BYTES,
                                                      SpUtils.getInstance().getLong(SpUtils.TOTAL_TX_BYTES, 0)
                                                              + SpUtils.getInstance().getLong(SpUtils.TOTAL_TX_BYTES_ONCE, 0));
            TimesBytesUtils.cleanOnceBytes();
            context.startService(new Intent(context, DataUsageService.class));
        }
    }
}
