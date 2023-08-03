package side.mimi.mdd.restApi.Member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberModifyRequestDto {
	private String nickname;
	private String introduce;
}