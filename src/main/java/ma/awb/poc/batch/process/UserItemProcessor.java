package ma.awb.poc.batch.process;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;

@Component
public class UserItemProcessor implements ItemProcessor<UserVO, UserDTO> {

	@Override
	public UserDTO process(UserVO item) throws Exception {
		System.out.println(item);
		return null;
	}
}
