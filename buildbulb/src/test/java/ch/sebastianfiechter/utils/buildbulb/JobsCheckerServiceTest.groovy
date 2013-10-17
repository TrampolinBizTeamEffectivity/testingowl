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
		assert true == service.checkSuccessfull(htmlService.read(), ["A7A_ZPV_BATCH_Ahvn13_Ruecklieferung_AlleStpfl"])
	}
	
	@Test
	public void testCheckDisabled() {
		assert true == service.checkSuccessfull(htmlService.read(), ["A7A ZPV Webservices R8.1"])
	}
	
	@Test
	public void testRegex() {
		def htmlSource = htmlService.read()
		htmlSource = ''' Success.
k>s>d>jf</A7A_ZPV_BATCH_Ahvn13_Ruecklieferung_AlleStpfl> '''
		println htmlSource
		assert htmlSource ==~ '(?s).*Success([^>]*>){3}[^>]*'+"A7A_ZPV_BATCH_Ahvn13_Ruecklieferung_AlleStpfl"+'.*' //single line mode //between the word Success and the job name, are three >
		/*([^>]*>){3}[^>]*
		if (htmlSource =~ 'Success.*A7A') {
			
		} else {
			fail("failed!!")
		}
		*/
	}

}