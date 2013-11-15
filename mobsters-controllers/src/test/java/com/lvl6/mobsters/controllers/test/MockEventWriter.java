package com.lvl6.mobsters.controllers.test;

import org.springframework.stereotype.Component;

import com.lvl6.mobsters.controller.utils.EventWriter;
import com.lvl6.mobsters.events.GameEvent;
import com.lvl6.mobsters.events.ResponseEvent;


@Component
public class MockEventWriter extends EventWriter {

	@Override
	protected void processEvent(GameEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void processGlobalChatResponseEvent(ResponseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processPreDBResponseEvent(ResponseEvent event, String udid) {
		// TODO Auto-generated method stub

	}

}
