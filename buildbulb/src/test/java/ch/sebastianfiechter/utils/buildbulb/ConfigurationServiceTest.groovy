package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class ConfigurationServiceTest {

	@Autowired
	ConfigurationService service;
	
	@Test
	public void testReadUser() {
		assert "rbqq" == service.config.user.toString()
	}
	
	@Test
	public void testReadJobs() {
		assert 2 == service.config.jobs.job.size()
	}

}