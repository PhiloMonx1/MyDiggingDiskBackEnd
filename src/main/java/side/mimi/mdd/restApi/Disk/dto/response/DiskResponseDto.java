package side.mimi.mdd.restApi.Disk.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import side.mimi.mdd.restApi.Disk.dto.DiskImgDto;
import side.mimi.mdd.restApi.Disk.model.enums.DiskColorEnum;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class DiskResponseDto {
	private Long diskId;
	private String diskName;
	private String content;
	private DiskColorEnum diskColor;
	private Boolean isPrivate;
	private Boolean isBookmark;
	private Integer likeCount;
	private Long diskOwnerId;
	private String diskOwnerNickname;
	private Boolean isMine;
	private List<DiskImgDto> image;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifiedAt;
}
