package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JenkinsWebSiteReaderServiceDummyTest {

	@Autowired
	@Qualifier("jenkinsWebSiteReaderServiceDummy")
	IJenkinsWebSiteReaderService service;
	
	@Test
	public void testRead() {
		println service.read(null, null, null);
	}

}
