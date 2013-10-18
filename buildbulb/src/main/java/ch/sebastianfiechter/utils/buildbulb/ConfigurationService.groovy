package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;

@Service
class ConfigurationService {

	def config
	
	def ConfigurationService() {
		readConfig();
		
	}
	
	def readConfig() {
		def pathToApp = new File(".").getCanonicalPath()
		config = new XmlSlurper().parse(pathToApp+"\\configuration.xml");
	}
	
}
