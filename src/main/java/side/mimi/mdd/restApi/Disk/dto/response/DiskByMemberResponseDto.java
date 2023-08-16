package side.mimi.mdd.restApi.Disk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class DiskByMemberResponseDto {
	private Long memberId;
	private String memberName;
	private String nickname;
	private Boolean isMine;
	private List<DiskResponseDto> diskList;
}
