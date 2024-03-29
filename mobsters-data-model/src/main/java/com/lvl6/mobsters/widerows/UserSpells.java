package com.lvl6.mobsters.widerows;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dekayd.astyanax.cassandra.widerow.BaseWideRow;


@Component
public class UserSpells extends BaseWideRow<UUID, UUID, Date> {

	
	//userId, spellId, time created(?)
	public UserSpells() {
		super(UUID.class, UUID.class, Date.class);
	}

}
