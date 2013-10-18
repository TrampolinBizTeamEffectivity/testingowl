package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.*
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
		println serviceDummy.read();
	}
	
	@Test
	public void testReadNoAuth() {
		println service.read("http://deadlock.netbeans.org/hudson/api/xml");
	}
	
	@Test
	public void testReadNoAuthEmptyCredentials() {
		println service.read("http://deadlock.netbeans.org/hudson/api/xml", "", "");
	}
	
	@Test(expected = Exception.class)
	public void testReadAuthFail() {
		service.read("http://deadlock.netbeans.org/hudson/api/xml", "test", "test");
	}
	
	@Test
	@Ignore
	public void testReadAuth() {
		println service.read("http://a99t-bld-se01.kud.bedag.ch/jenkins/api/xml", "rbqq", "***");
	}

}
