package com.lvl6.mobsters.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryConstructionUtil {

	private static final Logger log = LoggerFactory.getLogger(QueryConstructionUtil.class);
	private final String and = " AND ";
	private final String notNull = " NOT NULL ";
	private final String nullStr = " NULL ";
	private final String or = " OR ";
	private final String comma = ",";
	private final String equality = "=";
	private final String greaterThan = ">";
	private final String in = "IN"; 
	private final String is = "IS"; 
	private final String question = "?";
	private final String space = " ";
	private final int spaceLength = 1;


	//at the moment, just equality conditions and greater than conditions and 
	//"in ()" conditions, the argument "values" is another return value. It will contain
	//the values to be set into the CqlPreparedStatement in the proper order
	public String selectRowsQueryAllConditions(String tableName, Map<String, ?> equalityConditions,
			String equalityCondDelim, Map<String, ?> greaterThanConditions,
			String greaterThanCondDelim, Map<String, Collection<?>> inConditions,
			String inCondDelim, Map<String, ?> isConditions, String isCondDelim,
			String delimAcrossConditions, List<Object> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tableName);

		boolean emptyEqConditions = (null == equalityConditions || equalityConditions.isEmpty()); 
		boolean emptyGtConditions = (null == greaterThanConditions || greaterThanConditions.isEmpty());
		boolean emptyInConditions = (null == inConditions || inConditions.isEmpty());
		boolean emptyIsConditions = (null == isConditions || isConditions.isEmpty());

		//(paranoia) if caller doesn't provide anything, select whole table
		if (emptyEqConditions && emptyGtConditions && emptyInConditions) {
			sb.append(";");
			String query = sb.toString();
			log.info("no args provided. query=" + query);
			return query;
		}
		sb.append(" where ");

		//EQUALITY CONDITIONS
		String conjunction = "";
		if (!emptyEqConditions) {
			String equalityConditionsStr = createComparisonConditionsString(
					equalityConditions, values, equality, equalityCondDelim);
			sb.append(equalityConditionsStr);

			conjunction = delimAcrossConditions;
		}
		//GREATER THAN CONDITIONS
		if (!emptyGtConditions) {
			String gtConditionsStr = createComparisonConditionsString(greaterThanConditions,
					values, greaterThan, greaterThanCondDelim);
			sb.append(conjunction);
			sb.append(gtConditionsStr);

			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}
		// IN (VALUES) CONDITIONS
		if (!emptyInConditions) {
			for (String column : inConditions.keySet()) {
				Collection<?> inValues = inConditions.get(column);
				String inConditionsStr = createColInValuesString(column, inValues);

				sb.append(inCondDelim);
				sb.append(inConditionsStr);

			}
			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}
		// IS CONDITIONS
		if (!emptyIsConditions) {
			String strConditionStr = createIsConditionString(isConditions, values, isCondDelim);
			sb.append(conjunction);
			sb.append(strConditionStr);
			
			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}

		//CLOSE THE QUERY
		sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}


	//generalized method to construct a query, the argument "values" is another return
	//value. It will contain the values to be set into the CqlPreparedStatement in the
	//proper order
	public String selectRowsQueryEqualityConditions(String tableName,
			Map<String, ?> equalityConditions, String conditionDelimiter,
			List<Object> values, boolean preparedStatement) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tableName);

		if (null == equalityConditions || equalityConditions.isEmpty()) {
			sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(" where ");

		if (preparedStatement) {
			String preparedEqualityConditionsStr = createPreparedComparisonConditionsString(
					equalityConditions, values, equality, conditionDelimiter);
			sb.append(preparedEqualityConditionsStr);
		} else {
			String equalityConditionsStr = createComparisonConditionsString(
					equalityConditions, values, equality, conditionDelimiter);
			sb.append(equalityConditionsStr);
		}

		//close the query
		sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//look at createComparisonConditionsString() for details. This is just a copy of it.
	public String createPreparedComparisonConditionsString(Map<String, ?> equalityConditions,
			List<Object> values, String comparator, String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : equalityConditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = equalityConditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(question);

			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String equalityConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr + "\t values=" + values);
		return equalityConditionsStr;
	}

	//the argument "values" is another return value. It will contain the values
	//to be set into the CqlPreparedStatement in the proper order. string
	//will not be a prepared statement unless specified by boolean "preparedStatement"
	public String createComparisonConditionsString(Map<String, ?> equalityConditions,
			List<Object> values, String comparator, String conditionDelimiter) {

		List<Object> clauses = new ArrayList<Object>();

		//stringify the equality conditions. 
		//e.g. Map(col1=>x, col2=>y,...,colN=>something) 
		//col1 => x, now becomes String(col1) + String(=) + String(x)
		for (String key : equalityConditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = equalityConditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(obj);

			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}

		//We made Map(col1=>x, col2=>y,...,colN=>something) into 
		//List(String(col1=x), String(col2=y),..., String(colN=Something))
		//implode (join together) the equality conditions with "AND"

		//e.g. List becomes String(String(col1=x) + AND +...+String(colN=something))
		String equalityConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr + "\t values=" + values);
		return equalityConditionsStr;
	}


	public String createColInValuesString(String column, Collection<?> values) {
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
	
	//the argument "values" is another return value. It will contain the values
	//to be set into the CqlPreparedStatement in the proper order. string
	//will not be a prepared statement unless specified by boolean "preparedStatement"
	public String createIsConditionString(Map<String, ?> isConditions, List<Object> values,
			String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : isConditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = isConditions.get(key);

			clauseSb.append(key);
			clauseSb.append(space);
			clauseSb.append(is);
			clauseSb.append(space);
			clauseSb.append(obj);

			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}


		String isConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + isConditionsStr + "\t values=" + values);
		return isConditionsStr;
	}


	public String implode(Collection<?> thingsToImplode, String delimiter) {
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


	public String getAnd() {
		return and;
	}

	public String getNotNull() {
		return notNull;
	}

	public String getNullStr() {
		return nullStr;
	}

	public String getOr() {
		return or;
	}
	
}
