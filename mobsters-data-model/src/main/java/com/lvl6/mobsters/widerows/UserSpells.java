package com.lvl6.mobsters.widerows;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;


@Component
public class UserSpells extends BaseWideRow<UUID, UUID, Date> {

	
	
	public UserSpells() {
		super(UUID.class, UUID.class, Date.class);
	}

}
