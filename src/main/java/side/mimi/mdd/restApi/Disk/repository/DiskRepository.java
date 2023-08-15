package side.mimi.mdd.restApi.Disk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import side.mimi.mdd.restApi.Disk.model.DiskEntity;

import java.util.List;

public interface DiskRepository extends JpaRepository<DiskEntity, Long> {
	List<DiskEntity> findAllByMemberMemberIdOrderByIsBookmarkDesc(Long memberId);
	List<DiskEntity> findAllByMemberMemberIdAndIsBookmarkNotNullOrderByIsBookmarkDesc(Long memberId);
	@Query("SELECT SUM(d.likeCount) FROM DiskEntity d WHERE d.member.memberId = :memberId")
	Integer getTotalLikesByMemberId(Long memberId);
}
