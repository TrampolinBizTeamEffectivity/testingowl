package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;
import java.util.regex.*;

@Service
class JobsCheckerService {

	def checkSuccessfull(htmlSource, List jobs) {
		jobs.each {	
			def jobUrlEncoded = Pattern.quote(encodeString(it)) //space = %20	
			//single line mode //between the word Success and the job name, are three >'
			//see http://groovy.codehaus.org/Documenting+Regular+Expressions+in+Groovy
			def pattern = '(?s).*Success([^>]*>){3}[^>]*'+jobUrlEncoded+'.*'
			println pattern
			if (htmlSource ==~ pattern) {
				
			} else {
				return false
			}
		}
		
		return true
	}
	
	def encodeString(def stringToEncode){
		
		def reservedCaracters = [32:1, 33:1, 42:1, 34:1, 39:1, 40:1, 41:1, 59:1, 58:1, 64:1, 38:1, 61:1, 43:1, 36:1, 33:1, 47:1, 63:1, 37:1, 91:1, 93:1, 35:1]
		
		def encoded =  stringToEncode.collect { letter ->
		reservedCaracters[(int)letter] ? "%" +Integer.toHexString((int)letter).toString().toUpperCase() : letter
		}
		return encoded.join("")
	}
}
