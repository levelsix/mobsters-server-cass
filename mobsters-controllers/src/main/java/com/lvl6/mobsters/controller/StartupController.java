package com.lvl6.mobsters.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtils;
import com.lvl6.mobsters.eventprotos.ForceLogoutEventProto.ForceLogoutResponseProto;
import com.lvl6.mobsters.eventprotos.StartupEventProto.StartupRequestProto;
import com.lvl6.mobsters.eventprotos.StartupEventProto.StartupResponseProto;
import com.lvl6.mobsters.eventprotos.StartupEventProto.StartupRequestProto.LoginType;
import com.lvl6.mobsters.eventprotos.StartupEventProto.StartupResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.StartupEventProto.StartupResponseProto.StartupStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.StartupRequestEvent;
import com.lvl6.mobsters.events.response.ForceLogoutResponseEvent;
import com.lvl6.mobsters.events.response.StartupResponseEvent;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.FullUser.FullUserProto;
import com.lvl6.mobsters.noneventprotos.FullUser.MinimumUserProto;
import com.lvl6.mobsters.po.UserDevice;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.time.TimeUtils;
import com.lvl6.mobsters.services.user.UserService;
import com.lvl6.mobsters.services.userdevice.UserDeviceService;


@Component
public class StartupController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserDeviceService userDeviceService;
	
	@Autowired
	protected UserService userService;

	@Autowired
	protected TimeUtils timeUtils;

	@Autowired
	protected CreateNoneventProtoUtils createNoneventProtoUtils;
	
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
		DateTime loginTime = new DateTime();
		log.info("reqProto=" + reqProto);

		MinimumUserProto mup = reqProto.getMup(); //mup might not have userId
		LoginType lt = reqProto.getLoginType();
		String gameCenterId = reqProto.getMup().getGameCenterId();
		String udid = reqProto.getMup().getUdid();
		
		//List<String> facebookFriendIds = reqProto.getFacebookFriendIdsList();
		

		//response to send back to client
		StartupResponseProto.Builder responseBuilder = StartupResponseProto.newBuilder();
		responseBuilder.setStatus(StartupStatus.FAIL_OTHER);
		StartupResponseEvent resEvent = new StartupResponseEvent(udid);
		resEvent.setTag(event.getTag());

		//mup object might not have userId if user deleted app or something
		List<User> userList = new ArrayList<User>();

		try {
			
			boolean validRequestArgs = isValidRequestArgs(responseBuilder,
					mup, lt, gameCenterId, udid);
			
			boolean validRequest = false;
			boolean successful = false;
			
			if (validRequestArgs) {
				validRequest = isValidRequest(responseBuilder, mup, lt,
						gameCenterId, udid, loginTime, userList);
			}

			if (validRequest) {
				successful = writeChangesToDb(responseBuilder, mup,
						gameCenterId, loginTime, udid, userList);
			}

			if (successful) {
				//set the recipient
				User u = userList.get(0);
				FullUserProto fup = getCreateNoneventProtoUtils().createFullUserProtoFromUser(u);
				responseBuilder.setFup(fup);
//				setFacebookFriends(responseBuilder, facebookFriendIds);
			}

			//TODO: CONSTRUCT THE LOGIN CONSTANTS
			//set the login constants
			setConstants(responseBuilder);

			StartupResponseProto resProto = responseBuilder.build();
			resEvent.setStartupResponseProto(resProto);


			log.info("Writing event: " + resEvent);
			getEventWriter().processPreDBResponseEvent(resEvent, udid);
		} catch (Exception e) {
			log.error("exception in LoginController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(StartupStatus.FAIL_OTHER);
				resEvent.setStartupResponseProto(responseBuilder.build());
				getEventWriter().processPreDBResponseEvent(resEvent, udid);

			} catch (Exception e2) {
				log.error("exception2 in RetrieveNewQuestionsController" +
						" processRequestEvent", e2);
			}
		}
	}
	
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
		Map<String, UserDevice> udidsToDevices = bootOtherDevicesSharingAccount(u, udid);
		
		//give the user the initial currency and stuff, if needed
		initializeUser(u, loginDate);
		
		updateUserLogin(udidsToDevices, udid, loginDate);

		
		return true;
	}
	
	//if another device, device1, is on in the foreground then device1 gets kicked off
	//if device1 is in the background but in the dungeon, user is penalized 
	//and device1 will startup regularly, not in the dungeon.
	private Map<String, UserDevice> bootOtherDevicesSharingAccount(User u, String udid) {
		UUID userId = u.getId();
		String userIdStr = userId.toString();
		Map<String, UserDevice> udidsToDevices = 
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
	
	private void updateUserLogin(Map<String, UserDevice> udidsToDevices,
			String currentUdid, Date loginDate) {
		Collection<UserDevice> udCollection = new ArrayList<UserDevice>();
		
		//go through each device and make lastLogin < lastLogout, except for
		//the current device
		for (String udid : udidsToDevices.keySet()) {
			
			UserDevice ud = udidsToDevices.get(udid);
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

	public UserDeviceService getUserDeviceService() {
		return userDeviceService;
	}

	public void setUserDeviceService(UserDeviceService userDeviceService) {
		this.userDeviceService = userDeviceService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public CreateNoneventProtoUtils getCreateNoneventProtoUtils() {
		return createNoneventProtoUtils;
	}

	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtils createNoneventProtoUtils) {
		this.createNoneventProtoUtils = createNoneventProtoUtils;
	}

}
