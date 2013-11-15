package com.lvl6.aoc2.services.structure;

import com.lvl6.aoc2.entitymanager.StructureEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.StructureRetrieveUtils;

public interface StructureService {
	
	
	
	
	public abstract StructureEntityManager getStructureEntityManager();
	
	public abstract void setStructureEntityManager(StructureEntityManager structureEntityManager);
	
	public StructureRetrieveUtils getStructureRetrieveUtils();

	public void setStructureRetrieveUtils(StructureRetrieveUtils structureRetrieveUtils);
}