package ch.sebastianfiechter.utils.buildbulb

interface IBulbService {
	def showStartup()
	def showRed() //for Failed, Disabled, Unstable, Pending
	def showGreen() //for Success
	def switchOff()
	def showError()
	
}
