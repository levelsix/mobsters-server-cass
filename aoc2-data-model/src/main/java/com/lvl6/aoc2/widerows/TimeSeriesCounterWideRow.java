package com.lvl6.aoc2.widerows;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;

public class TimeSeriesCounterWideRow<Ky> extends CounterWideRow<Ky, Long> {

	
	
	private static final Logger log = LoggerFactory.getLogger(TimeSeriesCounterWideRow.class);
	
	public TimeSeriesCounterWideRow(Class<Ky> key) {
		super(key, Long.class);

	}

	

	public List<WideRowValue<Ky, Long, Long>> getRowTimeSlice(Ky key, Long startTime, Long endTime) throws ConnectionException{
		log.info("Getting row for key: {}", key);
		List<WideRowValue<Ky, Long, Long>> values = new ArrayList<WideRowValue<Ky, Long, Long>>();
		ColumnList<Long> result = getKeyspace().prepareQuery(getColumnFamily())
			    .getKey(key)
			    .withColumnRange(startTime, endTime, false, 0)
			    .execute()
			    .getResult();
		if (!result.isEmpty()) {
			Iterator<Column<Long>> it = result.iterator();
			while(it.hasNext()) {
				Column<Long> column = it.next();
				WideRowValue<Ky, Long, Long> wrv = new WideRowValue<Ky, Long, Long>()
					.setKey(key)
					.setColumn(column.getName())
					.setValue(column.getValue(getValueSerializer()));
				values.add(wrv);
			}
		}
		return values;
	}
}
