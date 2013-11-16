package com.lvl6.mobsters.cassandra;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class CQL3Util {

	
//	private static final Logger log = LoggerFactory.getLogger(CQL3Util.class);
	
    public static <T> String getCql3Type(Class<?> valueClass) {
    	//log.info("Type: {}", valueClass.getSimpleName());
        if (valueClass.equals(UUID.class)) {
            return "uuid";
        }
        else if (valueClass.equals(String.class)) {
            return "varchar";
        }
        else if (valueClass.equals(Long.class) || valueClass.equals(long.class)) {
            return "bigint";
        }
        else if (valueClass.equals(Integer.class) || valueClass.equals(int.class)) {
            return "int";
        }
        else if (valueClass.equals(Float.class) || valueClass.equals(float.class)) {
            return "float";
        }
        else if (valueClass.equals(Double.class) || valueClass.equals(double.class)) {
            return "double";
        }
        else if (valueClass.equals(BigInteger.class)) {
            return "bigint";
        }
        else if (valueClass.equals(Boolean.class) || valueClass.equals(boolean.class)) {
            return "boolean";
        }
        else if (valueClass.equals(Date.class)) {
            return "timestamp";
        }
        else{
            return "blob";
        }
    }
	
}
