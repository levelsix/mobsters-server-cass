package com.lvl6.mobsters.services.expansionpurchaseforuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lvl6.mobsters.entitymanager.nonstaticdata.ExpansionPurchaseForUserEntityManager;
import com.lvl6.mobsters.entitymanager.staticdata.utils.ExpansionCostRetrieveUtils;
import com.lvl6.mobsters.po.nonstaticdata.ExpansionPurchaseForUser;
import com.lvl6.mobsters.po.staticdata.ExpansionCost;
import com.lvl6.mobsters.utils.QueryConstructionUtil;

public interface ExpansionPurchaseForUserService {
	
	//CONTROLLER LOGIC STUFF****************************************************************
	public abstract ExpansionCost getExpansionCost(int nthExpansion);
	
	public abstract int calculateExpansionCostCash(int numOfExpansions);
	
	public abstract ExpansionPurchaseForUser selectSpecificExpansion(int xPosition, int yPosition,
			List<ExpansionPurchaseForUser> epfuList);
		
	//RETRIEVING STUFF****************************************************************
	public abstract List<ExpansionPurchaseForUser> getExpansionPurchasesForUser(UUID userId);
	
	public abstract ExpansionPurchaseForUser getSpecificExpansionPurchaseForUser(UUID userId,
			int xPosition, int yPosition);
	
	//INSERTING STUFF****************************************************************
	public abstract ExpansionPurchaseForUser createNewUserExpansionPurchaseForUser(UUID userId, int expansionPurchaseId, Date now);
	
	
	//SAVING STUFF****************************************************************
	public abstract void saveExpansionPurchaseForUser(ExpansionPurchaseForUser qfu);
	
	public abstract void saveExpansionPurchasesForUser(Collection<ExpansionPurchaseForUser> qfuList);
	
	
	
	//for the setter dependency injection or something****************************************************************
	public abstract ExpansionCostRetrieveUtils getExpansionCostRetrieveUtils();
	
	public abstract void setExpansionCostRetrieveUtils(ExpansionCostRetrieveUtils expansionPurchaseRetrieveUtils);
	
	public abstract ExpansionPurchaseForUserEntityManager getExpansionPurchaseForUserEntityManager();

	public abstract void setExpansionPurchaseForUserEntityManager(
			ExpansionPurchaseForUserEntityManager expansionPurchaseForUserEntityManager);

	public abstract QueryConstructionUtil getQueryConstructionUtil(); 
	
	public abstract void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil);
	
}
