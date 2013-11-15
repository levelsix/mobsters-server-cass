package com.lvl6.aoc2.widerows;

import java.util.Date;

import org.joda.time.DateTime;

public class DayTimeSeries extends BaseTimeSeries {

	@Override
	public String getName() {
		return "day";
	}

	@Override
	public Long getRoundedTimeMilliseconds() {
		DateTime time = new DateTime();
		time.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0);
		return time.getMillis();
	}

	@Override
	public Long getRoundedTimeMilliseconds(Date date) {
		DateTime time = new DateTime(date);
		time.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0);
		return time.getMillis();
	}

}
