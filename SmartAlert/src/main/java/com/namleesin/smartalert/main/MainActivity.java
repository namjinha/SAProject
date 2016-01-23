package com.namleesin.smartalert.main;

import java.util.ArrayList;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.ActionBarView;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.timeline.TimeLineActivity;


public class MainActivity extends FragmentActivity implements DrawerListener, 
													LoaderCallbacks<ArrayList<NotiInfoData>>,
													AdapterView.OnItemClickListener
{
	private DbHandler mDBHandler;
	private NotiDataListAdapter mAdapter;
	private View mMainDashboardView;
	private DrawerLayout mMenuDrawer;
	private LinearLayout mOverlay;
	private int mOverlayHeight = 0;
	private View mRemainLayout;
	private boolean mIsListExpanded = false;
	private ActionBarView mActionbar;
	private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			mOverlayHeight = mRemainLayout.getHeight();
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mOverlayHeight);
			params.gravity = Gravity.BOTTOM;
			mOverlay.setLayoutParams(params);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		OpenActivity.startSplashScreenActivity(this);
		mDBHandler = new DbHandler(this);
		initView();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getSupportLoaderManager().initLoader(0, null, this).forceLoad();
	}
	
	private void initView()
	{
		ListView menu = (ListView) findViewById(R.id.menu_list);

		String[] menu_array = getResources().getStringArray(R.array.menu_str);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu_array);
		menu.setAdapter(adapter);
		menu.setOnItemClickListener(this);

		mMenuDrawer = (DrawerLayout) findViewById(R.id.menu_drawer);
		mMenuDrawer.setDrawerListener(this);

		mActionbar = (ActionBarView) findViewById(R.id.actionbar);
		mActionbar.setOnMenuButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMenuDrawer.isDrawerOpen(GravityCompat.START)) {
					mMenuDrawer.closeDrawers();
				} else {
					mMenuDrawer.openDrawer(GravityCompat.START);
				}
			}
		});

		int total_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
		int spam_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_DISLIKE_COUNT, null);
		int like_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_COUNT, null);

		TextView total_view = (TextView)findViewById(R.id.total_noti_txt);
		total_view.setText(total_cnt+"");
		
		TextView spam_view = (TextView) findViewById(R.id.spam_cnt_txt);
		spam_view.setText(spam_cnt+"");
		
		TextView like_view = (TextView) findViewById(R.id.fav_cnt_txt);
		like_view.setText(like_cnt+"");

		mAdapter = new NotiDataListAdapter(this);
		ListView list = (ListView)findViewById(R.id.noti_list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NotiInfoData data = (NotiInfoData) view.getTag();

				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_PACKAGE);
				i.putExtra(TimeLineActivity.TIMELINE_PKG, data.getPkgName());

				startActivity(i);
			}
		});

		mMainDashboardView = findViewById(R.id.main_dashboard);
		mOverlay = (LinearLayout) findViewById(R.id.overlay);
		View more_btn = findViewById(R.id.more_btn);
		more_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRemainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
				View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
				float pixel  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
				if(mIsListExpanded == false)
				{
					float dpHeight = content.getHeight() - pixel;
					Animation ani = new GrowupAnimation(mOverlay,GrowupAnimation.MODE_GROW, mOverlayHeight, dpHeight);
					mOverlay.startAnimation(ani);
					mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_VIEW, "최근 한달동안 숨김 알림 앱 순위");
				}
				else
				{
					float dpHeight = content.getHeight() - pixel;
					Animation ani = new GrowupAnimation(mOverlay,GrowupAnimation.MODE_SHRINK, dpHeight, mOverlayHeight);
					mOverlay.startAnimation(ani);
					mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_MAIN, null);
				}

				mIsListExpanded = !mIsListExpanded;
			}
		});

		mRemainLayout = findViewById(R.id.remain_area);
		mRemainLayout.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
	}

	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public Loader<ArrayList<NotiInfoData>> onCreateLoader(int id, Bundle bundle)
	{
		return new NotiDataLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<NotiInfoData>> loader,
			ArrayList<NotiInfoData> data) 
	{
		if(data != null) {
			mAdapter.setData(data);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<NotiInfoData>> loader) {
		mAdapter.setData(null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode)
		{
			case MainValue.RES_SPLASH_SCREEN:
				OpenActivity.startGuideMgrActivity(this);
				break;
			case MainValue.RES_GUIDE_WIZARD:
				OpenActivity.startAlertSettingActivity(this);
				break;
			case MainValue.RES_ALERT_SETTING:
				OpenActivity.startSpamSettingActivity(this);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDrawerClosed(View arg0) {
	}

	@Override
	public void onDrawerOpened(View arg0) {
		
	}

	@Override
	public void onDrawerSlide(View arg0, float arg1) {
		
	}

	@Override
	public void onDrawerStateChanged(int arg0) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}
}
