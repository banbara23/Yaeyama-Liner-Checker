
package com.ikmr.banbara23.yaeyama_liner_checker.api;

import com.google.gson.Gson;
import com.ikmr.banbara23.yaeyama_liner_checker.Base;
import com.ikmr.banbara23.yaeyama_liner_checker.R;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Company;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Result;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 安栄一覧の取得
 */
public class StatusListApi {

    /***
     * RxJavaで一覧取得
     *
     * @param company
     * @return
     */
    public static Observable<Result> request(final Company company) {
        return Observable
                .create(new ObservableOnSubscribe<Result>() {
                    @Override
                    public void subscribe(ObservableEmitter<Result> emitter) {
                        String ncmbTableName = null;
                        switch (company) {
                            case ANNEI:
                                ncmbTableName = Base.getContext().getString(R.string.NCMB_annei_table);
                                break;
                            case YKF:
                                ncmbTableName = Base.getContext().getString(R.string.NCMB_ykf_table);
                                break;
                            case DREAM:
                                ncmbTableName = Base.getContext().getString(R.string.NCMB_dream_table);
                                break;
                        }
                        NCMBQuery<NCMBObject> query = new NCMBQuery<>(ncmbTableName);
                        query.setLimit(1);
                        query.addOrderByDescending(Base.getContext().getString(R.string.NCMB_sort_column_name));
                        List<NCMBObject> results = null;
                        try {
                            results = query.find();
                        } catch (NCMBException e) {
                            emitter.onError(e);
                        }
                        try {
                            NCMBObject object = results.get(0);
                            String json = object.getString(Base.getContext().getString(R.string.NCMB_get_column_name));
                            Result result = new Gson().fromJson(json, Result.class);
                            emitter.onNext(result);
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                });
    }
}
