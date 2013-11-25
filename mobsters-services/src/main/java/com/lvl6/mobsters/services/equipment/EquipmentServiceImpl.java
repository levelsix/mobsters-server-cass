package com.lvl6.mobsters.services.equipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureLabEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureLabRetrieveUtils;


@Component
public class EquipmentServiceImpl implements EquipmentService {
	
	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils;

	@Autowired
	protected StructureLabEntityManager structureLabEntityManager;
	
	public double DurabilityCostsDueToActionsPerformed(int actionsPerformed) {
		double damage = (actionsPerformed)/10;
		return damage;
	}
	
	@Override
	public StructureLabRetrieveUtils getEquipmentRetrieveUtils() {
		return structureLabRetrieveUtils;
	}

	@Override
	public void setEquipmentRetrieveUtils(
			StructureLabRetrieveUtils structureLabRetrieveUtils) {
		this.structureLabRetrieveUtils = structureLabRetrieveUtils;
	}
	
}