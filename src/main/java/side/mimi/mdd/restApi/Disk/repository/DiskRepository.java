package side.mimi.mdd.restApi.Disk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Disk.model.DiskEntity;

import java.util.List;

public interface DiskRepository extends JpaRepository<DiskEntity, Long> {
	List<DiskEntity> findAllByMemberMemberId(Long memberId);
}
