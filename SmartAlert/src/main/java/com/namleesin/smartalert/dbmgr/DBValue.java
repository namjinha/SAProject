package com.namleesin.smartalert.dbmgr;

public class DBValue 
{
	public static final int SUCCESS								= 0;
	public static final int FAILURE								= -1;

	public static final int CULUM_PACKAGE 		= 0;
	public static final int CULUM_TITLE 		= 1;
	public static final int CULUM_SUBTXT 		= 2;
	public static final int CULUM_LIKESTATUS 	= 6;

	
	public static final int TYPE_INSERT_NOTIINFO				= 0;
	public static final int TYPE_INSERT_KEYWORDFILTER			= 1;
	public static final int TYPE_INSERT_PACKAGEFILTER			= 2;
	
	public static final int TYPE_SELECT_DAY_COUNT				= 10;
	public static final int TYPE_SELECT_PRE_DAY_COUNT			= 11;
	public static final int TYPE_SELECT_TOTAL_COUNT				= 12;
	public static final int TYPE_SELECT_URL_COUNT				= 13;
	public static final int TYPE_SELECT_DISLIKE_COUNT			= 14;
	public static final int TYPE_SELECT_LIKE_COUNT				= 15;
	public static final int TYPE_SELECT_DISLIKE_PACKAGE_COUNT	= 16;
	public static final int TYPE_SELECT_LIKE_PACKAGE_COUNT		= 17;
	public static final int TYPE_SELECT_PACKAGE_INFO_COUNT		= 18;
	public static final int TYPE_SELECT_PACKAGE_INFO			= 20;
	public static final int TYPE_SELECT_PKGINFO_LIKESTAT		= 21;
		
	public static final String SQL_INSRT_NOTIDATA				= "INSERT INTO noti_info_table (noti_package, noti_titletxt, noti_subtxt, noti_id, noti_key, noti_time, noti_like_status, noti_dislike_status, noti_url_status) " +
																	"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_KEYWORDDATA			= "INSERT INTO keyword_filter_table (keyword, keyword_status) VALUES (?, ?)";
	public static final String SQL_INSERT_PACKDATA				= "INSERT INTO package_filter_table (package, pakcage_status) VALUES (?, ?)";
	
	public static final String SQL_SELECT_DAY_COUNT				= "SELECT count(*) FROM noti_info_table WHERE date(noti_time) = date(now())";
	public static final String SQL_SELECT_PRE_DAY_COUNT			= "SELECT count(*) FROM noti_info_table WHERE date(noti_time) >= date(subdate(now(), INTERVAL ? DAY) and date(noti_time) <= date(now())";
	public static final String SQL_SELECT_TOTAL_COUNT			= "SELECT count(*) FROM noti_info_table";
	public static final String SQL_SELECT_URL_COUNT				= "SELECT count(*) FROM noti_info_table WHERE noti_url_status=1";
	public static final String SQL_SELECT_DISLIKE_COUNT			= "SELECT count(*) FROM noti_info_table WHERE noti_dislike_status=1";
	public static final String SQL_SELECT_LIKE_COUNT			= "SELECT count(*) FROM noti_info_table WHERE noti_like_status=1";
	public static final String SQL_SELECT_DISLIKE_PACKAGE_COUNT = "SELECT count(*) FROM noti_info_table WHERE noti_package=? and noti_dislike_status=1";
	public static final String SQL_SELECT_PACKAGE_INFO_COUNT	= "SELECT count(*) FROM noti_info_table WHERE noti_package=?";
	public static final String SQL_SELECT_LIKE_PACKGAE_COUNT	= "SELECT count(*) FROM noti_info_table WHERE noti_package=? and noti_like_status = 1";
	public static final String SQL_SELECT_PACKAGE_INFO			= "SELECT * FROM noti_info_table WHERE noti_package=?";
	public static final String SQL_SELECT_PKGINFO_LIKESTAT		= "SELECT * FROM noti_info_table WHERE noti_like_status=?";

}
	