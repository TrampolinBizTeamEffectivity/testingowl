package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JenkinsWebSiteReaderServiceTest {

	@Autowired
	@Qualifier("jenkinsWebSiteReaderServiceDummy")
	IJenkinsWebSiteReaderService serviceDummy;
	
	@Autowired
	@Qualifier("jenkinsWebSiteReaderService")
	IJenkinsWebSiteReaderService service;
	
	@Test
	public void testReadDummy() {
		println service.read(null);
	}
	
	@Test
	public void testReadNoAuth() {
		println service.read("https://ci.jenkins-ci.org/view/All/");
	}
	
	@Test
	public void testReadAuth() {
		println service.read("http://a99t-bld-se01.kud.bedag.ch/jenkins/", "rbqq", "***");
	}

}
