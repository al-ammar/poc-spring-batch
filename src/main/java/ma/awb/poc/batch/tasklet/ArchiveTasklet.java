package ma.awb.poc.batch.tasklet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ArchiveTasklet implements Tasklet {

	private static final Logger logger = LoggerFactory.getLogger(ArchiveTasklet.class);

	@Value("${poc.directory.input}")
	private String path;

	@Value("${poc.directory.output}")
	private String pathOutput;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info(">>>>> Début execution Tasklet At {}", new Date());
		Path pathOutPut = Paths.get(pathOutput, "output");
		if (Files.exists(pathOutPut)) {
			logger.info("File already exist");
//			Files.deleteIfExists(pathOutPut);
		} else {
			Files.createFile(pathOutPut);
		}
		try (Stream<Path> paths = Files.list(Paths.get(path))) {
			paths.forEach(p -> {
				logger.info("File loaded {}", p.toString());
				try (Stream<String> lines = Files.lines(p)) {
					lines.forEach(line -> {
						try {
							Files.write(pathOutPut, new StringBuilder().append(line).append(System.lineSeparator())
									.toString().getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e) {
							logger.error("Error writing on file {}", p.toString(), e);
						}
					});
				} catch (IOException e) {
					logger.error("Error reading on file {}", p.toString(), e);
				}
			});
		}
		logger.info("<<<<< Fin execution Tasklet At {}", new Date());
		return RepeatStatus.FINISHED;
	}

}
