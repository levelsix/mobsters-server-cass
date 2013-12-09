package com.lvl6.mobsters.services.structurelab;

import com.lvl6.mobsters.entitymanager.staticdata.StructureLabRetrieveUtils;

public interface StructureLabService {
	
	public abstract double DurabilityCostsDueToActionsPerformed(int actionsPerformed);
	
	public abstract StructureLabRetrieveUtils getStructureLabRetrieveUtils();

	public void setStructureLabRetrieveUtils(StructureLabRetrieveUtils structureLabRetrieveUtils);

}