
package com.ikmr.banbara23.yaeyama_liner_checker.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ikmr.banbara23.yaeyama_liner_checker.AnalyticsUtils;
import com.ikmr.banbara23.yaeyama_liner_checker.Const;
import com.ikmr.banbara23.yaeyama_liner_checker.R;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Liner;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.YkfLinerDetail;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.FragmentApiQueryInterface;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.StatusDetailDreamFragment;
import com.ikmr.banbara23.yaeyama_liner_checker.util.StringUtils;

/**
 * ステータス詳細のActivity
 */
public class StatusDetailDreamActivity extends BaseActivity implements FragmentApiQueryInterface {

    YkfLinerDetail mYkfLinerDetail;
    Liner mLiner;
    Fragment mFragment;
    private static final String TAG = Const.FireBaseAnalitycsTag.TOP;

    private boolean mQuerying = false;

    /**
     * クエリ起動中かどうか
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        mYkfLinerDetail = getIntent().getParcelableExtra(StatusDetailDreamActivity.class.getName());
        mLiner = mYkfLinerDetail.getLiner();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(R.string.company_name_dream);
        }

        // タイトル
        setTitleString();

        if (savedInstanceState != null) {
            mYkfLinerDetail = (YkfLinerDetail) savedInstanceState.get(YkfLinerDetail.class.getCanonicalName());
            mLiner = (Liner) savedInstanceState.get(Liner.class.getCanonicalName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFragment == null) {
            createFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsUtils.logAppOpenEvent(TAG);
    }

    /**
     * フラグメント作成
     */
    private void createFragment() {
        mFragment = StatusDetailDreamFragment.NewInstance(mYkfLinerDetail);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(YkfLinerDetail.class.getCanonicalName(), mYkfLinerDetail);
        outState.putParcelable(Liner.class.getCanonicalName(), mLiner);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mYkfLinerDetail = (YkfLinerDetail) savedInstanceState.get(YkfLinerDetail.class.getCanonicalName());
        mLiner = (Liner) savedInstanceState.get(Liner.class.getCanonicalName());
    }

    /**
     * タイトルバーの設定
     */
    private void setTitleString() {
        if (mLiner == null) {
            return;
        }
        if (mLiner.getPort() == null) {
            return;
        }
        if (StringUtils.isEmpty(mLiner.getPort().getPort())) {
            setTitle("運航状況の詳細");
        }

        setTitle(mLiner.getPort().getPort() + "航路");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_reload:
                if (!mQuerying) {
                    createFragment();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startQuery() {
        mQuerying = true;
    }

    @Override
    public void finishQuery() {
        mQuerying = false;
    }
}
