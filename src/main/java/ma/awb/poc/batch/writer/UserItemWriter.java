package ma.awb.poc.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import ma.awb.poc.core.model.UserDTO;

public class UserItemWriter implements ItemWriter<UserDTO> {

	@Override
	public void write(List<? extends UserDTO> items) throws Exception {
		System.out.println(items);
	}

}
