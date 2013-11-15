package com.lvl6.mobsters.webapp.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.mobsters.controller.StartupController;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-application-context.xml")
public class SpringContextTest {
	private static final Logger log = LoggerFactory.getLogger(SpringContextTest.class);
	
	
	
	@Autowired
	protected StartupController startupController;
	
	
	@Test
	public void testApplicationContext() {
		log.info("ApplicationContext loaded");
	}
}
