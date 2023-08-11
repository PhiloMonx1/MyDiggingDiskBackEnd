package side.mimi.mdd.restApi.Disk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.mimi.mdd.restApi.Disk.dto.request.DiskModifyRequestDto;
import side.mimi.mdd.restApi.Disk.model.enums.DiskColorEnum;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.utils.BaseEntity;

import java.time.LocalDateTime;

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

	@Column(name = "IS_BOOKMARK")
	private LocalDateTime isBookmark;

	@Column(name = "LIKE_COUNT")
	private Integer likeCount;

	//TODO : diskImgIdList 추가

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "MEMBER_ID")
	private MemberEntity member;

	public void modifyDisk(DiskModifyRequestDto dto){
		if(dto.getDiskName() != null && !dto.getDiskName().isEmpty()) diskName = dto.getDiskName();
		if(dto.getContent() != null && !dto.getContent().isEmpty()) content = dto.getContent();
		if(dto.getDiskColor() != null) diskColor = dto.getDiskColor();
		if(dto.getIsPrivate() != null) isPrivate = dto.getIsPrivate();
		if(dto.getIsBookmark() != null && dto.getIsBookmark()) isBookmark = LocalDateTime.now();
		if(dto.getIsBookmark() != null && !dto.getIsBookmark()) isBookmark = null;
	}

	public void likedDisk(){
		likeCount ++;
	}

	public void bookmarkDisk(){
		if(isBookmark == null) isBookmark = LocalDateTime.now();
		else isBookmark = null;
	}
}
