package com.namleesin.smartalert.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.ActionBarView;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;
import com.namleesin.smartalert.timeline.TimeLineActivity;
import com.namleesin.smartalert.utils.NotiAlertState;
import com.namleesin.smartalert.utils.PFMgr;
import com.namleesin.smartalert.utils.PFValue;



public class MainActivity extends FragmentActivity implements DrawerListener,
		LoaderCallbacks<ArrayList<NotiInfoData>>,
		AdapterView.OnItemClickListener
{
	private final String AD_UNIT_ID = "ca-app-pub-6738646161258413/2235663683";
	private InterstitialAd interstitialAd = null;

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

	@Override
	public void onBackPressed()
	{
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID);
		interstitialAd.loadAd(adRequest);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				}
			}

			@Override
			public void onAdOpened() {
				finish();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				finish();
			}
		});
	}

	private void initView()
	{
		MenuDrawerView menu_view = (MenuDrawerView) findViewById(R.id.menu_list);
		menu_view.setOnMenuItemClickListener(this);
		menu_view.setCloseClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mMenuDrawer.closeDrawers();
			}
		});
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

		mActionbar.setOnGraphButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenActivity.startGraphActivity(MainActivity.this);
			}
		});

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

		initDashboard();
		initPrivacyMode();
	}

	private void initDashboard()
	{
		int total_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
		int spam_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_DISLIKE_COUNT, null);
		int like_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_COUNT, null);

		TextView total_view = (TextView)findViewById(R.id.total_noti_txt);
		total_view.setText(total_cnt+"");

		TextView spam_view = (TextView) findViewById(R.id.spam_cnt_txt);
		spam_view.setText(spam_cnt+"");

		TextView like_view = (TextView) findViewById(R.id.fav_cnt_txt);
		like_view.setText(like_cnt+"");

		mMainDashboardView = findViewById(R.id.main_dashboard);
		mOverlay = (LinearLayout) findViewById(R.id.overlay);
		View more_btn = findViewById(R.id.more_btn);
		more_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRemainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
				View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
				float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
				if (mIsListExpanded == false) {
					float dpHeight = content.getHeight() - pixel;
					Animation ani = new GrowupAnimation(mOverlay, GrowupAnimation.MODE_GROW, mOverlayHeight, dpHeight);
					mOverlay.startAnimation(ani);
					mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_VIEW, "최근 한달동안 숨김 알림 앱 순위");
				} else {
					float dpHeight = content.getHeight() - pixel;
					Animation ani = new GrowupAnimation(mOverlay, GrowupAnimation.MODE_SHRINK, dpHeight, mOverlayHeight);
					mOverlay.startAnimation(ani);
					mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_MAIN, null);
				}

				mIsListExpanded = !mIsListExpanded;
			}
		});

		View btn_spam = mMainDashboardView.findViewById(R.id.btn_spam);
		btn_spam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_HATE);

				startActivity(i);
			}
		});

		View btn_like = mMainDashboardView.findViewById(R.id.btn_like);
		btn_like.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_FAVORITE);

				startActivity(i);
			}
		});

		mRemainLayout = findViewById(R.id.remain_area);
		mRemainLayout.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
	}

	private void initPrivacyMode()
	{
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		int mode = pref.getInt(PrivacyMode.PREF_PRIVACY_MODE, -1);
		final PrivacyMode privacyMode = new PrivacyMode();

		ImageView privacy = (ImageView) findViewById(R.id.privacy_mode_img);
		if(mode == PrivacyMode.PRIVACY_MODE_ON)
		{
			privacy.setImageResource(R.drawable.privacy_on_xxhdpi);
		}
		else if(mode == PrivacyMode.PRIVACY_MODE_OFF)
		{
			privacy.setImageResource(R.drawable.privacy_off_xxhdpi);
		}
		else
		{
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_OFF);
			editor.commit();
			privacyMode.changePrivacyMode(MainActivity.this);
		}

		findViewById(R.id.privacy_mode_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
				int mode = pref.getInt(PrivacyMode.PREF_PRIVACY_MODE, -1);

				SharedPreferences.Editor editor = pref.edit();
				if (mode == PrivacyMode.PRIVACY_MODE_ON) {
					privacyMode.changePrivacyMode(MainActivity.this);
				} else if (mode == PrivacyMode.PRIVACY_MODE_OFF) {
					privacyMode.changePrivacyMode(MainActivity.this);
				}
			}
		});
	}

	@Override
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

		if(resultCode == Activity.RESULT_CANCELED)
		{
			finish();
			return;
		}

		PFMgr pmgr = new PFMgr(this);
		switch(requestCode)
		{
			case MainValue.RES_SPLASH_SCREEN:
				checkStartState();
				break;
			case MainValue.RES_GUIDE_WIZARD:
				pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_GUIDE_OK);
				if(true == NotiAlertState.isNLServiceRunning(this))
				{
					pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_ALERT_OK);
					OpenActivity.startSpamSettingActivity(this);
				}
				else
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				break;
			case MainValue.RES_ALERT_SETTING:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_ALERT_OK);
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case MainValue.RES_SL_SETTING:
				pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_SETTING_OK);
				break;
			default:
				break;
		}
	}

	private void checkStartState()
	{
		int initstate = new PFMgr(this).getIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_DEFAULT);
		switch(initstate)
		{
			case PFValue.PRE_INIT_DEFAULT:
				OpenActivity.startGuideMgrActivity(this);
				break;
			case PFValue.PRE_INIT_GUIDE_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case PFValue.PRE_INIT_ALERT_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case PFValue.PRE_INIT_SETTING_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		switch(position)
		{
			case MainValue.MENU_ITEM_SPAM_ALERT:
				OpenActivity.openSpamSettingActivity(this, 0);
				break;
			case MainValue.MENU_ITEM_LIKE_ALERT:
				OpenActivity.openSpamSettingActivity(this, 2);
				break;
			default:
				break;
		}

		mMenuDrawer.closeDrawers();
	}
}
