package side.mimi.mdd.restApi.Disk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Disk.model.DiskEntity;

public interface DiskRepository extends JpaRepository<DiskEntity, Long> {
}
