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


	/**
	 * no compression
	 *
	 * @param filenameWithoutFileEnding
	 * @return
	 */
	def pack(String filenameWithoutFileEnding) {

		FileOutputStream fos = new FileOutputStream(
				"${filenameWithoutFileEnding}.cap.owl")

		channel = fos.getChannel()

		currentPosition = 0

		addFile("${filenameWithoutFileEnding}.cap")
		addFile("${filenameWithoutFileEnding}.cap.xlsx")
		addFile("${filenameWithoutFileEnding}.cap.wav")

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


	def unpack(String filenameWithFileEnding) {

		FileInputStream fis = new FileInputStream(
				"${filenameWithFileEnding}");

		channel = fis.getChannel()

		currentPosition = 0

		extractFile("${filenameWithFileEnding}.cap")
		extractFile("${filenameWithFileEnding}.cap.xlsx")
		extractFile("${filenameWithFileEnding}.cap.wav")

		channel.close()
		fis.close()
	}

	def extractFile(def fileName) {

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
