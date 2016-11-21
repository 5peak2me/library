package com.jinlin.base.core.views.banner;

public interface AdIndicator {

	void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

	void onPageSelected(int position);

	void onPageScrollStateChanged(int state);

	void notifyPageCountChanged(int current, int pageCount);

	void setColor(int color);

}
