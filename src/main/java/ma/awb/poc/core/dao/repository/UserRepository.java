package ma.awb.poc.core.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.awb.poc.core.dao.vo.UserVO;

@Repository
public interface UserRepository extends JpaRepository<UserVO, String> {

}
