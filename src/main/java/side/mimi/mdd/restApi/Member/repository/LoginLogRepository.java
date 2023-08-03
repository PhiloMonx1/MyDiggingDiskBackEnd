package side.mimi.mdd.restApi.Member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Member.model.LoginLogEntity;

public interface LoginLogRepository extends JpaRepository<LoginLogEntity, Long> {
	Page<LoginLogEntity> findByMemberName(String memberName, Pageable pageable);
}