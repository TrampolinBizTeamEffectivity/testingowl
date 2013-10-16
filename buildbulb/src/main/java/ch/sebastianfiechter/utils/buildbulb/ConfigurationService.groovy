package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;

@Service
class ConfigurationService {

	def config
	
	def ConfigurationService() {
		config = new XmlSlurper().parse("configuration.xml");
		
					// Work with the xml document
					//println feed.body.h1
	}
	
}
