package com.namleesin.smartalert.timeline;


public class TimelineData
{
	public String pkgName;
	public String appName;
	public String content;
	public int likeStatus;

	public String getPkgName() {
		return pkgName;
	}
	
	public TimelineData setPkgName(String pkgName) {
		this.pkgName = pkgName;
		return this;
	}
	
	public String getAppName() {
		return appName;
	}

	public TimelineData setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	public int getLikeStatus() {
		return likeStatus;
	}
	public TimelineData setLikeStatus(int likeCnt) {
		this.likeStatus = likeCnt;
		return this;
	}
	public String getContent() {
		return content;
	}

	public TimelineData setContent(String content) {
		this.content = content;
		return this;
	}

}
