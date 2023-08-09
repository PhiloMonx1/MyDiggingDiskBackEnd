package side.mimi.mdd.restApi.Member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Member.model.TokenEntity;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
}