package com.lvl6.aoc2.services.equipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.EquipmentEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.EquipmentRetrieveUtils;


@Component
public class EquipmentServiceImpl implements EquipmentService {
	
	@Autowired
	protected EquipmentRetrieveUtils equipmentRetrieveUtils;

	@Autowired
	protected EquipmentEntityManager equipmentEntityManager;
	
	public double DurabilityCostsDueToActionsPerformed(int actionsPerformed) {
		double damage = (actionsPerformed)/10;
		return damage;
	}
	
	@Override
	public EquipmentRetrieveUtils getEquipmentRetrieveUtils() {
		return equipmentRetrieveUtils;
	}

	@Override
	public void setEquipmentRetrieveUtils(
			EquipmentRetrieveUtils equipmentRetrieveUtils) {
		this.equipmentRetrieveUtils = equipmentRetrieveUtils;
	}
	
}