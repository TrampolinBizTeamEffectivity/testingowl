package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderService implements IJenkinsWebSiteReaderService {

	
	def read(address, user=null, password=null) {
		
		def conn = address.toURL().openConnection()
		if (user?.trim() && password?.trim()) {
			def authString = "${user}:${password}".getBytes().encodeBase64().toString()
			conn.setRequestProperty( "Authorization", "Basic ${authString}" )
		}
		if( conn.responseCode == 200 ) {
			
			return conn.content.text
		
		} else {
			println "Something bad happened."
			println "${conn.responseCode}: ${conn.responseMessage}"
		}
	}
}
