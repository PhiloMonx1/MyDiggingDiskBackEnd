package side.mimi.mdd.restApi.Member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Member.model.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByMemberName(String memberName);
	Optional<MemberEntity> findByNickname(String nickname);
}