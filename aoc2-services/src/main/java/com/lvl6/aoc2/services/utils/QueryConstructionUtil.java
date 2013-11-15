package com.lvl6.aoc2.services.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryConstructionUtil {
	
	//generalized method to construct a query
	public String selectRowsQuery(String tableName,
			Map<String, Object> conditionParams) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ");
		sb.append(tableName);
		
		if (null == conditionParams || conditionParams.isEmpty()) {
			sb.append(";");
			return sb.toString();
		}
		sb.append(" where ");
		
		Set<String> keys = conditionParams.keySet();
		List<String> keyList = new ArrayList<String>(keys);
		
		//base case, after this, can generalize a loop for the rest
		//of the conditions
		genWhereClauseFirstCondition(keyList, conditionParams, sb);
		
		//generalized loop for the rest of the conditions
		genWhereClauseOtherConditions(keyList, conditionParams, sb);
		
		//close the query
		sb.append(";");
		return sb.toString();
	}
	
	//method that just creates the first condition in the where clause
	private void genWhereClauseFirstCondition(List<String> keyList,
			Map<String, Object> conditionParams, StringBuffer sb) {
		String keyOne = keyList.get(0);
		Object objOne = conditionParams.get(keyOne);
		sb.append(keyOne);
		sb.append("=");
		sb.append(objOne);
	}
	
	//assumes the StringBuffer ends with the one condition from
	//conditionParams
	private void genWhereClauseOtherConditions(List<String> keyList,
			Map<String, Object> conditionParams, StringBuffer sb) {
		for (int i = 1; i < keyList.size(); i++) {
			sb.append(" and ");
			String keyI = keyList.get(i); 
			Object objI = conditionParams.get(keyI);

			sb.append(keyI);
			sb.append("=");
			sb.append(objI);
		}
	}
}