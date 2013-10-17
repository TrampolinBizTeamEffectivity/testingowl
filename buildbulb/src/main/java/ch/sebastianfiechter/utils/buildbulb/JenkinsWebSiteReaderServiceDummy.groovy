package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderServiceDummy implements IJenkinsWebSiteReaderService {

	def read(address=null, user=null, password=null) {
		return new File('jenkins_html.txt').text
	}
}
