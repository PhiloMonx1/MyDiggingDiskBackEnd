package side.mimi.mdd.restApi.Member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberTokenResponseDto {
	MemberResponseDto memberInfo;
	String accessToken;
	String refreshToken;
}
