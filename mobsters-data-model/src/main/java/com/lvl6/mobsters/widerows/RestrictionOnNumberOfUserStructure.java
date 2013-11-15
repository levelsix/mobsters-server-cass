package com.lvl6.mobsters.widerows;


import java.util.UUID;

import org.springframework.stereotype.Component;


@Component
public class RestrictionOnNumberOfUserStructure extends BaseWideRow<Integer, UUID, Integer> {

	
	//level of user, structure id (uuid), and number allowed to user
	public RestrictionOnNumberOfUserStructure() {
		super(Integer.class, UUID.class, Integer.class);
	}

}
