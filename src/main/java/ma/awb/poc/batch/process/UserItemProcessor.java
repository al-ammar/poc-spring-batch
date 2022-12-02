package ma.awb.poc.batch.process;

import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.googlecode.jmapper.JMapper;

import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;

@Component
public class UserItemProcessor implements ItemProcessor<UserVO, UserDTO> {

	private JMapper<UserDTO, UserVO> mapper = new JMapper<>(UserDTO.class, UserVO.class);

	@Override
	public UserDTO process(UserVO item) throws Exception {
		Objects.requireNonNull(item, "item ne doit pas etre null");
		final UserDTO dto = new UserDTO();
		mapper.getDestination(dto, item);
		return dto;
	}
}
