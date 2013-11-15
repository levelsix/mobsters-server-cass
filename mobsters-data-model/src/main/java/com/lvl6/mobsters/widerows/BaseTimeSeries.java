package com.lvl6.mobsters.widerows;

import java.util.Date;

public abstract class BaseTimeSeries {
	abstract public String getName();
	abstract public Long getRoundedTimeMilliseconds();
	abstract public Long getRoundedTimeMilliseconds(Date date);
}
