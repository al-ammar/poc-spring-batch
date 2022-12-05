package ma.awb.poc.batch.writer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ma.awb.poc.core.dao.vo.EventBatchVO;
import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.EventBatchDTO;
import ma.awb.poc.core.model.UserDTO;
import ma.awb.poc.core.services.IEventBatch;

@Component
public class EventBatchItemWriter implements ItemWriter<EventBatchVO> {

	private static final Logger log = LoggerFactory.getLogger(EventBatchItemWriter.class);
	private static final String SEPARATOR = ",";

	@Value("${poc.directory.input}")
	private String path;

	@Autowired
	private IEventBatch services;

	private String fileName;

	public EventBatchItemWriter() {
	}
	
	public EventBatchItemWriter(String fileName) {
		this.fileName = fileName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(final List<? extends EventBatchVO> items) throws Exception {
		Objects.requireNonNull(items, "items ne doit pas être null");
		final Path pathFile;
		if (!Files.exists(Paths.get(path, fileName))) {
			pathFile = Files.createFile(Paths.get(path, fileName));
			log.debug("[File] creation by Writer {}", pathFile);

		} else {
			pathFile = Paths.get(path, fileName);
			log.debug("[File] already exist {}", pathFile);
		}

		final Iterator<EventBatchVO> iterator = (Iterator<EventBatchVO>) items.iterator();
		while (iterator.hasNext()) {
			try {
				final EventBatchVO dto = iterator.next();
				writeToFiles(dto, pathFile);
				EventBatchDTO u = services.getEventBatchById(dto.getId());
				u.setTreated(true);
				services.upsert(u);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void writeToFiles(final EventBatchVO item, final Path pathFile) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(item.getId()).append(SEPARATOR).append(item.getKey()).append(SEPARATOR)
				.append(item.getValue()).append(SEPARATOR).append(item.getType())
				.append(System.lineSeparator());
		try {
			log.debug("[File] writing by Writer {}", pathFile);
			Files.write(pathFile, builder.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			log.error("Erreur lors de l'écriture", e);
			throw e;
		}
	}

}
