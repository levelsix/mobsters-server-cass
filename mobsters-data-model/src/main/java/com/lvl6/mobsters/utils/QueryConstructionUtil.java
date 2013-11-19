package com.lvl6.mobsters.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryConstructionUtil {
	
	private static final Logger log = LoggerFactory.getLogger(QueryConstructionUtil.class);
	private static final String and = "AND";
	private static final String comma = ",";
	private static final String in = "IN"; 
	private static final String space = " ";
	private static final int spaceLength = 1;
	
	//generalized method to construct a query, keys is necessary to instill an ordering
	public String selectRowsQuery(String tableName, Map<String, ?> equalityConditions,
			List<String> keys) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tableName);
		
		if (null == equalityConditions || equalityConditions.isEmpty()) {
			sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(" where ");
		
		List<Object> clauses = new ArrayList<Object>();
		//stringify the equality conditions. 
		//e.g. Map(col1=>x, col2=>y,...,colN=>something) 
		//col1 => x, now becomes String(col1) + String(=) + String(x)
		for (String key : keys) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = equalityConditions.get(key);
			
			clauseSb.append(key);
			clauseSb.append("=");
			clauseSb.append(obj);
			
			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		
		//We made Map(col1=>x, col2=>y,...,colN=>something) into 
		//List(String(col1=x), String(col2=y),..., String(colN=Something))
		//implode (join together) the equality conditions with "AND"
		
		//e.g. List becomes String(String(col1=x) + AND +...+String(colN=something))
		String allConditions = implode(clauses, and);
		sb.append(allConditions);
		
		//close the query
		sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString());
		return sb.toString();
	}
	
	public String createColInValuesString(String column, List<?> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(space);
		sb.append(in);
		sb.append(space);
		
		String valuesStr = implode(values, comma);
		sb.append(valuesStr);
		
		String result = sb.toString();
		return result;
	}
	
	
	public String implode(List<?> thingsToImplode, String delimiter) {
		if (null == thingsToImplode || thingsToImplode.isEmpty()) {
			log.error("invalid parameters passed into StringUtils getListInString. clauses=" +
					thingsToImplode + ", delimiter=" + delimiter);
		      return "";
		}
		StringBuilder strBuilder = new StringBuilder();
		
		for (Object thing : thingsToImplode) {
			strBuilder.append(thing);
			strBuilder.append(space);
			strBuilder.append(delimiter);
			strBuilder.append(space);
		}
		
		int length = strBuilder.length();
		int delimLength = delimiter.length(); 
		return strBuilder.substring(0, length - delimLength - spaceLength);
	}
	
	public List<Integer> explodeIntoInts(String stringToExplode, 
			String delimiter) {
		List<Integer> returnValue = new ArrayList<Integer>();
		StringTokenizer st = new StringTokenizer(stringToExplode, delimiter);
		while (st.hasMoreTokens()) {
			returnValue.add(Integer.parseInt(st.nextToken()));
		}
		return returnValue;
	}
	
}
