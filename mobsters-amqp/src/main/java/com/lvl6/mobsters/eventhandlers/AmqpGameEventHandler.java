package com.lvl6.mobsters.eventhandlers;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;
import com.lvl6.mobsters.controller.EventController;
import com.lvl6.mobsters.controller.utils.EventControllersUtil;
import com.lvl6.mobsters.dto.ConnectedPlayer;
import com.lvl6.mobsters.events.PreDatabaseRequestEvent;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.utils.Attachment;


@Component
public class AmqpGameEventHandler extends AbstractGameEventHandler implements MessageListener {

	static Logger log = LoggerFactory.getLogger(AmqpGameEventHandler.class);

	private static final int DEFAULT_TTL = 9;
	
	@Autowired
	EventControllersUtil eventControllersUtil;

	public EventControllersUtil getEventControllersUtil() {
		return eventControllersUtil;
	}

	public void setEventControllersUtil(EventControllersUtil eventControllersUtil) {
		this.eventControllersUtil = eventControllersUtil;
	}

	@Resource(name = "playersByPlayerId")
	IMap<String, ConnectedPlayer> playersByPlayerId;

	public IMap<String, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(IMap<String, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Resource(name = "playersPreDatabaseByUDID")
	IMap<String, ConnectedPlayer> playersPreDatabaseByUDID;

	public IMap<String, ConnectedPlayer> getPlayersPreDatabaseByUDID() {
		return playersPreDatabaseByUDID;
	}

	public void setPlayersPreDatabaseByUDID(IMap<String, ConnectedPlayer> playersPreDatabaseByUDID) {
		this.playersPreDatabaseByUDID = playersPreDatabaseByUDID;
	}

	@Override
	public void onMessage(Message msg) {
		try {
		if (msg != null) {
			log.debug("Received message", msg.getMessageProperties().getMessageId());
			Attachment attachment = context.getBean(Attachment.class);
			byte[] payload = (byte[]) msg.getBody();
			attachment.readBuff = ByteBuffer.wrap(payload);
			while (attachment.eventReady()) {
				processAttachment(attachment);
				attachment.reset();
			}
		} else {
			throw new RuntimeException("Message was null or missing headers");
		}
		}catch(Exception e) {
			log.error("Error processing amqp message", e);
		}
	}
	
	protected void processAttachment(Attachment attachment) {
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOf(attachment.payload, attachment.payloadSize));
		EventController ec = getEventControllersUtil().getEventControllerByEventType(attachment.eventType);
		if (ec == null) {
			log.error("No event controller found in controllerMap for {}", attachment.eventType);
			return;
		}
		RequestEvent event = ec.createRequestEvent();
		event.setTag(attachment.tag);
		event.read(bb);
		log.debug("Received event from client: " + event.getPlayerId());
		if (getApplicationMode().isMaintenanceMode()) {
			if(event instanceof PreDatabaseRequestEvent) {
				String udid = ((PreDatabaseRequestEvent) event).getUdid();
				//messagingUtil.sendMaintanenceModeMessage(getApplicationMode().getMessageForUsers(), udid);
			}else {
				String playerId = event.getPlayerId();
				//messagingUtil.sendMaintanenceModeMessage(getApplicationMode().getMessageForUsers(), playerId);
				//TODO: Handle maintenance mode messages
			}
		} else {
			updatePlayerToServerMaps(event);
			ec.handleEvent(event);
		}
	}

	@Override
	protected void delegateEvent(byte[] bytes, RequestEvent event, String ip_connection_id,	Integer eventType) {
		if (event != null && eventType < 0) {
			log.error("the event type is < 0");
			return;
		}
		EventController ec = eventControllersUtil.getEventControllerByEventType(eventType);
		if (ec == null) {
			log.error("No EventController for eventType: " + eventType);
			return;
		}
		ec.handleEvent(event);
	}

	protected void updatePlayerToServerMaps(RequestEvent event) {
		log.debug("Updating player to server maps for player: " + event.getPlayerId());
		if (playersByPlayerId.containsKey(event.getPlayerId())) {
			ConnectedPlayer p = playersByPlayerId.get(event.getPlayerId());
			if (p != null) {
				p.setLastMessageSentToServer(new Date());
				playersByPlayerId.put(event.getPlayerId(), p, DEFAULT_TTL, TimeUnit.MINUTES);
			} else {
				addNewConnection(event);
			}
		} else {
			addNewConnection(event);
		}

	}

	protected void addNewConnection(RequestEvent event) {
		ConnectedPlayer newp = new ConnectedPlayer();
		if (event.getPlayerId() != "") {
			log.info("Player logged on: " + event.getPlayerId());
			newp.setPlayerId(event.getPlayerId());
			playersByPlayerId.put(event.getPlayerId(), newp, DEFAULT_TTL, TimeUnit.MINUTES);
		} else {
			newp.setUdid(((PreDatabaseRequestEvent) event).getUdid());
			getPlayersPreDatabaseByUDID().put(newp.getUdid(), newp, DEFAULT_TTL, TimeUnit.MINUTES);
			log.info("New player with UdId: " + newp.getUdid());
		}
	}

}
