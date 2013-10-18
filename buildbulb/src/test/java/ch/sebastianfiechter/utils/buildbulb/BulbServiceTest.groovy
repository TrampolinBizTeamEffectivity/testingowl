package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.*

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class BulbServiceTest {

	@Autowired
	@Qualifier("bulbServiceDummy")
	IBulbService serviceDummy;
	
	@Autowired
	@Qualifier("bulbService")
	IBulbService service;
	
	@After
	def void tearDownClass() {
		sleep 5000
		service.switchOff()
	}
	
	@Test
	public void testShowRedDummy() {
		serviceDummy.showRed();
		serviceDummy.showGreen();
	}
	
	@Test
	public void testShowStartup() {
		service.showStartup()
	}
	
	@Test
	public void testShowGreen() {
		service.showGreen()
	}
	
	@Test
	public void testShowRed() {
		service.showRed()
	}
	
	@Test
	public void testSwitchOff() {
		service.switchOff()
	}
	
}
