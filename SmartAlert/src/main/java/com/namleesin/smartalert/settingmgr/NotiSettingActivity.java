package com.namleesin.smartalert.settingmgr;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.PullDownInputView;
import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

import java.util.ArrayList;
import java.util.List;

public class NotiSettingActivity extends Activity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;
    private static Activity mActivity;

    public static interface AppFilter
    {
        public void init();
        public boolean filterApp(ApplicationInfo info);
    }

    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter()
    {
        public void init() {
        }
        @Override
        public boolean filterApp(ApplicationInfo info) {
            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return true;
            } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spamnotisetting);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mActivity = NotiSettingActivity.this;
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

    public static class NotiSpamSetAppFragment extends Fragment {
        private final int MENU_DOWNLOAD = 0;
        private final int MENU_ALL = 1;
        private int MENU_MODE = MENU_DOWNLOAD;

        private ListView mListView = null;
        private ListViewAdapter mAdapter = null;

        public NotiSpamSetAppFragment() {
        }

        public static NotiSpamSetAppFragment newInstance() {
            NotiSpamSetAppFragment fragment = new NotiSpamSetAppFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_spamnotiset_app, container, false);

            mListView = (ListView) rootView.findViewById(R.id.listview);
            mAdapter = new ListViewAdapter(getActivity().getApplicationContext());
            mAdapter.setData(getApplicatonInfoList());
            mListView.setAdapter(mAdapter);

            Button spamsetappBtn = (Button) rootView.findViewById(R.id.notisetspamappbtn);
            spamsetappBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(1);
                }
            });

            Button apptabBtn = (Button) rootView.findViewById(R.id.tab01);
            apptabBtn.setPressed(true);

            Button keywordtabBtn = (Button) rootView.findViewById(R.id.tab02);
            keywordtabBtn.setPressed(false);
            keywordtabBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(1);
                }
            });

            return rootView;
        }

        private ArrayList<ListViewItem> getApplicatonInfoList()
        {
            ArrayList<ListViewItem> listAppInfoData = new ArrayList<ListViewItem>();
            PackageManager pm = getActivity().getPackageManager();
            List<ApplicationInfo> appList = pm
                    .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_DISABLED_COMPONENTS);

            AppFilter filter;
            switch (MENU_MODE)
            {
                case MENU_DOWNLOAD:
                    filter = THIRD_PARTY_FILTER;
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filter != null)
            {
                filter.init();
            }

            ListViewItem addInfo = null;
            ApplicationInfo info = null;
            for (ApplicationInfo app : appList)
            {
                info = app;
                if (filter == null || filter.filterApp(info))
                {
                    addInfo = new ListViewItem();
                    // App Icon
                    addInfo.mAppIcon = app.loadIcon(pm);
                    // App Name
                    addInfo.mAppName = app.loadLabel(pm).toString();

                    listAppInfoData.add(addInfo);
                }
            }

            return listAppInfoData;
        }
    }

    public static class NotiSpamSetKeywordFragment extends Fragment
    {
        private ListView mListView = null;
        private ListViewAdapter mAdapter = null;

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
            View rootView = inflater.inflate(R.layout.fragment_spamnotiset_keyword, container, false);

            mListView = (ListView) rootView.findViewById(R.id.listview);
            mAdapter = new ListViewAdapter(getActivity().getApplicationContext());
            mAdapter.setData(getKeywordData());
            mListView.setAdapter(mAdapter);

            Button spamsetkeywordBtn = (Button)rootView.findViewById(R.id.notisetspamkeywordbtn);
            spamsetkeywordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(2);
                }
            });

            PullDownInputView pullDownInputView = (PullDownInputView) rootView.findViewById(R.id.pulldownView);
            pullDownInputView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback() {
                @Override
                public void onPUlldownInpuViewCallback(String input) {
                    Log.d("NJ Lee", "input : "+input);
                    DbHandler handler = new DbHandler(mActivity);
                    KeywordData keywordData = new KeywordData().setKeywordata(input)
                            .setKeywordstatus(DBValue.STATUS_DISLIKE);
                    handler.insertDB(DBValue.TYPE_INSERT_KEYWORDFILTER, keywordData);
                    Toast.makeText(mActivity, input+" 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            Button apptabBtn = (Button) rootView.findViewById(R.id.tab01);
            apptabBtn.setPressed(false);
            apptabBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(0);
                }
            });

            Button keywordtabBtn = (Button) rootView.findViewById(R.id.tab02);
            keywordtabBtn.setPressed(true);

            return rootView;
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
            Button likesetkeywordBtn = (Button)rootView.findViewById(R.id.notisetlikekeywordbtn);
            likesetkeywordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });

            PullDownInputView pulldownView = (PullDownInputView) rootView.findViewById(R.id.pulldownView);
            pulldownView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback() {
                @Override
                public void onPUlldownInpuViewCallback(String input) {
                    Log.d("NJ Lee", "input : "+input);
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
