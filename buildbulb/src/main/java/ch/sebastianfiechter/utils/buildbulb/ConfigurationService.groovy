package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;

@Service
class ConfigurationService {

	def config
	
	def ConfigurationService() {
		readConfig();
		
	}
	
	def readConfig() {
		config = new XmlSlurper().parse("configuration.xml");
	}
	
}
