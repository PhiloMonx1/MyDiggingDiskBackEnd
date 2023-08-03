package side.mimi.mdd.restApi.Member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.restApi.Member.dto.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.restApi.Member.repository.MemberRepository;
import side.mimi.mdd.utils.JwtUtil;
import side.mimi.mdd.utils.RegexUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder encoder;

	@Value("${jwt.secret}")
	private String secretKey;
	private Long expireTimeMs = 1000 * 60 * 60 * 24L;

	public String join(MemberJoinRequestDto dto){
		if(dto.getMemberName().isEmpty() || dto.getPassword().isEmpty() || dto.getNickname().isEmpty()){
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, ": MemberName, password, nickname은 필수 값 입니다.");
		}
		String memberName = dto.getMemberName().toLowerCase();

		if(memberName.length() > 20 || !RegexUtils.isAlphanumeric(memberName)){
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, ": MemberName은 20자 이하의 영어와 숫자로만 이루어져 있어야 합니다.");
		}

		if(dto.getPassword().length() != 6 || !RegexUtils.isNumeric(dto.getPassword())){
			throw new AppException(ErrorCode.WRONG_PASSWORD_VALID, ": password는 6자리 숫자로만 이루어져 있어야 합니다.");
		}

		if(dto.getNickname().length() > 10){
			throw new AppException(ErrorCode.WRONG_NICKNAME_VALID, ": nickname은 10자를 초과할 수 없습니다.");
		}

		if(dto.getIntroduce().length() > 30){
			throw new AppException(ErrorCode.WRONG_INTRODUCE_VALID, ": introduce는 30자를 초과할 수 없습니다.");
		}

		memberRepository.findByMemberName(memberName)
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NAME_DUPLICATED, ": 이미 사용중인 MemberName 입니다.");});

		memberRepository.findByNickname(dto.getNickname())
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, ": 이미 사용중인 nickname 입니다.");});

		memberRepository.save(MemberEntity.builder()
				.memberName(memberName)
				.password(encoder.encode(dto.getPassword()))
				.nickname(dto.getNickname())
				.introduce(dto.getIntroduce())
				.build());

		return  JwtUtil.createToken(dto.getMemberName(), secretKey, expireTimeMs);
	}

	public String login(MemberLoginRequestDto dto) {
		MemberEntity selectedMember = memberRepository.findByMemberName(dto.getMemberName())
				.orElseThrow(() ->new AppException(ErrorCode.MEMBER_NAME_NOT_FOUND, ": 찾을 수 없는 memberName 입니다."));

		if(!encoder.matches(dto.getPassword(), selectedMember.getPassword())){
			throw new AppException(ErrorCode.INVALID_PASSWORD, ": 비밀번호가 일치하지 않습니다.");
		}

		return JwtUtil.createToken(selectedMember.getMemberName(), secretKey, expireTimeMs);
	}
}