package com.wet.wired.jsr.recorder;

import static org.junit.Assert.*;

import com.wet.wired.jsr.player.JPlayer
import java.awt.event.ActionEvent
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JPlayerTest {

	@Autowired
	JPlayer player
	
	@Test
	public void testStartPlayer() {
		
		player.init(new String[0])
		
		sleep 1000000
		
		//fail("Not yet implemented");
	}

}
