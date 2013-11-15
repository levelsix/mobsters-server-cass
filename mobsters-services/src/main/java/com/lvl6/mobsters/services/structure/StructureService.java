package com.lvl6.mobsters.services.structure;

import com.lvl6.mobsters.entitymanager.StructureEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureRetrieveUtils;

public interface StructureService {
	
	
	
	
	public abstract StructureEntityManager getStructureEntityManager();
	
	public abstract void setStructureEntityManager(StructureEntityManager structureEntityManager);
	
	public StructureRetrieveUtils getStructureRetrieveUtils();

	public void setStructureRetrieveUtils(StructureRetrieveUtils structureRetrieveUtils);
}