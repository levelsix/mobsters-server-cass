package com.lvl6.mobsters.services.structurelab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.entitymanager.staticdata.StructureLabEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.StructureLabRetrieveUtils;


@Component
public class StructureLabServiceImpl implements StructureLabService {
	
	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils;

	@Autowired
	protected StructureLabEntityManager structureLabEntityManager;
	
	public double DurabilityCostsDueToActionsPerformed(int actionsPerformed) {
		double damage = (actionsPerformed)/10;
		return damage;
	}
	
	@Override
	public StructureLabRetrieveUtils getStructureLabRetrieveUtils() {
		return structureLabRetrieveUtils;
	}

	@Override
	public void setStructureLabRetrieveUtils(
			StructureLabRetrieveUtils structureLabRetrieveUtils) {
		this.structureLabRetrieveUtils = structureLabRetrieveUtils;
	}
	
}