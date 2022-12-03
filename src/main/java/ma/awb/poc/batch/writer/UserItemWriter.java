package ma.awb.poc.batch.writer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ma.awb.poc.core.dao.repository.UserRepository;
import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.services.IUser;

@Component
public class UserItemWriter implements ItemWriter<UserVO> {

	private static final Logger log = LoggerFactory.getLogger(UserItemWriter.class);

	@Value("${poc.directory.input}")
	private String path;

	@Autowired
	private IUser services;

	@Autowired
	private UserRepository repository;

	private static final String SEPARATOR = ",";

	@SuppressWarnings("unchecked")
	@Override
	public void write(final List<? extends UserVO> items) throws Exception {
		Objects.requireNonNull(items, "items ne doit pas être null");
		final Path pathFile = Files.createFile(Paths.get(path, "USER" + UUID.randomUUID().toString()));
		log.debug("[File] creation by Writer {}", pathFile);
		final Iterator<UserVO> iterator = (Iterator<UserVO>) items.iterator();
		while (iterator.hasNext()) {
			try {
				final UserVO dto = iterator.next();
				writeToFiles(dto, pathFile);
				UserVO u = repository.getReferenceById(dto.getId());
//				services.upsert(u);
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
