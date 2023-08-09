package side.mimi.mdd.restApi.Member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class MemberResponseDto {
	private Long memberId;
	private String memberName;
	private String nickname;
	private String interest;
	private String introduce;
	private Boolean isMe;
	//TODO: Member 소유의 disk[] 정보
	//TODO: Member 좋아요 수.
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
