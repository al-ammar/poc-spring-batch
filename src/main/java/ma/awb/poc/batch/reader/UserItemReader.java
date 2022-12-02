package ma.awb.poc.batch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import ma.awb.poc.core.dao.vo.UserVO;

public class UserItemReader implements ItemReader<UserVO> {

	@Override
	public UserVO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return null;
	}

}
