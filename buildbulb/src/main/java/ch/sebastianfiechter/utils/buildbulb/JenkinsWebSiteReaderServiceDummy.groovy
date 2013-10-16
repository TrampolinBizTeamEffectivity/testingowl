package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderServiceDummy implements IJenkinsWebSiteReaderService {

	def read(address, user, password) {
		return new File('jenkins_html.txt').text
	}
}
