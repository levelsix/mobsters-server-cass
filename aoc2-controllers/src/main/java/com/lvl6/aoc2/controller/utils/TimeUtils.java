package com.lvl6.aoc2.controller.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
  
  public static int NUM_MINUTES_LEEWAY_FOR_CLIENT_TIME = 5;
  
  public boolean isSynchronizedWithServerTime(Date maybeNow) {
    if (null == maybeNow) {
      return false;
    }
    DateTime possiblyNow = new DateTime(maybeNow); 
    DateTime now = new DateTime();
    Period interim = new Period(possiblyNow, now);
    
    int minutesApart = interim.getMinutes();
    if (minutesApart > NUM_MINUTES_LEEWAY_FOR_CLIENT_TIME) {
      //client time is unsynchronized with server time
      return false;
    } else {
      return true;
    }
  }
  
  public int numMinutesDifference(Date d1, Date d2) {
    DateTime dOne = new DateTime(d1);
    DateTime dTwo = new DateTime(d2);
    Period interim = new Period(dOne, dTwo);
    
    return interim.getMinutes();
  }
  
  //not sure if DateTime works, as well.
  public boolean isFirstEarlierThanSecond(Date one, Date two) {
	  LocalDate ldOne = new LocalDate(one);
	  LocalDate ldTwo = new LocalDate(two);
	  
	  return ldOne.isBefore(ldTwo);
  }
}