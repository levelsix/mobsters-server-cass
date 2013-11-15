package com.lvl6.mobsters.controller.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.dto.ConnectedPlayer;
import com.lvl6.mobsters.events.GameEvent;
import com.lvl6.mobsters.events.NormalResponseEvent;
import com.lvl6.mobsters.properties.APNSProperties;
import com.lvl6.mobsters.properties.Globals;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;


@Component
public class APNSWriter {

	@Autowired
	Globals globals;

	public Globals getGlobals() {
		return globals;
	}

	public void setGlobals(Globals globals) {
		this.globals = globals;
	}

	@Autowired
	private EventWriter eventWriter;

	public EventWriter getEventWriter() {
		return eventWriter;
	}

	public void setEventWriter(EventWriter eventWriter) {
		this.eventWriter = eventWriter;
	}

	public Map<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(Map<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Resource(name = "playersByPlayerId")
	protected Map<Integer, ConnectedPlayer> playersByPlayerId;

	private static Logger log = LoggerFactory.getLogger(APNSWriter.class);

	/*
	 * private static final int SOFT_MAX_NOTIFICATION_BADGES = 20;
	 * 
	 * private static final int MIN_MINUTES_BETWEEN_BATTLE_NOTIFICATIONS = 180;
	 * // 3 // hours private static final int
	 * MIN_MINUTES_BETWEEN_MARKETPLACE_NOTIFICATIONS = 0;//30; private static
	 * final int MIN_MINUTES_BETWEEN_WALL_POST_NOTIFICATIONS = 0;//15;
	 * 
	 * private static final int MAX_NUM_CHARACTERS_TO_SEND_FOR_WALL_POST = 120;
	 * 
	 * // 3 days private static final long
	 * MINUTES_BETWEEN_INACTIVE_DEVICE_TOKEN_FLUSH = 60 * 24 * 3; private static
	 * Date LAST_NULLIFY_INACTIVE_DEVICE_TOKEN_TIME = new Date();
	 */

	@Autowired
	protected APNSProperties apnsProperties;

	public void setApnsProperties(APNSProperties apnsProperties) {
		this.apnsProperties = apnsProperties;
	}

	/**
	 * constructor.
	 */
	public APNSWriter() {

	}

	/**
	 * note we override the Wrap's run method here doing essentially the same
	 * thing, but first we allocate a ByteBuffer for this thread to use
	 */
	public void run() {

	}

	public void handleEvent(GameEvent event, String actionKey, String alertBody) {
		if (event instanceof NormalResponseEvent) {
			processResponseEvent((NormalResponseEvent) event, actionKey, alertBody);
		}
	}

	/**
	 * our own version of processEvent that takes the additional parameter of
	 * the writeBuffer
	 */
	protected void processResponseEvent(NormalResponseEvent event, String actionKey, String alertBody) {
		String playerId = event.getPlayerId();
		ConnectedPlayer connectedPlayer = playersByPlayerId.get(playerId);
		if (connectedPlayer != null) {
			log.info("wrote a response event to connected player with id " + playerId
					+ " instead of sending APNS message");
			getEventWriter().handleEvent(event);
		}
		log.info("received APNS notification to send to player with id " + playerId);

		ApnsService service = null;
		try {
			service = getApnsService();
			if (null == service) {
				log.warn("Apns service is null, not writing APNS:" + " actionKey=" + actionKey
						+ "\t alertBody=" + alertBody);
				return;
			}
		} catch (FileNotFoundException e) {
			log.error("File not found", e);
		}

		createAndSendMessage(playerId, actionKey, alertBody);
	}

	private void createAndSendMessage(String playerId, String actionKey, String alertBody) {
/*		AuthorizedDevice exempt = null;
		List<AuthorizedDevice> devices = getAuthorizedDeviceService().devicesSharingUserAccount(playerId,
				exempt);

		if (null == devices || devices.isEmpty()) {
			log.warn("could not send push notification because userId " + playerId
					+ " has no devices registered to send apns to.");
			return;
		}

		// for every user's authorized device notify it
		for (AuthorizedDevice ad : devices) {
			String deviceId = ad.getDeviceId();
			if (null == deviceId || deviceId.isEmpty()) {
				log.warn("could not send push notification because authorized " + "device:" + ad
						+ " has no device token");
			}
			PayloadBuilder pb = APNS.newPayload().actionKey(actionKey).badge(1);
			pb.alertBody(alertBody);
			if (!pb.isTooLong()) {
				log.info("sending message. actionKey=" + actionKey + "\t alertBody=" + alertBody);
				service.push(deviceId, pb.build());
			} else {
				log.error("PayloadBuilder isTooLong to send apns message." + " alertBody=" + alertBody);
			}
		}*/
	}

	protected ApnsService service;

	public ApnsService getApnsService() throws FileNotFoundException {
		if (service == null) {
			log.info("Apns Service null... building new");
			buildService();
		}
		try {
			log.info("Testing APNS connection");
			service.testConnection();
		} catch (Throwable e) {
			log.info("ApnsService connection test failed... building again");
		}
		return service;
	}

	/*
	 * @Scheduled(fixedRate=1000*60*60) public void resetApnsService() {
	 * log.info("Rebuilding APNSService"); service.stop(); service = null; try {
	 * getApnsService(); } catch (FileNotFoundException e) {
	 * log.error("Error rebuilding APNSService", e); } }
	 */

	protected void buildService() throws FileNotFoundException {
		log.info("Building ApnsService");
		File certFile = new File(apnsProperties.pathToCert);
		log.info(certFile.getAbsolutePath());
		try {
			if (certFile.exists() && certFile.canRead()) {
				ApnsServiceBuilder builder = APNS.newService()
						//TODO: setup connection pool / executor service
						.withCert(apnsProperties.pathToCert, apnsProperties.certPassword);
				if (globals.getSandbox()) {
					log.info("Building apns with sandbox=true");
					builder.withSandboxDestination();
				} else {
					builder.withProductionDestination();
				}
				service = builder.build();
				service.start();
			} else {
				log.error("Apns Certificate exists: {}  can read: {}", certFile.exists(), certFile.canRead());
			}
		} catch (Exception e) {
			log.error("Error getting apns cert.. Invalid SSL Config Exception", e);
		}
	}

/*	public AuthorizedDeviceService getAuthorizedDeviceService() {
		return authorizedDeviceService;
	}

	public void setAuthorizedDeviceService(AuthorizedDeviceService authorizedDeviceService) {
		this.authorizedDeviceService = authorizedDeviceService;
	}*/

}// APNSWriter