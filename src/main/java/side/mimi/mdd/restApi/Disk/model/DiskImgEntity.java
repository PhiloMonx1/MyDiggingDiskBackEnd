package side.mimi.mdd.restApi.Disk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.mimi.mdd.utils.BaseEntity;

@Builder
@Entity
@Table(name="DISK_IMG")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DiskImgEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMG_ID")
	private Long imgId;

	@Column(name = "IMG_URL", nullable = false, unique = true)
	private String imgUrl;

	@Column(name = "IMG_STATUS")
	private Boolean img_status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "DISK_ID")
	private DiskEntity disk;
}
