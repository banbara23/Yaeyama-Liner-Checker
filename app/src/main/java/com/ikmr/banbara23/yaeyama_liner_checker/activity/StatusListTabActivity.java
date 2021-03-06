
package com.ikmr.banbara23.yaeyama_liner_checker.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.ikmr.banbara23.yaeyama_liner_checker.AnalyticsUtils;
import com.ikmr.banbara23.yaeyama_liner_checker.Base;
import com.ikmr.banbara23.yaeyama_liner_checker.BuildConfig;
import com.ikmr.banbara23.yaeyama_liner_checker.Const;
import com.ikmr.banbara23.yaeyama_liner_checker.PagerAdapter;
import com.ikmr.banbara23.yaeyama_liner_checker.R;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Company;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.StatusListTabFragment;
import com.ikmr.banbara23.yaeyama_liner_checker.util.CustomTabUtil;
import com.nifty.cloud.mb.core.NCMB;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 一覧タブActivity
 */
public class StatusListTabActivity extends BaseActivity implements StatusListTabFragment.EmptyClickListener {

    private static final String TAG = Const.FireBaseAnalitycsTag.STATUS_LIST;
    private static final int TAB_FIRST = 0;
    private static final int TAB_SECOND = 1;
    private static final int TAB_THREAD = 2;

    @Bind(R.id.activity_list_tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.activity_list_tab_view_pager)
    ViewPager viewPager;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tab);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        NCMB.initialize(Base.getContext(),
                BuildConfig.NCMB_APPLICATION_ID,
                BuildConfig.NCMB_CLIENT_KEY);
        createTab(getCurrentPosition());
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsUtils.logAppOpenEvent(TAG);
    }

    /**
     * 前回開いていたタブ番号を取得
     *
     * @return タブ番号を取得
     */
    private int getCurrentPosition() {
        Company company = (Company) getIntent().getSerializableExtra(StatusListTabActivity.class.getCanonicalName());
        switch (company) {
            case ANNEI:
                return TAB_FIRST;
            case YKF:
                return TAB_SECOND;
            case DREAM:
                return TAB_THREAD;
            default:
                return 0;
        }
    }

    /**
     * タブの作成
     *
     * @param position
     */
    private void createTab(int position) {
        if (tabLayout == null)
            return;
        if (viewPager == null)
            return;

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.company_tab_name_annei)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.company_tab_name_ykf)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.company_tab_name_dream)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_browser:
                launchBrowser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * カスタムタグでHPを表示
     */
    private void launchBrowser() {
        AnalyticsUtils.logSelectEvent(TAG, "list_browser");
        CustomTabUtil.start(this, getCompanyHomepageUrl());
    }

    @Override
    public void emptyClick() {
        createTab(getCurrentPosition());
        AnalyticsUtils.logSelectEvent(TAG, "empty");
    }

    public String getCompanyHomepageUrl() {
        switch (viewPager.getCurrentItem()) {
            case TAB_FIRST:
                return "http://www.aneikankou.co.jp/";
            case TAB_SECOND:
                return "http://www.yaeyama.co.jp/";
            case TAB_THREAD:
                return "http://ishigaki-dream.co.jp/";
            default:
                return null;
        }
    }
}
