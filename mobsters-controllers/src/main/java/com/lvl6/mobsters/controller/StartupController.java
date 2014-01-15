package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.AlertOnStartupRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.QuestRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupRequestProto;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto.StartupStatus;
import com.lvl6.mobsters.eventprotos.EventStartupProto.StartupResponseProto.UpdateStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.StartupRequestEvent;
import com.lvl6.mobsters.events.response.StartupResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.mobsters.noneventprotos.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.mobsters.noneventprotos.QuestStuffProto.FullUserQuestProto;
import com.lvl6.mobsters.noneventprotos.StaticDataStuffProto.StaticDataProto;
import com.lvl6.mobsters.noneventprotos.UserProto.FullUserProto;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.mobsters.noneventprotos.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.mobsters.po.nonstaticdata.MonsterEnhancingForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterForUser;
import com.lvl6.mobsters.po.nonstaticdata.MonsterHealingForUser;
import com.lvl6.mobsters.po.nonstaticdata.QuestForUser;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.nonstaticdata.UserFacebookInviteForSlot;
import com.lvl6.mobsters.po.staticdata.AlertOnStartup;
import com.lvl6.mobsters.po.staticdata.Quest;
import com.lvl6.mobsters.properties.Globals;
import com.lvl6.mobsters.services.monsterenhancingforuser.MonsterEnhancingForUserService;
import com.lvl6.mobsters.services.monsterforuser.MonsterForUserService;
import com.lvl6.mobsters.services.monsterhealingforuser.MonsterHealingForUserService;
import com.lvl6.mobsters.services.questforuser.QuestForUserService;
import com.lvl6.mobsters.services.taskforusercompleted.TaskForUserCompletedService;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.userfacebookinviteforslot.UserFacebookInviteForSlotService;


@Component
public class StartupController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	Globals globals;

	@Autowired
	protected MiscUtil miscUtil;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected QuestForUserService questForUserService;

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;
	
	@Autowired
	protected AlertOnStartupRetrieveUtils startupStuffRetrieveUtils;
	
	@Autowired
	protected MonsterForUserService monsterForUserService;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;

	@Autowired
	protected MonsterHealingForUserService monsterHealingForUserService;
	
	@Autowired
	protected MonsterEnhancingForUserService monsterEnhancingForUserService;
	
	@Autowired
	protected UserFacebookInviteForSlotService userFacebookInviteForSlotService;
	
	@Autowired
	protected TaskForUserCompletedService taskForUserCompletedService;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new StartupRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_STARTUP_EVENT_VALUE;
	}

	@Override
	@Transactional
	protected void processRequestEvent(RequestEvent event) throws Exception {
		StartupRequestProto reqProto = ((StartupRequestEvent) event).getStartupRequestProto();
		log.info("reqProto=" + reqProto);

		log.info("Processing startup request event");
		UpdateStatus updateStatus;
		String udid = reqProto.getUdid();
		String apsalarId = reqProto.hasApsalarId() ? reqProto.getApsalarId() : null;
		String fbId = reqProto.getFbId();
		
		//the player might be a new player with no user_id yet
		UUID playerId = null;
		getMiscUtil().setMDCProperties(udid, playerId);

		double tempClientVersionNum = reqProto.getVersionNum() * 10;
		//not sure if this is the right version number to use
	    double tempLatestVersionNum = getGlobals().getVersionNumber() * 10;

	    // Check version number
	    if ((int) tempClientVersionNum < (int) tempLatestVersionNum && tempClientVersionNum > 12.5) {
	      updateStatus = UpdateStatus.MAJOR_UPDATE;
	      log.info("player has been notified of forced update");
	    } else if (tempClientVersionNum < tempLatestVersionNum) {
	      updateStatus = UpdateStatus.MINOR_UPDATE;
	    } else {
	      updateStatus = UpdateStatus.NO_UPDATE;
	    }
	    
	    //response to send back to client
	    Builder resBuilder = StartupResponseProto.newBuilder();
	    resBuilder.setUpdateStatus(updateStatus);
	    resBuilder.setAppStoreURL(getGlobals().getAppStoreUrl());
	    resBuilder.setReviewPageURL(getGlobals().getReviewPageUrl());
	    resBuilder.setReviewPageConfirmationMessage(getGlobals().getReviewPageConfirmationMessage());
	    StartupResponseEvent resEvent = new StartupResponseEvent(udid);
	    resEvent.setTag(event.getTag());

	    // Don't fill in other fields if it is a major update
	    StartupStatus startupStatus = StartupStatus.USER_NOT_IN_DB;
	    Date now = new Date();
	    User user = null;
	    
		try {
			if (UpdateStatus.MAJOR_UPDATE.equals(updateStatus)) {
				List<User> userList = getUserService().getUserByUDIDorFbId(udid, fbId);
				user = getUserService().selectivelyChooseUser(userList, fbId, udid);
				if (null != user) {
					playerId = user.getId();
					startupStatus = StartupStatus.USER_IN_DB;
					log.info("No major update... getting user info");
					loginExistingUser(resBuilder, user, playerId);
				} else {
					log.info("no user id: tutorial(?) player with udid " + udid);
				}
				
				resBuilder.setStartupStatus(startupStatus);
				setConstants(resBuilder, startupStatus);
			}

			//startup time
			resBuilder.setServerTimeMillis((new Date()).getTime());
			StartupResponseProto resProto = resBuilder.build();
			resEvent.setStartupResponseProto(resProto);
			
			log.info("Writing event: " + resEvent);
			getEventWriter().processPreDBResponseEvent(resEvent, udid);
			
		} catch (Exception e) {
			log.error("exception in LoginController processRequestEvent", e);

			try {
				//try to tell client that something failed
				resBuilder.setStartupStatus(StartupStatus.USER_NOT_IN_DB);
				resEvent.setStartupResponseProto(resBuilder.build());
				getEventWriter().processPreDBResponseEvent(resEvent, udid);

			} catch (Exception e2) {
				log.error("exception2 in RetrieveNewQuestionsController" +
						" processRequestEvent", e2);
			}
		}
	}
	
	private void loginExistingUser(Builder resBuilder, User user, UUID userId) {
		setInProgressAndAvailableQuests(resBuilder, userId);
//		setUserClanInfos(resBuilder, user);
//        setNotifications(resBuilder, user);
        setNoticesToPlayers(resBuilder);
//        setChatMessages(resBuilder, user);
//        setPrivateChatPosts(resBuilder, user);
        setUserMonsterStuff(resBuilder, userId);
//        setBoosterPurchases(resBuilder);
        setFacebookAndExtraSlotsStuff(resBuilder, user);
        setCompletedTasks(resBuilder, userId);
        setAllStaticData(resBuilder, userId);
        
//        setWhetherPlayerCompletedInAppPurchase(resBuilder, user);
//        setUnhandledForgeAttempts(resBuilder, user);
//        setLockBoxEvents(resBuilder, user);
//        setLeaderboardEventStuff(resBuilder);
//        setAllies(resBuilder, user);
//        setAllBosses(resBuilder, user.getType());

        FullUserProto fup = getCreateNoneventProtoUtil().createFullUserProtoFromUser(user);
        resBuilder.setSender(fup);
	}
	
	private void setInProgressAndAvailableQuests(Builder resBuilder, UUID userId) {
		//questIds to user quests
		Map<Integer, QuestForUser> inProgressAndRedeemedUserQuests = getQuestForUserService()
				.getQuestIdsToUserQuestsForUser(userId);
		//		  	  log.info("user quests: " + inProgressAndRedeemedUserQuests);

		List<QuestForUser> inProgressQuests = new ArrayList<QuestForUser>();
		List<Integer> redeemedQuestIds = new ArrayList<Integer>();

		Map<Integer, Quest> questIdToQuests = getQuestRetrieveUtils().getQuestIdsToQuests();
		for (int questId : inProgressAndRedeemedUserQuests.keySet()) {
			QuestForUser uq = inProgressAndRedeemedUserQuests.get(questId);

			if (!uq.isRedeemed()) {
				//unredeemed quest section
				inProgressQuests.add(uq);
			} else {
				redeemedQuestIds.add(questId);
			}
		}

		//generate the user quests
		List<FullUserQuestProto> currentUserQuests = getCreateNoneventProtoUtil()
				.createFullUserQuestProtos(inProgressQuests, questIdToQuests);
		resBuilder.addAllUserQuests(currentUserQuests);

		//generate the redeemed quest ids
		resBuilder.addAllRedeemedQuestIds(redeemedQuestIds);
	}
	
	private void setNoticesToPlayers(Builder resBuilder) {
		List<AlertOnStartup> notices = getStartupStuffRetrieveUtils().getAllActiveAlerts();
		if (null != notices) {
			for (AlertOnStartup notice : notices) {
				String noticeMsg = notice.getMessage();
				resBuilder.addNoticesToPlayers(noticeMsg);
			}
		}
	}
	
	private void setUserMonsterStuff(Builder resBuilder, UUID userId) {
		List<MonsterForUser> userMonsters = getMonsterForUserService()
				.getMonstersForUser(userId);

		if (null != userMonsters && !userMonsters.isEmpty()) {
			for (MonsterForUser mfu : userMonsters) {
				FullUserMonsterProto fump = getCreateNoneventProtoUtil().createFullUserMonsterProtoFromUserMonster(mfu);
				resBuilder.addUsersMonsters(fump);
			}
		}
		
		Map<UUID, MonsterHealingForUser> userMonstersHealing =
				getMonsterHealingForUserService().getMonstersHealingForUser(userId);
		if (null != userMonstersHealing && !userMonstersHealing.isEmpty()) {
			
			for (MonsterHealingForUser mhfu : userMonstersHealing.values()) {
				UserMonsterHealingProto umhp = getCreateNoneventProtoUtil()
						.createUserMonsterHealingProtoFromObj(mhfu);
				resBuilder.addMonstersHealing(umhp);
			}
		}
		
		Map<UUID, MonsterEnhancingForUser> userMonstersEnhancing = getMonsterEnhancingForUserService()
				.getMonstersEnhancingForUser(userId);
		if (null != userMonstersEnhancing && !userMonstersEnhancing.isEmpty()) {
			//find the monster that is being enhanced
			Collection<MonsterEnhancingForUser> enhancingMonsters = userMonstersEnhancing.values();
			UserEnhancementItemProto baseMonster = null;

			List<UserEnhancementItemProto> feeders = new ArrayList<UserEnhancementItemProto>();
			for (MonsterEnhancingForUser mefu : enhancingMonsters) {
				UserEnhancementItemProto ueip = getCreateNoneventProtoUtil()
						.createUserEnhancementItemProtoFromObj(mefu);

				//TODO: if user has no monsters with null start time
				//(if user has all monsters with a start time), or if user has more than one
				//monster with a null start time
				//STORE THEM AND DELETE THEM OR SOMETHING

				//search for the monster that is being enhanced, the one with null start time
				Date startTime = mefu.getExpectedStartTime();
				if(null == startTime) {
					//found him
					baseMonster = ueip;
				} else {
					//just a feeder, add him to the list
					feeders.add(ueip);
				}
			}

			UserEnhancementProto uep = getCreateNoneventProtoUtil().createUserEnhancementProtoFromObj(
					userId, baseMonster, feeders);

			resBuilder.setEnhancements(uep);
		}
	}
	
	private void setFacebookAndExtraSlotsStuff (Builder resBuilder, User thisUser) {
		UUID userId = thisUser.getId();
		
		//get the invites where this user is the recipient, get unaccepted, hence, unredeemed invites
		Map<UUID, UserFacebookInviteForSlot> idsToInvitesToMe =
				new HashMap<UUID, UserFacebookInviteForSlot>();
		String fbId = thisUser.getFacebookId();
		List<UUID> specificInviteIds = null;
		boolean filterByAccepted = true;
		boolean isAccepted = false;
		boolean filterByRedeemed = false;
		boolean isRedeemed = false; //doesn't matter
		//base case where user does not have facebook id
		if (null != fbId && !fbId.isEmpty()) {
			idsToInvitesToMe = getUserFacebookInviteForSlotService()
					.getSpecificOrAllInvitesForRecipient(fbId, specificInviteIds, filterByAccepted,
							isAccepted, filterByRedeemed, isRedeemed);
		}

		//get the invites where this user is the inviter, get accepted, unredeemed does not matter 
		isAccepted = true;
		Map<UUID, UserFacebookInviteForSlot> idsToInvitesFromMe = 
				getUserFacebookInviteForSlotService().getSpecificOrAllInvitesForInviter(
						userId, specificInviteIds, filterByAccepted, isAccepted, filterByRedeemed, isRedeemed);

		Collection<UserFacebookInviteForSlot> invites = idsToInvitesFromMe.values();
		List<String> recipientFacebookIds = getUserFacebookInviteForSlotService()
				.getRecipientFbIds(invites);

		//to make it easier later on, get the inviter ids for these invites and
		//map inviter id to an invite
		Map<UUID, UserFacebookInviteForSlot> inviterIdsToInvites =
				new HashMap<UUID, UserFacebookInviteForSlot>();
		//inviterIdsToInvites will be populated by getInviterIds(...)
		List<UUID> inviterUserIds = getUserFacebookInviteForSlotService()
				.getInviterIds(idsToInvitesToMe, inviterIdsToInvites);


		//base case where user never did any invites
		if ((null == recipientFacebookIds || recipientFacebookIds.isEmpty()) &&
				(null == inviterUserIds || inviterUserIds.isEmpty())) {
			//no facebook stuff
			return;
		}


		//GET THE USERS
		Map<UUID, User> idsToUsers = getUserService()
				.getUsersForFacebookIdsOrUserIds(recipientFacebookIds, inviterUserIds);
		List<User> recipients = new ArrayList<User>();
		List<User> inviters = new ArrayList<User>();
		//given map of userIds to users, list of recipient facebook ids and list of inviter
		//user ids, separate the map of users into recipient and inviter
		getUserService().separateUsersIntoRecipientsAndInviters(idsToUsers,
				recipientFacebookIds, inviterUserIds, recipients, inviters);


		//send all the invites where this user is the one being invited
		for (UUID inviterId : inviterUserIds) {
			User inviter = idsToUsers.get(inviterId);
			MinimumUserProtoWithFacebookId inviterProto = null;
			UserFacebookInviteForSlot invite = inviterIdsToInvites.get(inviterId);
			UserFacebookInviteForSlotProto inviteProto = getCreateNoneventProtoUtil()
					.createUserFacebookInviteForSlotProtoFromInvite(invite, inviter, inviterProto);

			resBuilder.addInvitesToMeForSlots(inviteProto);
		}

		//send all the invites where this user is the one inviting
		MinimumUserProtoWithFacebookId thisUserProto = getCreateNoneventProtoUtil()
				.createMinimumUserProtoWithFacebookIdFromUser(thisUser);
		for (UserFacebookInviteForSlot invite : idsToInvitesFromMe.values()) {
			UserFacebookInviteForSlotProto inviteProto = getCreateNoneventProtoUtil()
					.createUserFacebookInviteForSlotProtoFromInvite(invite, thisUser, thisUserProto);
			resBuilder.addInvitesFromMeForSlots(inviteProto);
		}
	}
	
	private void setCompletedTasks(Builder resBuilder, UUID userId) {
		Set<Integer> taskIds = getTaskForUserCompletedService()
				.getAllCompletedTaskIdsForUser(userId);
		resBuilder.addAllCompletedTaskIds(taskIds);
	}
	
	private void setAllStaticData(Builder resBuilder, UUID userId) {
		StaticDataProto sdp = getMiscUtil().getAllStaticData(userId);
		resBuilder.setStaticDataStuffProto(sdp);
	}

	
	private void setConstants(Builder startupBuilder, StartupStatus startupStatus) {
		startupBuilder.setStartupConstants(getMiscUtil().createStartupConstantsProto());
		if (startupStatus == StartupStatus.USER_NOT_IN_DB) {
//			setTutorialConstants(startupBuilder);
		}
	}
	
	
/*
	//sanity check for data sent by the client, making sure server 
	//won't generate an null pointer exception (npe)
	private boolean isValidRequestArgs(Builder responseBuilder, 
			MinimumUserProto mup, LoginType lt, String gameCenterId,
			String udid) {

		if (LoginType.GAME_CENTER_ID == lt) {
			if (null == gameCenterId || gameCenterId.isEmpty()) {
				log.error("client error: login type is game center. " +
						"\t gameCenterId=" + gameCenterId + " mup=" + mup);
				return false;
			}	
		} else if (LoginType.UDID == lt) {
			//all client devices should have a udid
			if (null == udid || udid.isEmpty()) {
				log.error("client error: maybe null udid. udid=" + udid
						+ "\t mup=" + mup);
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	//userList won't be set if the user is a new user
	private boolean isValidRequest(Builder responseBuilder, MinimumUserProto mup,
			LoginType lt, String gameCenterId, String udid, DateTime loginTime,
			List<User> userList) throws Exception {
		if (LoginType.GAME_CENTER_ID == lt) {
			return isValidGameCenterId(responseBuilder, mup, loginTime,
					gameCenterId, udid, userList);
		}
		if (LoginType.UDID == lt) {
			return isValidUdid(responseBuilder, mup, loginTime,
					gameCenterId, udid, userList);
		}
		//		if (LoginType.FACEBOOK == lt) {
		//			return isValidFacebookLogin(responseBuilder, mup, userList);
		//		}
		//		if (LoginType.EMAIL_PASSWORD == lt) {
		//			return isValidEmailPasswordLogin(responseBuilder, mup, userList);
		//		}
		//		if (LoginType.NO_CREDENTIALS == lt) {
		//			return isValidNoCredentialsLogin(responseBuilder, mup, userList);
		//		}
		log.error("unexpected error: loginType=" + lt);
		return false;
	}

	//Game center id is only used to link a new device with an existing account.

	//Example 
	//UserA starts new game on deviceA. 
	//UserA starts game on deviceB. User has a choice to login to game center 
	//and reuse existing account or just continue with a new account. 
	//After this point if the user tries linking this account, on deviceB,
	//to game center then only this account can be reproduced on other accounts
	private boolean isValidGameCenterId(Builder responseBuilder,
			MinimumUserProto mup, DateTime loginTime, String gameCenterId,
			String udid, List<User> userList) throws Exception {
		log.info("game center id validation");

		//find user with game center id
		User u = getUserService().retrieveUser(gameCenterId, null);

		if (null != u) {
			//found user with specified game center id
			userList.add(u);
			responseBuilder.setStatus(StartupStatus.SUCCESS_GAME_CENTER_ID);

		} else {
			//none. find user tied to the udid
			u = getUserService().retrieveUserForUdid(udid);

			if (null != u) {
				//found user with udid
				userList.add(u);
				responseBuilder.setStatus(StartupStatus.SUCCESS_UDID);
			}
		}

		if (null == u) {
			//no user with game center id nor udid, signal new user
			responseBuilder.setStatus(StartupStatus.SUCCESS_NEW_USER);
		} else {
			userList.add(u);
		}
		return true;
	}

	private boolean isValidUdid(Builder responseBuilder,
			MinimumUserProto mup, DateTime loginTime, String gameCenterId,
			String udid, List<User> userList) throws Exception {
		log.info("udid validation");

		User u = getUserService().retrieveUserForUdid(udid);

		if (null == u) {
			responseBuilder.setStatus(StartupStatus.SUCCESS_NEW_USER);
		} else {
			userList.add(u);
			responseBuilder.setStatus(StartupStatus.SUCCESS_UDID);
		}

		return true;
	}


	private boolean writeChangesToDb(Builder responseBuilder,
			MinimumUserProto mup, String gameCenterId,
			DateTime loginTime, String udid, List<User> uList) {
		Date loginDate = loginTime.toDate();

		User u;
		if (responseBuilder.getStatus() == StartupStatus.SUCCESS_NEW_USER) {
			u = getUserService().createNewUser(gameCenterId, loginTime, udid);
			uList.add(u);
		} else {
			u = uList.get(0);
		}
		Map<String, Profanity> udidsToDevices = bootOtherDevicesSharingAccount(u, udid);

		//give the user the initial currency and stuff, if needed
		initializeUser(u, loginDate);

		updateUserLogin(udidsToDevices, udid, loginDate);


		return true;
	}

	//if another device, device1, is on in the foreground then device1 gets kicked off
	//if device1 is in the background but in the dungeon, user is penalized 
	//and device1 will startup regularly, not in the dungeon.
	private Map<String, Profanity> bootOtherDevicesSharingAccount(User u, String udid) {
		UUID userId = u.getId();
		String userIdStr = userId.toString();
		Map<String, Profanity> udidsToDevices = 
				getUserDeviceService().getUdidsToDevicesForUser(userId);

		boolean exitDungeon = false;

		//for each device that is not this device,
		//"kick them off" and make the user exit the dungeon
		for (String targetUdid : udidsToDevices.keySet()) {
			if (udid.equals(targetUdid)) {
				continue;
			}
			if (!exitDungeon) {
				exitDungeon = true;
			}

			ForceLogoutResponseEvent flre = new ForceLogoutResponseEvent(targetUdid);
			ForceLogoutResponseProto.Builder flrpb = ForceLogoutResponseProto.newBuilder();
			flrpb.setUserId(userIdStr);
			flrpb.setUdid(udid);

			flre.setForceLogoutResponseProto(flrpb.build());
			getEventWriter().processPreDBResponseEvent(flre, udid);
		}

		if (exitDungeon) {
			//TODO: kick the user out of the dungeon and
			//penalize him for doing so

		}

		return udidsToDevices;
	}

	private User initializeUser(User u, Date loginDate) {
		if (u.isAccountInitialized()) {
			return u;
		}
		getUserService().initializeUser(u, loginDate);

		return u;
	}

	private void updateUserLogin(Map<String, Profanity> udidsToDevices,
			String currentUdid, Date loginDate) {
		Collection<Profanity> udCollection = new ArrayList<Profanity>();

		//go through each device and make lastLogin < lastLogout, except for
		//the current device
		for (String udid : udidsToDevices.keySet()) {

			Profanity ud = udidsToDevices.get(udid);
			Date login = ud.getLastLogin();
			Date logout = ud.getLastLogout();

			if (udid.equals(currentUdid)) {
				ud.setLastLogin(loginDate);
				udCollection.add(ud);
				continue;
			}

			//for the devices that are not the current device, if
			//last_login > last_logout, set last_logout to be loginDate
			boolean isChronological = getTimeUtils().isFirstEarlierThanSecond(
					login, logout);
			if (!isChronological) {
				ud.setLastLogout(loginDate);
				udCollection.add(ud);
			}
		}

		getUserDeviceService().saveUserDevices(udCollection);
	}

	private void setFacebookFriends(Builder responseBuilder, List<String> facebookFriendIds) {
		//		//log.info("\t\t facebookFriendIds=" + facebookFriendIds);
		//		if (null == facebookFriendIds || facebookFriendIds.isEmpty()) {
		//			return;
		//		}
		//		List<MinimumUserProto> bupList = new ArrayList<MinimumUserProto>();
		//
		//		List<User> uList = getLoginService().getFacebookUsers(facebookFriendIds);
		//		//construct the protos for the users
		//		for (User u : uList) {
		//			AuthorizedDevice adNull = null;
		//			MinimumUserProto bup = getNoneventProtoUtils().createMinimumUserProto(u, adNull, null);
		//			bupList.add(bup);
		//		}
		//
		//		//log.info("\t\t sent facebookFriends=" + uList);
		//		responseBuilder.addAllFacebookFriendsWithAccounts(bupList);
	}


	private void setConstants(Builder responseBuilder) {
		//		LoginConstants.Builder lcb = LoginConstants.newBuilder();
		//		CurrencyConstants cc = getCurrencyConstants();
		//		RoundConstants rc = getRoundConstants();
		//		QuestionTypeScoringConstants qtsc = getQuestionTypeScoringConstants();
		//
		//		lcb.setCurrencyConstants(cc);
		//		lcb.setRoundConstants(rc);
		//		lcb.setScoreTypes(qtsc);

		//		responseBuilder.setLoginConstants(lcb.build());
	}
*/
	public Globals getGlobals() {
		return globals;
	}

	public void setGlobals(Globals globals) {
		this.globals = globals;
	}
	
	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public QuestForUserService getQuestForUserService() {
		return questForUserService;
	}

	public void setQuestForUserService(QuestForUserService questForUserService) {
		this.questForUserService = questForUserService;
	}

	public QuestRetrieveUtils getQuestRetrieveUtils() {
		return questRetrieveUtils;
	}

	public void setQuestRetrieveUtils(QuestRetrieveUtils questRetrieveUtils) {
		this.questRetrieveUtils = questRetrieveUtils;
	}

	public AlertOnStartupRetrieveUtils getStartupStuffRetrieveUtils() {
		return startupStuffRetrieveUtils;
	}

	public void setStartupStuffRetrieveUtils(
			AlertOnStartupRetrieveUtils startupStuffRetrieveUtils) {
		this.startupStuffRetrieveUtils = startupStuffRetrieveUtils;
	}

	public MonsterForUserService getMonsterForUserService() {
		return monsterForUserService;
	}

	public void setMonsterForUserService(MonsterForUserService monsterForUserService) {
		this.monsterForUserService = monsterForUserService;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtil() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtil(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}

	public MonsterHealingForUserService getMonsterHealingForUserService() {
		return monsterHealingForUserService;
	}

	public void setMonsterHealingForUserService(
			MonsterHealingForUserService monsterHealingForUserService) {
		this.monsterHealingForUserService = monsterHealingForUserService;
	}

	public MonsterEnhancingForUserService getMonsterEnhancingForUserService() {
		return monsterEnhancingForUserService;
	}

	public void setMonsterEnhancingForUserService(
			MonsterEnhancingForUserService monsterEnhancingForUserService) {
		this.monsterEnhancingForUserService = monsterEnhancingForUserService;
	}

	public UserFacebookInviteForSlotService getUserFacebookInviteForSlotService() {
		return userFacebookInviteForSlotService;
	}

	public void setUserFacebookInviteForSlotService(
			UserFacebookInviteForSlotService userFacebookInviteForSlotService) {
		this.userFacebookInviteForSlotService = userFacebookInviteForSlotService;
	}

	public TaskForUserCompletedService getTaskForUserCompletedService() {
		return taskForUserCompletedService;
	}

	public void setTaskForUserCompletedService(
			TaskForUserCompletedService taskForUserCompletedService) {
		this.taskForUserCompletedService = taskForUserCompletedService;
	}

}
