package com.lvl6.mobsters.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.MiscUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.ProfanityRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.UserBannedRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatRequestProto;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatResponseProto;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventChatProto.SendGroupChatResponseProto.SendGroupChatStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.SendGroupChatRequestEvent;
import com.lvl6.mobsters.events.response.SendGroupChatResponseEvent;
import com.lvl6.mobsters.noneventprotos.ChatProto.GroupChatScope;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.properties.MobstersTableConstants;
import com.lvl6.mobsters.services.clanchatpost.ClanChatPostService;
import com.lvl6.mobsters.services.user.UserService;


@Component
public class SendGroupChatController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected UserBannedRetrieveUtils userBannedRetrieveUtils;
	
	@Autowired
	protected ProfanityRetrieveUtils profanityRetrieveUtils;
	
	@Autowired
	protected ClanChatPostService clanChatPostService;
	
	@Autowired
	protected MiscUtil miscUtil;

	
	
	public SendGroupChatController() {
		numAllocatedThreads = 4;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new SendGroupChatRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_SEND_GROUP_CHAT_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		final SendGroupChatRequestProto reqProto = ((SendGroupChatRequestEvent) event)
		        .getSendGroupChatRequestProto();

		//get the values client sent
		MinimumUserProto senderProto = reqProto.getSender();
	    final GroupChatScope scope = reqProto.getScope();
	    String chatMessage = reqProto.getChatMessage();
	    final Timestamp timeOfPost = new Timestamp(new Date().getTime());

		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdString = senderProto.getUserUuid();
		UUID userId = UUID.fromString(userIdString);

		//response to send back to client
		Builder responseBuilder = SendGroupChatResponseProto.newBuilder();
		responseBuilder.setSender(senderProto);
		responseBuilder.setStatus(SendGroupChatStatus.FAIL_OTHER);
		SendGroupChatResponseEvent resEvent = new SendGroupChatResponseEvent(userIdString);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);

			boolean legitSend = checkLegitSend(responseBuilder, user, scope, chatMessage);
			
			if (legitSend) {
		        log.info("Group chat message is legit... sending to group");
		        Set<String> blackList = getProfanityRetrieveUtils().getAllProfanityTerms();
		        String censoredChatMessage = getMiscUtil().censorInput(
		        		chatMessage, blackList);
		        writeChangesToDB(user, scope, censoredChatMessage, timeOfPost);
		        //TODO: SEND ReceivedGroupChatResponseProto TO global or clan chat
		        //Note:
		        //looking at mysql version of server
		        //EventWriter.java has a HandleClanEvent()
		        //which calls abstract method processClanResponseEvent()
		        //EventWriterAmqp.java processes clan response events like it
		        //processes global chat response events
			}
			//write to client
			resEvent.setSendGroupChatResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in SendGroupChatController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(SendGroupChatStatus.FAIL_OTHER);
				resEvent.setSendGroupChatResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception2 in SendGroupChatController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitSend(Builder resBuilder, User user, GroupChatScope scope, String chatMessage) {
		if (user == null || scope == null || chatMessage == null || chatMessage.length() == 0) {
			resBuilder.setStatus(SendGroupChatStatus.FAIL_OTHER);
			log.error("user is " + user + ", scope is " + scope + ", chatMessage=" + chatMessage);
			return false;
		}

		if (chatMessage.length() > MobstersTableConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING) {
			resBuilder.setStatus(SendGroupChatStatus.FAIL_TOO_LONG);
			log.error("chat message is too long. allowed is "
					+ MobstersTableConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING + ", length is "
					+ chatMessage.length() + ", chatMessage is " + chatMessage);
			return false;
		}

		Set<UUID> banned = getUserBannedRetrieveUtils().getAllBannedUserIds();
		if(banned.contains(user.getId())) {
			resBuilder.setStatus(SendGroupChatStatus.FAIL_BANNED);
			log.warn("banned user tried to send a post. user=" + user);
			return false;
		}

		resBuilder.setStatus(SendGroupChatStatus.SUCCESS);
		return true;
	}

	private void writeChangesToDB(User user, GroupChatScope scope, String content, Timestamp timeOfPost) {
		// if (!user.updateRelativeNumGroupChatsRemainingAndDiamonds(-1, 0)) {
		// log.error("problem with decrementing a global chat");
		// }

		if (scope == GroupChatScope.CLAN) {
			getClanChatPostService().insertClanChatPost(user.getId(), user.getClanId(), content, timeOfPost);
		}
	}

	
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserBannedRetrieveUtils getUserBannedRetrieveUtils() {
		return userBannedRetrieveUtils;
	}

	public void setUserBannedRetrieveUtils(
			UserBannedRetrieveUtils userBannedRetrieveUtils) {
		this.userBannedRetrieveUtils = userBannedRetrieveUtils;
	}

	public ProfanityRetrieveUtils getProfanityRetrieveUtils() {
		return profanityRetrieveUtils;
	}

	public void setProfanityRetrieveUtils(
			ProfanityRetrieveUtils profanityRetrieveUtils) {
		this.profanityRetrieveUtils = profanityRetrieveUtils;
	}

	public ClanChatPostService getClanChatPostService() {
		return clanChatPostService;
	}

	public void setClanChatPostService(ClanChatPostService clanChatPostService) {
		this.clanChatPostService = clanChatPostService;
	}

	public MiscUtil getMiscUtil() {
		return miscUtil;
	}

	public void setMiscUtil(MiscUtil miscUtil) {
		this.miscUtil = miscUtil;
	}
	
}
