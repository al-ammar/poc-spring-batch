package ma.awb.poc.core.services;

import ma.awb.poc.core.model.UserDTO;

public interface IUser {

	UserDTO getUserById(String id);

	void upsert(UserDTO dto);

}
