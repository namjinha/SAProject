package com.namleesin.smartalert.settingmgr;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.PullDownInputView;
import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.PackData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.main.MainValue;
import com.namleesin.smartalert.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class NotiSettingActivity extends Activity
{
    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    private static ViewPager mViewPager = null;
    private static Activity mActivity = null;
    private int mNextIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spamnotisetting);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mActivity = NotiSettingActivity.this;

        Intent i = getIntent();
        if(null != i)
        {
            int index = i.getIntExtra(MainValue.SET_INDEX_NUMBER, 0);
            mViewPager.setCurrentItem(index);
        }

        Button gonebtn = (Button)findViewById(R.id.nextbtn);
        gonebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mNextIdx = mViewPager.getCurrentItem();

                mNextIdx++;

                if(mNextIdx > 2)
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                    return;
                }
                mViewPager.setCurrentItem(mNextIdx);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_spam_noti_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class NotiSpamSetAppFragment extends Fragment implements AdapterView.OnItemClickListener
    {
        private ListView mListView = null;
        private ListViewAdapter mAdapter = null;
        private View mRootView = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            DbHandler handler = new DbHandler(getActivity().getApplicationContext());

            PackData packdata = new PackData();
            packdata.packagename = ((ListViewItem) mAdapter.getItem(position)).mPackageName;

            CheckBox checkstate = (CheckBox) view.findViewById(R.id.checkstate);
            if (true == checkstate.isChecked())
            {
                Log.d("namjinha", "ret = " + checkstate.isChecked());
                Log.d("namjinha", "packdata.packagename = " + packdata.packagename);
                checkstate.setChecked(false);
                handler.deleteDB(DBValue.TYPE_DELETE_FILTER_APP, packdata);
            }
            else
            {
                checkstate.setChecked(true);
                handler.insertDB(DBValue.TYPE_INSERT_PACKAGEFILTER, packdata);
            }
        }

        private class AppListAsyncTask extends AsyncTask<Void, Integer, ArrayList<ListViewItem>>
        {
            private int mMaxCount = 0;
            private ProgressBar mProgressBar = null;
            private List<ApplicationInfo> mAppList = null;
            private PackageManager mPm = null;

            @Override
            protected void onPreExecute()
            {
                mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressbar);

                mPm = getActivity().getPackageManager();
                mAppList = mPm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_DISABLED_COMPONENTS);

                mProgressBar.setMax(mAppList.size());
            }

            @Override
            protected ArrayList<ListViewItem> doInBackground(Void... params)
            {
                AppInfo.AppFilter filter;
                switch (AppInfo.MENU_MODE)
                {
                    case AppInfo.MENU_DOWNLOAD:
                        filter = AppInfo.THIRD_PARTY_FILTER;
                        break;
                    default:
                        filter = null;
                        break;
                }

                if (filter != null)
                {
                    filter.init();
                }

                int i = 0;
                ApplicationInfo info = null;
                ArrayList<ListViewItem> listAppInfoData = new ArrayList<ListViewItem>();

                ArrayList<String> packagelist = new ArrayList<String>();
                DbHandler handler = new DbHandler(getActivity().getApplicationContext());
                Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
                if(null != cursor && cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    do
                    {
                        String packagename = cursor.getString(0);
                        packagelist.add(packagename);
                    }
                    while (cursor.moveToNext());
                }

                for (ApplicationInfo app : mAppList)
                {
                    info = app;
                    if (filter == null || filter.filterApp(info))
                    {
                        ListViewItem addInfo = new ListViewItem();
                        addInfo.mPackageName = app.packageName;
                        addInfo.mAppIcon = app.loadIcon(mPm);
                        addInfo.mAppName = app.loadLabel(mPm).toString();

                        if(true == packagelist.contains(addInfo.mPackageName))
                        {
                            addInfo.mFilterState = 1;
                        }

                        listAppInfoData.add(addInfo);
                    }
                    i++;
                    this.publishProgress(i);
                }

                return listAppInfoData;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                mProgressBar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<ListViewItem> aListArray)
            {
                mAdapter = new ListViewAdapter(getActivity().getApplicationContext());
                mAdapter.setData(aListArray);
                mListView.setAdapter(mAdapter);

                mProgressBar.setVisibility(View.GONE);
            }
        }

        public NotiSpamSetAppFragment() {
        }

        public static NotiSpamSetAppFragment newInstance() {
            NotiSpamSetAppFragment fragment = new NotiSpamSetAppFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mRootView = inflater.inflate(R.layout.fragment_spamnotiset_app, container, false);

            mListView = (ListView)mRootView.findViewById(R.id.listview);
            ProgressBar progressBarempty = (ProgressBar)mRootView.findViewById(R.id.emptyprogress);
            mListView.setEmptyView(progressBarempty);
            mListView.setOnItemClickListener(this);

            AppListAsyncTask appListAsyncTask = new AppListAsyncTask();
            appListAsyncTask.execute();

            Button apptabBtn = (Button) mRootView.findViewById(R.id.tab01);
            apptabBtn.setPressed(true);

            Button keywordtabBtn = (Button) mRootView.findViewById(R.id.tab02);
            keywordtabBtn.setPressed(false);
            keywordtabBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(1);
                }
            });

            return mRootView;
        }
    }

    public static class NotiSpamSetKeywordFragment extends Fragment
    {
        private ListView mListView = null;
        private ListViewAdapter mAdapter = null;
        private View mRootView;

        public NotiSpamSetKeywordFragment()
        {
        }

        public static NotiSpamSetKeywordFragment newInstance()
        {
            NotiSpamSetKeywordFragment fragment = new NotiSpamSetKeywordFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_spamnotiset_keyword, container, false);

            mListView = (ListView) mRootView.findViewById(R.id.listview);
            mAdapter = new ListViewAdapter(getActivity().getApplicationContext());
            mAdapter.setData(getKeywordData());
            mListView.setAdapter(mAdapter);

            PullDownInputView pullDownInputView = (PullDownInputView) mRootView.findViewById(R.id.pulldownView);
            pullDownInputView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback() {
                @Override
                public void onPUlldownInpuViewCallback(String input) {
                    Log.d("NJ Lee", "input : " + input);
                    DbHandler handler = new DbHandler(mActivity);
                    KeywordData keywordData = new KeywordData().setKeywordata(input)
                            .setKeywordstatus(DBValue.STATUS_DISLIKE);
                    handler.insertDB(DBValue.TYPE_INSERT_KEYWORDFILTER, keywordData);
                    Toast.makeText(mActivity, input + " 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            Button apptabBtn = (Button) mRootView.findViewById(R.id.tab01);
            apptabBtn.setPressed(false);
            apptabBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(0);
                }
            });

            Button keywordtabBtn = (Button) mRootView.findViewById(R.id.tab02);
            keywordtabBtn.setPressed(true);

            return mRootView;
        }

        @Override
        public void onResume()
        {
            super.onResume();
            PullDownInputView pullDownInputView = (PullDownInputView) mRootView.findViewById(R.id.pulldownView);
            Rect delegateArea = new Rect();
            View container = pullDownInputView.findViewById(R.id.handle);
            container.getHitRect(delegateArea);

            TouchDelegate delegate = new TouchDelegate(delegateArea, pullDownInputView);
            pullDownInputView.setTouchDelegate(delegate);
        }

        private ArrayList<ListViewItem> getKeywordData()
        {
            ArrayList<ListViewItem> listKeywordData = new ArrayList<ListViewItem>();

            ListViewItem item = new ListViewItem();
            item.mAppName = "대출";

            listKeywordData.add(item);

            return listKeywordData;
        }
    }

    public static class NotiLikeSetKeywordFragment extends Fragment
    {
        public NotiLikeSetKeywordFragment()
        {
        }

        public static NotiLikeSetKeywordFragment newInstance()
        {
            NotiLikeSetKeywordFragment fragment = new NotiLikeSetKeywordFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {

            View rootView = inflater.inflate(R.layout.fragment_likenotiset_keyword, container, false);

            PullDownInputView pulldownView = (PullDownInputView) rootView.findViewById(R.id.pulldownView);
            pulldownView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback() {
                @Override
                public void onPUlldownInpuViewCallback(String input) {
                    DbHandler handler = new DbHandler(mActivity);
                    KeywordData keywordData = new KeywordData().setKeywordata(input)
                            .setKeywordstatus(DBValue.STATUS_LIKE);
                    handler.insertDB(DBValue.TYPE_INSERT_KEYWORDFILTER, keywordData);
                    Toast.makeText(mActivity, input+" 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            Rect delegateArea = new Rect();
            rootView.getHitRect(delegateArea);
            TouchDelegate delegate = new TouchDelegate(delegateArea, pulldownView);
            rootView.setTouchDelegate(delegate);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0: return NotiSpamSetAppFragment.newInstance();
                case 1: return NotiSpamSetKeywordFragment.newInstance();
                case 2: return NotiLikeSetKeywordFragment.newInstance();
                default: return NotiSpamSetAppFragment.newInstance();
            }
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
