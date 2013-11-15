package com.lvl6.mobsters.widerows;

import java.util.ArrayList;
import java.util.Collection;

public class TimeSeriesUtil {
	
	public static Collection<BaseTimeSeries> defaultTimeSeries; 
	
	static{
		defaultTimeSeries = new ArrayList<BaseTimeSeries>();
		defaultTimeSeries.add(new HourTimeSeries());
		defaultTimeSeries.add(new DayTimeSeries());
		defaultTimeSeries.add(new WeekTimeSeries());
		defaultTimeSeries.add(new AllTimeSeries());
	}
	
	
}
