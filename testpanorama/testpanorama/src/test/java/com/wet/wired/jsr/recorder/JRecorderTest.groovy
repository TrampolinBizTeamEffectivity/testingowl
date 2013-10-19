package com.wet.wired.jsr.recorder;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JRecorderTest {

	@Autowired
	JRecorder recorder
	
	@Test
	public void testStartRecorder() {
		
		recorder.@control.doClick();
		
		sleep 50000
		
		//fail("Not yet implemented");
	}

}
