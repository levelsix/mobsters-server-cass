package com.lvl6.mobsters.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.CreateNoneventProtoUtil;
import com.lvl6.mobsters.entitymanager.staticdata.utils.CityElementRetrieveUtils;
import com.lvl6.mobsters.entitymanager.staticdata.utils.CityRetrieveUtils;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityRequestProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityResponseProto;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityResponseProto.Builder;
import com.lvl6.mobsters.eventprotos.EventCityProto.LoadCityResponseProto.LoadCityStatus;
import com.lvl6.mobsters.events.RequestEvent;
import com.lvl6.mobsters.events.request.LoadCityRequestEvent;
import com.lvl6.mobsters.events.response.LoadCityResponseEvent;
import com.lvl6.mobsters.noneventprotos.CityProto.CityElementProto;
import com.lvl6.mobsters.noneventprotos.MobstersEventProtocolProto.MobstersEventProtocolRequest;
import com.lvl6.mobsters.noneventprotos.UserProto.MinimumUserProto;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.po.staticdata.City;
import com.lvl6.mobsters.po.staticdata.CityElement;
import com.lvl6.mobsters.services.user.UserService;



@Component
public class LoadCityController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected CityRetrieveUtils cityRetrieveUtils;
	
	@Autowired
	protected CityElementRetrieveUtils cityElementRetrieveUtils;
	
	@Autowired
	protected CreateNoneventProtoUtil createNoneventProtoUtil;
	
	

	public LoadCityController() {
		numAllocatedThreads = 3;
	}
	
	@Override
	public RequestEvent createRequestEvent() {
		return new LoadCityRequestEvent();
	}

	@Override
	public int getEventType() {
		return MobstersEventProtocolRequest.C_LOAD_CITY_EVENT_VALUE;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		//stuff client sent
		LoadCityRequestProto reqProto = ((LoadCityRequestEvent) event)
				.getLoadCityRequestProto();

		//get the values client sent
		MinimumUserProto sender = reqProto.getSender();
		int cityId = reqProto.getCityId();
		
		//uuid's are not strings, need to convert from string to uuid, vice versa
		String userIdStr = sender.getUserUuid();
		UUID userId = UUID.fromString(userIdStr);

		//response to send back to client
		Builder responseBuilder = LoadCityResponseProto.newBuilder();
		responseBuilder.setStatus(LoadCityStatus.FAIL_OTHER);
		responseBuilder.setCityId(cityId);
		responseBuilder.setSender(sender);
		LoadCityResponseEvent resEvent = new LoadCityResponseEvent(userIdStr);
		resEvent.setTag(event.getTag());

		try {
			//get whatever we need from the database
			User user = getUserService().getUserWithId(userId);
			City city = getCityRetrieveUtils().getCityForCityId(cityId);
			
			boolean legitCityLoad = checkLegitCityLoad(responseBuilder, user, city);//, currentCityRankForUser);

			if(legitCityLoad) {
				List<CityElement> elems = getCityElementRetrieveUtils().getCityElementsForCity(cityId);
				if (null != elems) {
					for (CityElement ce : elems) {
						CityElementProto cep = getCreateNoneventProtoUtils()
								.createCityElementProtoFromCityElement(ce);
						responseBuilder.addCityElements(cep);
					}
				}
				
				//TODO: FIGURE OUT IF NEED TO SET THE IN PROGRESS QUESTS FOR THIS CITY
			}

			//write to client
			resEvent.setLoadCityResponseProto(responseBuilder.build());
			log.info("Writing event: " + resEvent);
			getEventWriter().handleEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in LoadCityController processRequestEvent", e);

			try {
				//try to tell client that something failed
				responseBuilder.setStatus(LoadCityStatus.FAIL_OTHER);
				resEvent.setLoadCityResponseProto(responseBuilder.build());
				getEventWriter().handleEvent(resEvent);

			} catch (Exception e2) {
				log.error("exception in LoadCityController processRequestEvent", e2);
			}
		}
	}

	private boolean checkLegitCityLoad(Builder resBuilder, User user, City city) {//, int currentCityRankForUser) {
		if (city == null || user == null) {
			resBuilder.setStatus(LoadCityStatus.FAIL_OTHER);
			log.error("city or user is null. city=" + city + ", user=" + user);
			return false;
		}
		//	    if (currentCityRankForUser < 1) {
		//	      resBuilder.setStatus(LoadCityStatus.NOT_ACCESSIBLE_TO_USER);
		//	      log.error("city " + city + "is not unlocked for user");
		//	      return false;
		//	    }
		resBuilder.setStatus(LoadCityStatus.SUCCESS);
		return true;
	}

	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CityRetrieveUtils getCityRetrieveUtils() {
		return cityRetrieveUtils;
	}

	public void setCityRetrieveUtils(
			CityRetrieveUtils cityRetrieveUtils) {
		this.cityRetrieveUtils = cityRetrieveUtils;
	}

	public CityElementRetrieveUtils getCityElementRetrieveUtils() {
		return cityElementRetrieveUtils;
	}

	public void setCityElementRetrieveUtils(
			CityElementRetrieveUtils cityElementRetrieveUtils) {
		this.cityElementRetrieveUtils = cityElementRetrieveUtils;
	}

	public CreateNoneventProtoUtil getCreateNoneventProtoUtils() {
		return createNoneventProtoUtil;
	}

	public void setCreateNoneventProtoUtils(
			CreateNoneventProtoUtil createNoneventProtoUtil) {
		this.createNoneventProtoUtil = createNoneventProtoUtil;
	}
	
}
