package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderService implements IJenkinsWebSiteReaderService {

	def read() {
		def addr       = "https://www.haus-der-religionen.ch/webdav"
		def authString = "webdav:w3d45".getBytes().encodeBase64().toString()

		def conn = addr.toURL().openConnection()
		conn.setRequestProperty( "Authorization", "Basic ${authString}" )
		if( conn.responseCode == 200 ) {
			
			def html = conn.content.text
			
			println html
			
			def feed = new XmlSlurper().parseText( html )

			// Work with the xml document
			println feed.body.h1
			

		} else {
			println "Something bad happened."
			println "${conn.responseCode}: ${conn.responseMessage}"
		}
	}
}
