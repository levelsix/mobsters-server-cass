package com.lvl6.mobsters.widerows;

import java.util.Date;

public class AllTimeSeries extends BaseTimeSeries {

	@Override
	public String getName() {
		return "all_time";
	}

	@Override
	public Long getRoundedTimeMilliseconds() {
		return 0l;
	}

	@Override
	public Long getRoundedTimeMilliseconds(Date date) {
		return 0l;
	}

}
