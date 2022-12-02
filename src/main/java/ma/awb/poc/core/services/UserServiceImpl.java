package ma.awb.poc.core.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.awb.poc.core.dao.repository.UserRepository;

@Transactional
@Service
public class UserServiceImpl implements IUser{

	private UserRepository userRepository;
	
	@Autowired
	private UserServiceImpl(UserRepository repository) {
		this.userRepository = repository;
	}
}
