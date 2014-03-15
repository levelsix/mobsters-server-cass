package com.lvl6.mobsters.controller.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.eventprotos.EventMonsterProto.InviteFbFriendsForSlotsRequestProto.FacebookInviteStructure;
//utility class that messes with protos
@Component
public class FacebookStuffUtil {
	
	public List<String> demultiplexFacebookInviteStructure(List<FacebookInviteStructure> invites,
	  		Map<String, UUID> fbIdsToUserStructIds, Map<String, Integer> fbIdsToUserStructFbLvl) {
	  	
	  	List<String> retVal = new ArrayList<String>();
	  	for (FacebookInviteStructure fis : invites) {
	  		String fbId = fis.getFbFriendId();
	  		
	  		String userStructIdStr = fis.getUserStructId();
	  		UUID userStructId = UUID.fromString(userStructIdStr);
	  		int userStructFbLvl = fis.getUserStructFbLvl();
	  		
	  		retVal.add(fbId);
	  		fbIdsToUserStructIds.put(fbId, userStructId);
	  		fbIdsToUserStructFbLvl.put(fbId, userStructFbLvl);
	  	}
	  	return retVal;
	  }
	
}
