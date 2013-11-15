package com.lvl6.mobsters.widerows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class CounterWideRow<Ky, Col> extends BaseWideRow<Ky, Col, Long> {
	
	
	private static final Logger log = LoggerFactory.getLogger(CounterWideRow.class);

	public CounterWideRow(Class<Ky> key, Class<Col> column) {
		super(key, column, Long.class);
	}

	
	@Override
	public void saveValue(WideRowValue<Ky, Col, Long> wrv) throws ConnectionException {
		getKeyspace().prepareColumnMutation(getColumnFamily(), wrv.getKey(), wrv.getColumn())
		.incrementCounterColumn(wrv.getValue())
	    .execute();
	}

	
	@Override
	protected void putColumn(ColumnListMutation<Col> clm,	WideRowValue<Ky, Col, Long> value) {
		clm.incrementCounterColumn(value.getColumn(), value.getValue());
	}
	
}
