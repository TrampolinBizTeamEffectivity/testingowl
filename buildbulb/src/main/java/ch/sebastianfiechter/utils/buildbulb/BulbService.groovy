package ch.sebastianfiechter.utils.buildbulb
import org.springframework.stereotype.Service;

@Service
class BulbService implements IBulbService {

	@Override
	def showStartup() {
		executeMailNotifierCommand("-C 11 13 45 -P 5", 10000)
	}

	@Override
	def showRed() {
		executeMailNotifierCommand("-C 64 0 0", 1000)
	}

	@Override
	def showGreen() {
		executeMailNotifierCommand("-C 0 64 0", 1000)
	}

	@Override
	def switchOff() {
		executeMailNotifierCommand("-Q", 1000)
	}

	/**
	 * siehe http://stackoverflow.com/questions/159148/groovy-executing-shell-commands/159270#159270
	 * 
	 * @param command
	 * @return
	 */
	def executeMailNotifierCommand(def mailNotifierCommand, def waitForOrKill) {

		def pathToApp = new File(".").getCanonicalPath()
		def command = pathToApp+/\USB_Mail_Notifier\USBMailNotifierCmd.exe /+mailNotifierCommand
		
		def sout = new StringBuffer()
		def serr = new StringBuffer()
		def proc = command.execute()
		proc.consumeProcessOutput(sout, serr)
		proc.waitForOrKill(waitForOrKill)
		println "out> $sout err> $serr"
	}
	
}
