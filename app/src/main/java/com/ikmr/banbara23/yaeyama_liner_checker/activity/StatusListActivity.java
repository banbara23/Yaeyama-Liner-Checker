
package com.ikmr.banbara23.yaeyama_liner_checker.activity;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ikmr.banbara23.yaeyama_liner_checker.R;
import com.ikmr.banbara23.yaeyama_liner_checker.StatusAsyncTaskLoader;
import com.ikmr.banbara23.yaeyama_liner_checker.StatusListAdapter;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Company;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Liner;
import com.ikmr.banbara23.yaeyama_liner_checker.entity.Result;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.ListFragmentInterface;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.QueryInterface;
import com.ikmr.banbara23.yaeyama_liner_checker.fragment.StatusListFragment;
import com.ikmr.banbara23.yaeyama_liner_checker.parser.AnneiListParser;
import com.ikmr.banbara23.yaeyama_liner_checker.parser.YkfParser;

import org.jsoup.nodes.Document;

import timber.log.Timber;

/**
 * ステータス一覧Activity
 */
public class StatusListActivity extends BaseActivity implements
        StatusListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Document>, QueryInterface {

    final static String PARAM_COMPANY = "company";
    // 観光会社
    private Company mCompany;
    /**
     * クエリ起動中かどうか
     */
    private boolean mQuerying;
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCompany = (Company) getIntent().getSerializableExtra(PARAM_COMPANY);
        setPageTitle();

        if (savedInstanceState == null) {
            mFragment = StatusListFragment.NewInstance(mCompany);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }
    }

    /**
     * タイトルの設定
     */
    private void setPageTitle() {
        if (mCompany == null) {
            return;
        }
        // String title = mCompany.getCompanyName() +
        // getString(R.string.title_activity_status_list);
        String title = mCompany.getCompanyName();
        setTitle(title);
    }

    @Override
    public void startQuery() {
        // 一覧の取得開始
        createList();
    }

    /**
     * 一覧の取得開始
     */
    private void createList() {
        if (mFragment != null && mFragment instanceof ListFragmentInterface) {
            ((ListFragmentInterface) mFragment).onStartQuery();
        }
        mQuerying = true;
        getLoaderManager().initLoader(0, null, this);
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
            case R.id.action_reload:
                if (!mQuerying) {
                    createList();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Document> onCreateLoader(int id, Bundle args) {
        String url;
        if (mCompany == Company.ANNEI) {
            url = getApplicationContext().getString(R.string.url_annei_list);
        } else {
            url = getApplicationContext().getString(R.string.url_ykf_list);
        }
        StatusAsyncTaskLoader appLoader = new StatusAsyncTaskLoader(getApplication(), url);

        // loaderの開始
        appLoader.forceLoad();
        //
        return appLoader;
    }

    @Override
    public void onLoadFinished(Loader<Document> loader, Document doc) {
        if (doc == null) {
            // エラーを通知
            if (mFragment != null && mFragment instanceof ListFragmentInterface) {
                ((ListFragmentInterface) mFragment).onFailedQuery();
            }
            return;
        }
        Result result = null;
        try {
            if (mCompany == Company.ANNEI) {
                // 安栄のHTMLパース呼び出し
                result = AnneiListParser.pars(doc);
            } else {
                // 八重山観光フェリーのHTMLパース呼び出し
                result = YkfParser.pars(doc);
            }
            // 結果を通知
            if (mFragment != null && mFragment instanceof ListFragmentInterface) {
                ((ListFragmentInterface) mFragment).onResultQuery(result);
            }
            // 終了
            if (mFragment != null && mFragment instanceof ListFragmentInterface) {
                ((ListFragmentInterface) mFragment).onFinishQuery();
            }
        } catch (Exception e) {
            Log.d("StatusListActivity", "e:" + e);
            Timber.d("エラー発生！！");
            Timber.d(e.getMessage());
            Timber.d(e.getLocalizedMessage());
            if (mFragment != null && mFragment instanceof ListFragmentInterface) {
                ((ListFragmentInterface) mFragment).onFailedQuery();
            }
        } finally {
            mQuerying = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Document> loader) {

    }

    /**
     * リストのセルビュークリック処理
     *
     * @param liner
     */
    @Override
    public void onItemClick(Liner liner) {
        liner.setCompany(mCompany);
        Intent intent = new Intent(this, StatusDetailActivity.class);
        intent.putExtra(StatusDetailActivity.class.getName(), liner);
        startActivity(intent);
    }

}
