package ch.sebastianfiechter.testingowl

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

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
import org.apache.xmlbeans.impl.xb.ltgfmt.impl.TestCaseImpl.FilesImpl;

@Component
@Slf4j
class FilePacker {
	
	FileChannel channel

	long currentPosition
	
	def fileNameWithoutEnding

	/**
	 * no compression
	 *
	 * @param filenameWithoutFileEnding
	 * @return
	 */
	def pack() {

		assert fileNameWithoutEnding != null
		
		FileOutputStream fos = new FileOutputStream(
				"${fileNameWithoutEnding}.cap.owl")

		channel = fos.getChannel()

		currentPosition = 0

		addFile("${fileNameWithoutEnding}.cap")
		addFile("${fileNameWithoutEnding}.cap.xlsx")
		addFile("${fileNameWithoutEnding}.cap.wav")

		channel.close()
		fos.close()
	}

	def addFile(def fileName) {

		FileInputStream fis = new FileInputStream(fileName)

		FileChannel fic = fis.getChannel()
		//first write file size to channel
		writeFileSize(fic)
		//now write the data
		channel.transferFrom(fic, currentPosition, fic.size())
		currentPosition += fic.size()
		fic.close()
		fis.close()
	}

	def writeFileSize(FileChannel fic) {

		def fsrbc = new FileSizeReadableByteChannel(fic.size())
		log.info("write file size: " + fsrbc.fileSize)

		channel.transferFrom(fsrbc, currentPosition, fsrbc.size())

		currentPosition += fsrbc.size()
	}


	def unpack() {

		assert fileNameWithoutEnding != null
		
		FileInputStream fis = new FileInputStream(
				"${fileNameWithoutEnding}.cap.owl");

		channel = fis.getChannel()

		currentPosition = 0
		
		extractFileIfNotYetExists("${fileNameWithoutEnding}.cap")
		extractFileIfNotYetExists("${fileNameWithoutEnding}.cap.xlsx")
		extractFileIfNotYetExists("${fileNameWithoutEnding}.cap.wav")

		channel.close()
		fis.close()
	}

	def extractFileIfNotYetExists(def fileName) {

		if (new File(fileName).exists() == true) {
			return
		}
		
		FileOutputStream fos = new FileOutputStream(fileName)
		FileChannel foc = fos.getChannel()

		def fileSize = readFileSize()
		log.info("read file size: " + fileSize)
		channel.transferTo(currentPosition, fileSize, foc)
		currentPosition += fileSize

		foc.close()
		fos.close()
	}

	long readFileSize() {
		def fswbc = new FileSizeWritableByteChannel()
		channel.transferTo(currentPosition, fswbc.size(), fswbc)
		currentPosition += fswbc.size()
		return fswbc.fileSize
	}


	class FileSizeReadableByteChannel implements ReadableByteChannel {

		long fileSize
		boolean read


		public FileSizeReadableByteChannel(long fileSiz) {
			fileSize = fileSiz
			read = false
		}

		@Override
		public void close() throws IOException {
			//do nothing
		}

		@Override
		public boolean isOpen() {
			return !read
		}

		@Override
		public int read(ByteBuffer buffer) throws IOException {

			buffer.putLong(fileSize);
			/*
			 buffer.put(((fileSize &  0xFF000000) >>> 24) as byte)
			 buffer.put(((fileSize &  0x00FF0000) >>> 16) as byte)
			 buffer.put(((fileSize &  0x0000FF00) >>> 8) as byte)
			 buffer.put((fileSize &  0x000000FF) as byte)
			 */
			read = true

			return 8;
		}

		long size() {
			return 8
		}
	}

	class FileSizeWritableByteChannel implements WritableByteChannel {

		long fileSize
		boolean written


		public FileSizeWritableByteChannel() {
			written = false
		}

		@Override
		public void close() throws IOException {
			//do nothing
		}

		@Override
		public boolean isOpen() {
			return !written
		}


		@Override
		public int write(ByteBuffer buffer) throws IOException {


			/*
			 fileSize = buffer.get()
			 fileSize = fileSize << 8
			 fileSize += buffer.get()
			 fileSize = fileSize << 8
			 fileSize += buffer.get()
			 fileSize = fileSize << 8
			 fileSize += buffer.get()
			 */

			fileSize = buffer.getLong()
			written = true
			return 8;
		}

		long size() {
			return 8
		}
	}
}
