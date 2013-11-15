package com.lvl6.mobsters.services.equipment;

import com.lvl6.mobsters.entitymanager.staticdata.EquipmentRetrieveUtils;

public interface EquipmentService {
	
	public abstract double DurabilityCostsDueToActionsPerformed(int actionsPerformed);
	
	public abstract EquipmentRetrieveUtils getEquipmentRetrieveUtils();

	public void setEquipmentRetrieveUtils(EquipmentRetrieveUtils equipmentRetrieveUtils);

}