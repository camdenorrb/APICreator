package dev.twelveoclock.apicreator

import dev.twelveoclock.apicreator.cleaner.ASMOW2Cleaner
import dev.twelveoclock.apicreator.cleaner.ProGuardCleaner
import dev.twelveoclock.apicreator.cleaner.base.Cleaner
import kotlinx.cli.*
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


object Main {

	@OptIn(ExperimentalTime::class)
	@JvmStatic
	fun main(args: Array<String>) {

		val (inputPath, outputPath, cleanerSelection, options) = parseArgs(args)

		println(
			"""
			Running with {
				Input path: $inputPath
				Output path: $outputPath
				Cleaner: $cleanerSelection
				Options: $options
			}
			
			""".trimIndent()
		)

		// Delete the provided output if exists, overwrite
		outputPath.deleteIfExists()

		val cleaner = when (cleanerSelection) {
			CleanerSelection.ASM_OW2 -> ASMOW2Cleaner
			CleanerSelection.PROGUARD -> ProGuardCleaner
		}

		val time = measureTime {
			if (outputPath.extension.equals("jar", true)) {
				cleaner.cleanUpJar(inputPath, outputPath, options)
			}
			else {
				cleaner.cleanUpClass(inputPath, outputPath, options)
			}
		}

		println("Completed: $outputPath, Took: ${time.inWholeMilliseconds}ms, Output Size: ${outputPath.fileSize()} bytes")
	}


	/**
	 * Parses the options from the program arguments
	 *
	 * @param args Array<String>, The arguments used to run the program
	 * @return Args, Parsed arguments
	 */
	fun parseArgs(args: Array<String>): ParsedArgs {

		/* Parsing */

		val argsParser = ArgParser("APICreator")

		val input by argsParser.option(
			ArgType.String,
			shortName = "i",
			description = "Input .jar or .class file"
		).required()

		val output by argsParser.option(
			ArgType.String,
			shortName = "o",
			description = "Output file path (Default: API-{inputFileName})"
		)

		val overwrite by argsParser.option(
			ArgType.Boolean,
			fullName = "overwrite",
			description = "Whether or not to overwrite the output if it already exists"
		).default(false)

		val cleanerSelection by argsParser.option(
			ArgType.Choice<CleanerSelection>(),
			"cleaner",
			"c",
			"The cleaner library to use"
		).default(CleanerSelection.PROGUARD)

		val options by argsParser.option(
			ArgType.Choice<Cleaner.Option>(),
			description = "The cleaner options to use"
		).multiple().default(setOf(Cleaner.Option.KEEP_NON_CLASS_FILES, Cleaner.Option.STRIP_KOTLIN_HEADERS))

		argsParser.parse(args)

		/* Verifying */

		val inputPath = Path(input)
		val outputPath = Path(output ?: "API-${Path(input).name}")

		check(inputPath.exists()) {
			"Could not find input file for path: '${inputPath.pathString}'"
		}

		check(inputPath.extension.equals("jar", true) || inputPath.extension.equals("class", true)) {
			"The input file path provided isn't a .jar nor .class file"
		}

		if (outputPath.exists() && !overwrite) {
			error("You must use the `--overwrite` option if the output file already exists!")
		}

		return ParsedArgs(inputPath, outputPath, cleanerSelection, options.toSet())
	}

	/**
	 *
	 * @property inputPath Path, The path to the input file
	 * @property outputPath Path, The path to the output file
	 * @property selection CleanerSelection, The cleaner to use
	 * @property options EnumSet<Option>, The options to clean with
	 * @constructor
	 */
	data class ParsedArgs(
		val inputPath: Path,
		val outputPath: Path,
		val selection: CleanerSelection,
		val options: Set<Cleaner.Option>
	)

	/**
	 * The types of cleaners to choose from
	 */
	enum class CleanerSelection {
		ASM_OW2,
		PROGUARD,
	}

}