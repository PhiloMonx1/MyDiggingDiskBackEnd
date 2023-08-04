package side.mimi.mdd.restApi.Disk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.mimi.mdd.restApi.Disk.model.enums.DiskColorEnum;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.utils.BaseEntity;

@Builder
@Entity
@Table(name="DISK")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DiskEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DISK_ID")
	private Long diskId;

	@Column(name = "DISK_NAME", length = 50 ,nullable = false)
	private String diskName;

	@Column(name = "CONTENT", length = 300)
	private String content;

	@Column(name = "DISK_COLOR")
	private DiskColorEnum diskColor;

	@Column(name = "IS_PRIVATE")
	private boolean isPrivate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "MEMBER_ID")
	private MemberEntity member;

}
