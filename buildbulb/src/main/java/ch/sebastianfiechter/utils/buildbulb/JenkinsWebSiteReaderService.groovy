package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class JenkinsWebSiteReaderService implements IJenkinsWebSiteReaderService {

	
	def read(address, user=null, password=null) {
		
		def conn = address.toURL().openConnection()
		if (user != null && user.toString().trim().length()>0 && password != null) {
			def authString = "${user}:${password}".getBytes().encodeBase64().toString()
			conn.setRequestProperty( "Authorization", "Basic ${authString}" )
		}
		if( conn.responseCode == 200 ) {
			
			return conn.content.text
		
		} else {
			throw new Exception("Couldnt get REST XML from jenkins: ${conn.responseCode}: ${conn.responseMessage}")
		}
	}
}
