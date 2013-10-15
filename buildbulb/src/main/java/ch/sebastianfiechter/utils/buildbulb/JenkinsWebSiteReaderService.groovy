package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderService implements IJenkinsWebSiteReaderService {

	def read() {
		def addr       = "https://ci.jenkins-ci.org/view/All/rssAll"
		def authString = "username:password".getBytes().encodeBase64().toString()

		def conn = addr.toURL().openConnection()
		conn.setRequestProperty( "Authorization", "Basic ${authString}" )
		if( conn.responseCode == 200 ) {
			def feed = new XmlSlurper().parseText( conn.content.text )

			// Work with the xml document

		} else {
			println "Something bad happened."
			println "${conn.responseCode}: ${conn.responseMessage}"
		}
	}
}
