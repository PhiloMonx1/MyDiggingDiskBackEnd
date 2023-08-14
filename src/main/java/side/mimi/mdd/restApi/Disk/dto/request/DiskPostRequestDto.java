package side.mimi.mdd.restApi.Disk.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import side.mimi.mdd.restApi.Disk.model.enums.DiskColorEnum;

@AllArgsConstructor
@Getter
public class DiskPostRequestDto {
	private String diskName;
	private String content;
	private DiskColorEnum diskColor;
	private Boolean isPrivate;
	private Boolean isBookmark;
	private Boolean isTest;
}