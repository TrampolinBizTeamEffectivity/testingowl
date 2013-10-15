package ch.sebastianfiechter.utils.buildbulb

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class MainTest {

	@Autowired
	Main main;
	
	@Test
	public void testMain() {
		
		main.repeat();
		
		//fail("Not yet implemented");
	}

}
