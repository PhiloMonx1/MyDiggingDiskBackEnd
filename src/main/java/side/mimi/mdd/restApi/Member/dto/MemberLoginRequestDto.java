package side.mimi.mdd.restApi.Member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberLoginRequestDto {
	private String memberName;
	private String password;
}