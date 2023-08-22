package side.mimi.mdd.restApi.Member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	private Integer visitCount;
	private Integer likeCount;
	private String profileImg;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modifiedAt;
}
