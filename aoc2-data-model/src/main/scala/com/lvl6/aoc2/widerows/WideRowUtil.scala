package com.lvl6.aoc2.widerows

import java.util.Collection
import collection.JavaConversions._
import java.util.Map
import com.lvl6.aoc2.cassandra.Cassandra
import com.netflix.astyanax.model.ColumnFamily

object WideRowUtil {
	
  
 
  
  def  groupWideRowValuesByKey[Ky,Col,Val](values:Collection[WideRowValue[Ky, Col, Val]]):Map[Ky, Iterable[WideRowValue[Ky, Col, Val]]] = {
    return values.groupBy(_.getKey());
  }
  
  
  
  
  
  
}