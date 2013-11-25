package com.lvl6.mobsters.services.equipment;

import com.lvl6.mobsters.entitymanager.staticdata.StructureLabRetrieveUtils;

public interface EquipmentService {
	
	public abstract double DurabilityCostsDueToActionsPerformed(int actionsPerformed);
	
	public abstract StructureLabRetrieveUtils getEquipmentRetrieveUtils();

	public void setEquipmentRetrieveUtils(StructureLabRetrieveUtils structureLabRetrieveUtils);

}