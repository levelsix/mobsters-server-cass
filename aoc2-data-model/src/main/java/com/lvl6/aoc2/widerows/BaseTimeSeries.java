package com.lvl6.aoc2.widerows;

import java.util.Date;

public abstract class BaseTimeSeries {
	abstract public String getName();
	abstract public Long getRoundedTimeMilliseconds();
	abstract public Long getRoundedTimeMilliseconds(Date date);
}
