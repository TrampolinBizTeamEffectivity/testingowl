package ch.sebastianfiechter.testingowl

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.recorder.FileHelper;

import groovy.util.logging.*
import groovy.util.AntBuilder.*
import org.apache.tools.ant.dispatch.*

@Component
@Slf4j
class Zip {


	def zip(String filenameWithoutFileEnding) {


		ZipOutputStream zipFile = new ZipOutputStream(
				new FileOutputStream("${filenameWithoutFileEnding}.cap.zip"))

		addFile(zipFile, "${filenameWithoutFileEnding}.cap")
		addFile(zipFile, "${filenameWithoutFileEnding}.cap.xlsx")
		addFile(zipFile, "${filenameWithoutFileEnding}.cap.wav")

		zipFile.close()
	}

	def addFile(def zipFile, def fileName) {

		File file = new File(fileName)

		zipFile.putNextEntry(new ZipEntry(file.name))

		zipFile.write(file.getBytes());
		
		zipFile.closeEntry()

	}

	def unzip(String filenameWithFileEnding, String targetDir) {

		ZipInputStream zis= new ZipInputStream(
				new FileInputStream("${filenameWithFileEnding}"));
		// now write zip file in extracted file
		ZipEntry ze;
		byte[] buff = new byte[1024];
		while((ze=zis.getNextEntry())!=null){
			// get file name
			FileOutputStream fos= new FileOutputStream(targetDir+ze.getName());
			int l=0;
			// write buffer to file
			while((l=zis.read(buff))>0){
				fos.write(buff,0, l);
			}
		}
		zis.close();
	}
	
	def unzip(String filenameWithFileEnding) {
		
		def zipFile = new File("${filenameWithFileEnding}")

		unzip(filenameWithFileEnding, zipFile.getParent()+"\\")
		
	}
}
