package dev.twelveoclock.apicreator

import proguard.io.DataEntry
import java.io.*
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

class PathDataEntry(private val path: Path) : DataEntry {

	private var inputStream: InputStream? = null


	override fun getName(): String {
		return path.toString()
	}

	override fun getOriginalName(): String {
		return name
	}

	override fun getSize(): Long {
		return path.fileSize()
	}

	override fun isDirectory(): Boolean {
		return path.isDirectory()
	}

	override fun getInputStream(): InputStream {

		if (inputStream == null) {
			inputStream = BufferedInputStream(path.inputStream())
		}

		return inputStream!!
	}

	override fun closeInputStream() {
		inputStream?.close()
		inputStream = null
	}

	override fun getParent(): DataEntry {
		return PathDataEntry(path.parent)
	}

	override fun toString(): String {
		return name
	}

}
