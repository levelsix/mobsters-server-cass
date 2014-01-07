package com.lvl6.mobsters.controller.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureResidenceRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.StructureRetrieveUtils;
import com.lvl6.mobsters.noneventprotos.StructureProto.StructureInfoProto.StructType;
import com.lvl6.mobsters.po.nonstaticdata.StructureForUser;
import com.lvl6.mobsters.po.staticdata.Structure;
import com.lvl6.mobsters.po.staticdata.StructureResidence;


//utility class that messes with protos
public class StructureStuffUtils {
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;
	
	@Autowired
	protected StructureResidenceRetrieveUtils structureResidenceRetrieveUtils;

	public int getMinNumInvitesForStruct(StructureForUser sfu) {
		int structId = sfu.getStructureId();
	  	Structure struct = getStructureRetrieveUtils().getStructureForId(structId);
	  	String structType = struct.getStructType();
	  	
	  	int minNumInvites = -1;
	  	//at the moment, invites are only for residences
	  	if (StructType.valueOf(structType) == StructType.RESIDENCE) {
	  		StructureResidence residence = getStructureResidenceRetrieveUtils()
	  				.getResidenceForStructId(structId);
	  		minNumInvites = residence.getNumAcceptedFbInvites();
	  	}
	  	
	  	return minNumInvites;
	}

	public int getGemPriceForStruct(StructureForUser sfu) {
		//get the structure
		int structId = sfu.getStructureId();
		Structure struct = getStructureRetrieveUtils().getStructureForId(structId);
		String structType = struct.getStructType();

		int gemPrice = Integer.MAX_VALUE;
		//at the moment, invites are only for residences
		if (StructType.valueOf(structType) == StructType.RESIDENCE) {
			StructureResidence residence = getStructureResidenceRetrieveUtils()
					.getResidenceForStructId(structId);
			gemPrice = residence.getNumGemsRequired();
		}

		return gemPrice;
	}
	
	public StructureRetrieveUtils getStructureRetrieveUtils() {
		return structureRetrieveUtils;
	}

	public void setStructureRetrieveUtils(
			StructureRetrieveUtils structureRetrieveUtils) {
		this.structureRetrieveUtils = structureRetrieveUtils;
	}

	public StructureResidenceRetrieveUtils getStructureResidenceRetrieveUtils() {
		return structureResidenceRetrieveUtils;
	}

	public void setStructureResidenceRetrieveUtils(
			StructureResidenceRetrieveUtils structureResidenceRetrieveUtils) {
		this.structureResidenceRetrieveUtils = structureResidenceRetrieveUtils;
	}
	
}
