package com.lvl6.aoc2.services.structure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.aoc2.entitymanager.StructureEntityManager;
import com.lvl6.aoc2.entitymanager.staticdata.StructureRetrieveUtils;

@Component
public class StructureServiceImpl implements StructureService {
	
	@Autowired
	protected StructureEntityManager structureEntityManager;
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	
	
	
	
	
	
	@Override
	public StructureEntityManager getStructureEntityManager() {
		return structureEntityManager;
	}
	
	@Override
	public void setStructureEntityManager(
			StructureEntityManager structureEntityManager) {
		this.structureEntityManager = structureEntityManager;
	}

	@Override
	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	@Override
	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}
	
	
}