package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class BulbServiceDummy implements IBulbService {

	@Override
	def showStartup() {
		println "showStartup..."
	}

	@Override
	def showRed() {
		println "showRed..."
	}

	@Override
	def showGreen() {
		println "showGreen..."
	}

	@Override
	def switchOff() {
		println "switchingOff..."
	}

}
