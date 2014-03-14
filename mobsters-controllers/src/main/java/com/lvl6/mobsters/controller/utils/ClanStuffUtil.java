package com.lvl6.mobsters.controller.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.noneventprotos.ClanProto.UserClanStatus;


public class ClanStuffUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

//	public ClanEventPersistentForClan createClanEventPersistentForClan(
//			PersistentClanEventClanInfoProto pceip) {
//		log.info("creating ClanEventPersistentForClan");
//
//		int clanId = pceip.getClanId();
//		int clanEventPersistentId = pceip.getClanEventId();
//		int crId = pceip.getClanRaidId();
//		int crsId = pceip.getClanRaidStageId();
//		Date stageStartTime = null;
//		long stageStartTimeMillis = pceip.getStageStartTime();
//		if (stageStartTimeMillis > 0) {
//			stageStartTime = new Date(stageStartTimeMillis);
//		}
//		int crsmId = pceip.getCrsmId();
//
//		Date stageMonsterStartTime = null;
//		long stageMonsterStartTimeMillis = pceip.getStageMonsterStartTime();
//		if (stageMonsterStartTimeMillis > 0) { 
//			stageMonsterStartTime = new Date(stageMonsterStartTimeMillis);
//		}
//
//		return new ClanEventPersistentForClan(clanId, clanEventPersistentId, crId, crsId,
//				stageStartTime, crsmId, stageMonsterStartTime);
//	}

	public boolean firstUserClanStatusAboveSecond(String firstStatus,
			UserClanStatus first, String secondStatus, UserClanStatus second) {
		
		try {
			if (null == first) {
				first = UserClanStatus.valueOf(firstStatus);
			}
			if (null == second) {
				second = UserClanStatus.valueOf(secondStatus);
			}
		} catch (Exception e) {
			log.error("invalid clan status string. statusOne=" + firstStatus +
					"\t statusTwo=" + secondStatus, e);
		}
		
		if (null == first || null == second) {
			return false;
		}

		if (first.equals(second)) {
			return false;
		}
		if (UserClanStatus.LEADER.equals(second)) {
			return false;
		}
		if (UserClanStatus.MEMBER.equals(first)) {
			return false;
		}
		if (UserClanStatus.CAPTAIN.equals(first) &&
				UserClanStatus.JUNIOR_LEADER.equals(second)) {
			return false;
		}

		return true;

	}
}