package com.seuic.app.store.ui.presenter;

import com.seuic.app.store.bean.RecycleObject;
import com.seuic.app.store.bean.RecycleTitleMoreBean;
import com.seuic.app.store.bean.RecycleViewType;
import com.seuic.app.store.bean.response.RecommendReceive;
import com.seuic.app.store.greendao.CheckUpdateAppsTable;
import com.seuic.app.store.greendao.GreenDaoManager;
import com.seuic.app.store.net.download.DownloadManager;
import com.seuic.app.store.ui.contact.UpdateContact;
import com.seuic.app.store.utils.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/9/26.
 *
 * @author dpuntu
 */

public class UpdatePresenter implements UpdateContact.Presenter {
    private UpdateContact.View mView;

    public UpdatePresenter(UpdateContact.View view) {
        this.mView = view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void checkUpdateApps() {
        List<CheckUpdateAppsTable> checkUpdateAppsTableList = GreenDaoManager.getInstance().queryCheckUpdateApps();
        List<RecycleObject> recycleObjects = new ArrayList<>();
        recycleObjects.add(new RecycleObject(RecycleViewType.RECYCEL_TITLE,
                                             new RecycleTitleMoreBean("可用更新", false, "")));
        List<RecommendReceive> recommendReceives = new ArrayList<>();
        if (checkUpdateAppsTableList != null && checkUpdateAppsTableList.size() > 0) {
            for (CheckUpdateAppsTable checkUpdateAppsTable : checkUpdateAppsTableList) {
                RecommendReceive recommendReceive = new RecommendReceive(checkUpdateAppsTable.getAppName(),
                                                                         checkUpdateAppsTable.getPackageName(),
                                                                         checkUpdateAppsTable.getAppSize(),
                                                                         checkUpdateAppsTable.getAppVersion(),
                                                                         checkUpdateAppsTable.getAppVersionId(),
                                                                         checkUpdateAppsTable.getAppDesc(),
                                                                         checkUpdateAppsTable.getMD5(),
                                                                         checkUpdateAppsTable.getDownloadName(),
                                                                         checkUpdateAppsTable.getAppIconName());
                recommendReceives.add(recommendReceive);
                recycleObjects.add(new RecycleObject(RecycleViewType.RECYCEL_DATA, recommendReceive));
            }
        }
        mView.setRecommendReceives(recommendReceives);
        mView.updateRecycleView(recycleObjects);
    }

    @Override
    public void updateAllApps(List<RecommendReceive> recommendReceives) {
        if (recommendReceives != null && recommendReceives.size() > 0) {
            for (RecommendReceive recommendReceive : recommendReceives) {
                DownloadManager.getInstance().start(recommendReceive.getAppVersionId());
            }
        } else {
            Loger.e("没有可以更新的软件");
        }
    }
}