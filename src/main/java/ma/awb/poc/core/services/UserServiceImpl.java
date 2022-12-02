package ma.awb.poc.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.jmapper.JMapper;

import ma.awb.poc.core.dao.repository.UserRepository;
import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;

@Transactional
@Service
public class UserServiceImpl implements IUser {

	@Autowired
	private UserRepository userRepository;

	private JMapper<UserDTO, UserVO> mapper = new JMapper<>(UserDTO.class, UserVO.class);
	private JMapper<UserVO, UserDTO> mapperInverse = new JMapper<>(UserVO.class, UserDTO.class);

	@Override
	public UserDTO getUserById(String id) {
		return mapper.getDestination(userRepository.getReferenceById(id));
	}

	@Override
	public void upsert(UserDTO dto) {
		userRepository.save(mapperInverse.getDestination(dto));
	}
}
