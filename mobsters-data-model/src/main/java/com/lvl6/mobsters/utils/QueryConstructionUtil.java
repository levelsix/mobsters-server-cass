package com.lvl6.mobsters.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryConstructionUtil {

	private static final Logger log = LoggerFactory.getLogger(QueryConstructionUtil.class);
	private final String COMMA = ",";
	private final String EQUALITY = "=";
	private final String GREATERTHAN = ">";
	private final String PERCENT = "%";
	
	private final String AND = " AND ";
	private final String NOTNULL = " NOT NULL ";
	private final String NULLSTR = " NULL ";
	private final String OR = " OR ";
	private final String IN = "IN"; 
	private final String IS = "IS"; 
	private final String LIKE = "LIKE";
	private final String QUESTION = "?";
	private final String SPACE = " ";
	private final int SPACELENGTH = 1;
	private final String ALLOWFILTERING = "allow filtering";

	//at the moment, just EQUALITY conditions, GREATER THAN conditions,  "IN ()" and IS
	//conditions. the argument "values" is another return value. It will contain
	//the values to be set into the CqlPreparedStatement IN the proper order, but is not
	//used at the moment
	public String selectRowsQueryAllConditions(String tableName, Map<String, ?> equalityConditions,
			String equalityCondDelim, Map<String, ?> greaterThanConditions,
			String greaterThanCondDelim, Map<String, Collection<?>> inConditions,
			String inCondDelim, Map<String, ?> isConditions, String isCondDelim,
			String delimAcrossConditions, List<Object> values, boolean allowFiltering) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tableName);

		boolean emptyEqConditions = (null == equalityConditions || equalityConditions.isEmpty()); 
		boolean emptyGtConditions = (null == greaterThanConditions || greaterThanConditions.isEmpty());
		boolean emptyInConditions = (null == inConditions || inConditions.isEmpty());
		boolean emptyIsConditions = (null == isConditions || isConditions.isEmpty());

		//(paranoia) if caller doesn't provide anything, select whole table
		if (emptyEqConditions && emptyGtConditions && emptyInConditions && emptyIsConditions) {
			sb.append(";");
			String query = sb.toString();
			log.info("no args provided. query=" + query);
			return query;
		}
		sb.append(" where ");

		//EQUALITY CONDITIONS
		String conjunction = "";
		if (!emptyEqConditions) {
			String eqConditionsStr = createComparisonConditionsString(equalityConditions,
					EQUALITY, equalityCondDelim);
			sb.append(eqConditionsStr);

			conjunction = delimAcrossConditions;
		}
		//GREATER THAN CONDITIONS
		if (!emptyGtConditions) {
			String gtConditionsStr = createComparisonConditionsString(greaterThanConditions,
					GREATERTHAN, greaterThanCondDelim);
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
			String strConditionStr = createIsConditionString(isConditions, isCondDelim);
			sb.append(conjunction);
			sb.append(strConditionStr);
			
			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}

		//this is to tell cassandra even though searching by non row keys, it's alright
		if (allowFiltering) {
			sb.append(SPACE);
			sb.append(ALLOWFILTERING);
		}
		//CLOSE THE QUERY
		sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//generalized method to construct a query, the argument "values" IS another return
	//value. It will contain the values to be set into the CqlPreparedStatement IN the
	//proper order
	public String selectRowsQueryEqualityConditions(String tableName,
			Map<String, ?> equalityConditions, String conditionDelimiter,
			List<Object> values, boolean preparedStatement, boolean allowFiltering) {
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
					equalityConditions, values, EQUALITY, conditionDelimiter);
			sb.append(preparedEqualityConditionsStr);
		} else {
			String equalityConditionsStr = createComparisonConditionsString(
					equalityConditions, EQUALITY, conditionDelimiter);
			sb.append(equalityConditionsStr);
		}

		//this is to tell cassandra even though searching by non row keys, it's alright
		if (allowFiltering) {
			sb.append(SPACE);
			sb.append(ALLOWFILTERING);
		}
		//close the query
		sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//generalized method to construct a query, the argument "values" is another return
	//value. It will contain the values to be set into the CqlPreparedStatement in the
	//proper order, but not used at the moment
	public String selectRowsQueryLikeConditions(String tableName, Map<String, ?> beginsWith,
			String beginsWithCondDelim, Map<String, ?> beginsAndEndsWith,
			String beginsAndEndsWithCondDelim, Map<String, ?> endsWith,
			String endsWithCondDelim, String overallDelimiter, List<Object> values,
			boolean preparedStatement, boolean allowFiltering) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tableName);
		
		boolean emptyBeginsWith = (null == beginsWith || beginsWith.isEmpty());
		boolean emptyBeginsAndEndsWith = (null == beginsAndEndsWith || beginsAndEndsWith.isEmpty());
		boolean emptyEndsWith = (null == endsWith || endsWith.isEmpty());
		
		if (emptyBeginsWith && emptyBeginsAndEndsWith && emptyEndsWith) {
			sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(" where ");
		
		String conjunction = "";
		//PERCENT SIGN AT THE END
		if (!emptyBeginsWith) {
			String beginsWithConditionsStr = createLikeConditionsString(
					beginsWith, beginsWithCondDelim, true, false);
			sb.append(beginsWithConditionsStr);
			
			conjunction = overallDelimiter;
		}
		//PERCENT SIGN AT THE BEGINNING AND END
		if (!emptyBeginsAndEndsWith) {
			String beginsAndEndsWithCondStr = createLikeConditionsString(beginsAndEndsWith,
					beginsAndEndsWithCondDelim, true, true);
			sb.append(conjunction);
			sb.append(beginsAndEndsWithCondStr);
			
			conjunction = overallDelimiter;
		} else {
			conjunction = "";
		}
		//PERCENT SIGN AT THE BEGINNING
		if (!emptyEndsWith) {
			String endsWithCondStr = createLikeConditionsString(beginsAndEndsWith,
					beginsAndEndsWithCondDelim, true, true);
			sb.append(conjunction);
			sb.append(endsWithCondStr);
			
			conjunction = overallDelimiter;
		} else {
			conjunction = "";
		}
		
		//this is to tell cassandra even though searching by non row keys, it's alright
		if (allowFiltering) {
			sb.append(SPACE);
			sb.append(ALLOWFILTERING);
		}
		//close the query
		sb.append(";");
		log.info("(LIKE) selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}
	
	
	
	
	

	//the argument "values" is another return value. It will contain the values
	//to be set into the CqlPreparedStatement IN the proper order.
	//look at createComparisonConditionsString() for details. This IS just a copy of it.
	public String createPreparedComparisonConditionsString(Map<String, ?> conditions,
			List<Object> values, String comparator, String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : conditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = conditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(QUESTION);

			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String equalityConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr + "\t values=" + values);
		return equalityConditionsStr;
	}

	public String createComparisonConditionsString(Map<String, ?> conditions,
			String comparator, String conditionDelimiter) {

		List<Object> clauses = new ArrayList<Object>();

		//stringify the EQUALITY conditions. 
		//e.g. Map(col1=>x, col2=>y,...,colN=>something) 
		//col1 => x, now becomes String(col1) + String(=) + String(x)
		for (String key : conditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = conditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}

		//We made Map(col1=>x, col2=>y,...,colN=>something) into 
		//List(String(col1=x), String(col2=y),..., String(colN=Something))
		//implode (join together) the EQUALITY conditions with "AND"

		//e.g. List becomes String(String(col1=x) + AND +...+String(colN=something))
		String equalityConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr);
		return equalityConditionsStr;
	}


	public String createColInValuesString(String column, Collection<?> inValues) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(SPACE);
		sb.append(IN);
		sb.append(SPACE);

		String valuesStr = implode(inValues, COMMA);
		sb.append(valuesStr);

		String result = sb.toString();
		return result;
	}
	
	public String createIsConditionString(Map<String, ?> isConditions,
			String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : isConditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = isConditions.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(IS);
			clauseSb.append(SPACE);
			clauseSb.append(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}


		String isConditionsStr = implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + isConditionsStr);
		return isConditionsStr;
	}
	
	public String createPreparedLikeConditionsString(Map<String, ?> likeCondition,
			String likeCondDelim, boolean beginsWith, boolean endsWith, List<Object> values) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : likeCondition.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = likeCondition.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(LIKE);
			clauseSb.append(SPACE);
			
			if (endsWith) {
				clauseSb.append(PERCENT);
			}
			clauseSb.append(QUESTION);
			if (beginsWith) {
				clauseSb.append(PERCENT);
			}
			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String likeConditionsStr = implode(clauses, likeCondDelim); 

		log.info("equalityConditionsStr=" + likeConditionsStr + "\t values=" + values);
		return likeConditionsStr;
	}
	
	public String createLikeConditionsString(Map<String, ?> likeCondition,
			String likeCondDelim, boolean beginsWith, boolean endsWith) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : likeCondition.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = likeCondition.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(LIKE);
			clauseSb.append(SPACE);

			if (endsWith) {
				clauseSb.append(PERCENT);
			}
			clauseSb.append(obj);
			if (beginsWith) {
				clauseSb.append(PERCENT);
			}

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String likeConditionsStr = implode(clauses, likeCondDelim); 

		log.info("equalityConditionsStr=" + likeConditionsStr);
		return likeConditionsStr;
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
			strBuilder.append(SPACE);
			strBuilder.append(delimiter);
			strBuilder.append(SPACE);
		}

		int length = strBuilder.length();
		int delimLength = delimiter.length(); 
		return strBuilder.substring(0, length - delimLength - SPACELENGTH);
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
		return AND;
	}

	public String getNotNull() {
		return NOTNULL;
	}

	public String getNullStr() {
		return NULLSTR;
	}

	public String getOr() {
		return OR;
	}
	
}
