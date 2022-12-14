package ma.awb.poc.batch.tasklet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ma.awb.poc.core.util.DateUtil;

@Component
public class ArchiveTasklet implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(ArchiveTasklet.class);
	private static final String OUTPUT_FILE = "output";
	private static final String ERROR_FILE = "error";

	@Value("${poc.directory.input}")
	private String pathInput;

	@Value("${poc.directory.output}")
	private String pathOutput;

	@Value("${poc.directory.errors}")
	private String pathOutputError;

	@Value("${poc.directory.archive}")
	private String pathArchive;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info(">>>>> Début execution Tasklet At {}", new Date());
		Path pathArchiveData = Paths.get(pathArchive, "data");
		if (!Files.isDirectory(pathArchiveData)) {
			Files.createDirectories(pathArchiveData);
		}
		if (Files.exists(Paths.get(pathOutput, OUTPUT_FILE))) {
			File archive = new File(pathArchive, OUTPUT_FILE + DateUtil.format(DateUtil.FORMAT_DATE_FILE_RESULT));
			File output = new File(pathOutput, OUTPUT_FILE);
			// on archive les fichiers de données, le fichier résultat doit regrouper toutes
			// les données traités
			Files.move(output.toPath(), archive.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		final Path pathOutPath = createIfNotExist(pathOutput, OUTPUT_FILE);
		try (Stream<Path> paths = Files.list(Paths.get(pathInput))) {
			paths.forEach(p -> {
				logger.debug("File loaded {}", p);
				try (Stream<String> lines = Files.lines(p)) {
					lines.forEach(line -> {
						try {
							Files.write(pathOutPath, new StringBuilder().append(line).append(System.lineSeparator())
									.toString().getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e) {
							// If Error occurs when writing on file
							logger.error("Error writing on file {}", pathInput, e);
							handleError(pathOutput, line);
						}
					});
				} catch (IOException e) {
					logger.error("Error reading on file {}", p, e);
					handleError(pathInput, StringUtils.EMPTY);
				}
				try {
					File archiveData = new File(pathArchiveData.toString(),
							p.getFileName().toString() + DateUtil.format(DateUtil.FORMAT_DATE_FILE_RESULT));
					Files.move(p, archiveData.toPath());
				} catch (IOException e) {
					logger.error("Error reading on file {}", p, e);
					handleError(pathInput, StringUtils.EMPTY);
				}
			});
		}
		logger.info("<<<<< Fin execution Tasklet At {}", new Date());
		return RepeatStatus.FINISHED;
	}

	private Path createIfNotExist(String path, String fileName) throws IOException {
		Path pathOutPut = Paths.get(path, fileName);
		if (Files.exists(pathOutPut)) {
			logger.debug("File already exist {}", pathOutPut);
			Files.deleteIfExists(pathOutPut);
		} else {
			try {
				Files.createFile(pathOutPut);
			} catch (IOException e) {
				logger.error("Error creating on file {}", pathOutPut, e);
				throw e;
			}
		}
		return pathOutPut;
	}

	private void handleError(final String path, final String line) {
		try {
			final Path errorFile = createIfNotExist(pathOutputError, ERROR_FILE);
			Files.write(errorFile,
					new StringBuilder().append(line).append(System.lineSeparator()).toString().getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			logger.error("Error writing on error file {}", path, e);
		}
	}
}
