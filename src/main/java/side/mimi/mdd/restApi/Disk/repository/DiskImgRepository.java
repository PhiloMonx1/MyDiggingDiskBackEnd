package side.mimi.mdd.restApi.Disk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.mimi.mdd.restApi.Disk.model.DiskImgEntity;

public interface DiskImgRepository extends JpaRepository<DiskImgEntity, Long> {
}
