package com.wet.wired.jsr.recorder;

import static org.junit.Assert.*;

import ch.sebastianfiechter.testpanorama.AudioCapture01
import com.wet.wired.jsr.player.JPlayer
import java.awt.event.ActionEvent
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class AudioCapture01Test {

	@Autowired
	AudioCapture01 audio
	
	@Test
	public void testAudio() {
		audio.startup();
		sleep 100000;
		
	}

}
