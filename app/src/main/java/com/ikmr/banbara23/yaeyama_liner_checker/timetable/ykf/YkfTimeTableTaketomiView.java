
package com.ikmr.banbara23.yaeyama_liner_checker.timetable.ykf;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ikmr.banbara23.yaeyama_liner_checker.R;

import butterknife.ButterKnife;

/**
 * 時刻表 竹富
 */
public class YkfTimeTableTaketomiView extends YkfTimeTableBaseView {

    public YkfTimeTableTaketomiView(Context context) {
        super(context);
    }

    public YkfTimeTableTaketomiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View layout = LayoutInflater.from(context).inflate(R.layout.view_time_table_ykf_taketomi, this);
        ButterKnife.bind(this, layout);
    }
}
