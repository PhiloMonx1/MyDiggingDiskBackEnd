package side.mimi.mdd.restApi.Disk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import side.mimi.mdd.restApi.Disk.model.enums.DiskColorEnum;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class DiskResponseDto {
	private Long diskId;
	private String diskName;
	private String content;
	private DiskColorEnum diskColor;
	private Boolean isPrivate;
	private Boolean isMine;
	//TODO: 이미지///
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
