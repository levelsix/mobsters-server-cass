package com.lvl6.aoc2.services.equipment;

import com.lvl6.aoc2.entitymanager.staticdata.EquipmentRetrieveUtils;

public interface EquipmentService {
	
	public abstract double DurabilityCostsDueToActionsPerformed(int actionsPerformed);
	
	public abstract EquipmentRetrieveUtils getEquipmentRetrieveUtils();

	public void setEquipmentRetrieveUtils(EquipmentRetrieveUtils equipmentRetrieveUtils);

}