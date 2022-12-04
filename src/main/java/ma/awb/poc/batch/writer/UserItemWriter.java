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

import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;
import ma.awb.poc.core.services.IUser;

@Component
public class UserItemWriter implements ItemWriter<UserVO> {

	private static final Logger log = LoggerFactory.getLogger(UserItemWriter.class);

	@Value("${poc.directory.input}")
	private String path;

	@Autowired
	private IUser services;

	private static final String SEPARATOR = ",";

	private String fileName;

	public UserItemWriter() {
	}

	public UserItemWriter(String fileName) {
		this.fileName = fileName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(final List<? extends UserVO> items) throws Exception {
		Objects.requireNonNull(items, "items ne doit pas être null");
		final Path pathFile;
		if (!Files.exists(Paths.get(path, fileName))) {
			pathFile = Files.createFile(Paths.get(path, fileName));
			log.debug("[File] creation by Writer {}", pathFile);

		} else {
			pathFile = Paths.get(path, fileName);
			log.debug("[File] already exist {}", pathFile);
		}

		final Iterator<UserVO> iterator = (Iterator<UserVO>) items.iterator();
		while (iterator.hasNext()) {
			try {
				final UserVO dto = iterator.next();
				writeToFiles(dto, pathFile);
				UserDTO u = services.getUserById(dto.getId());
				u.setUpdatedBy("BATCH");
				services.upsert(u);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void writeToFiles(final UserVO item, final Path pathFile) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(item.getId()).append(SEPARATOR).append(item.getLastName()).append(SEPARATOR)
				.append(item.getFirstName()).append(SEPARATOR).append(item.getUserName())
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
