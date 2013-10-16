package ch.sebastianfiechter.utils.buildbulb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.ContextConfiguration

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class JobsCheckerServiceTest {

	@Autowired
	JobsCheckerService service
	
	@Autowired
	@Qualifier("jenkinsWebSiteReaderServiceDummy")
	IJenkinsWebSiteReaderService htmlService
	
	@Test
	public void testCheckSuccessfull() {
		assert true == service.checkSuccessfull(htmlService.read(null,  null, null), ["A7A_ZPV_BATCH_Ahvn13_Ruecklieferung_AlleStpfl", "A5A_Outsourcing_BlueBull_Branch_0.3"])
	}

}