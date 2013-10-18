package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

import ch.sebastianfiechter.utils.buildbulb.JobsCheckerService.JobStatus;

/**
 * upload - blue - Success
 * NB-Core-Build - blue_anime - Success
 * uml - red - Failed
 * visualweb - disabled - Disabled
 * web-main-tests-checkout - grey - Pending
 * ergonomics - yellow - Unstable
 * 
 * @author rbqq
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JobsCheckerServiceTest {

	@Autowired
	JobsCheckerService service
	
	@Autowired
	@Qualifier("jenkinsWebSiteReaderServiceDummy")
	IJenkinsWebSiteReaderService htmlService
	
	@Test
	public void testCheckStates() {
		assert JobStatus.Success == service.checkStatus(htmlService.read(), ["upload"])
		assert JobStatus.Success == service.checkStatus(htmlService.read(), ["NB-Core-Build"])
		assert JobStatus.Failed == service.checkStatus(htmlService.read(), ["uml"])
		assert JobStatus.Disabled == service.checkStatus(htmlService.read(), ["visualweb"])
		assert JobStatus.Pending == service.checkStatus(htmlService.read(), ["web-main-tests-checkout"])
		assert JobStatus.Unstable == service.checkStatus(htmlService.read(), ["ergonomics"])
	}
	
	@Test
	public void testCheckSuccess() {
		assert JobStatus.Success == service.checkStatus(htmlService.read(), ["upload", "NB-Core-Build"])
	}
	
	@Test
	public void testCheckFailed() {
		assert JobStatus.Failed == service.checkStatus(htmlService.read(), ["uml", "NB-Core-Build"])
	}
	
	@Test
	public void testCheckDisabled() {
		assert JobStatus.Disabled == service.checkStatus(htmlService.read(), ["upload", "visualweb"])
	}
	
	@Test
	public void testCheckPending() {
		assert JobStatus.Pending == service.checkStatus(htmlService.read(), ["upload", "web-main-tests-checkout"])
	}
	
	@Test
	public void testCheckUnstable() {
		assert JobStatus.Unstable == service.checkStatus(htmlService.read(), ["upload", "ergonomics"])
	}

}