package side.mimi.mdd.restApi.Member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.model.LoginLogEntity;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.restApi.Member.repository.LoginLogRepository;
import side.mimi.mdd.restApi.Member.repository.MemberRepository;
import side.mimi.mdd.utils.JwtUtil;
import side.mimi.mdd.utils.RegexUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final LoginLogRepository loginLogRepository;
	private final BCryptPasswordEncoder encoder;

	@Value("${jwt.secret}")
	private String secretKey;
	private Long expireTimeMs = 1000 * 60 * 60 * 24L;

	public MemberResponseDto getMyPage(String token) {
		String memberName = JwtUtil.getMemberName(token, secretKey);
		MemberEntity member = memberRepository.findByMemberName(memberName)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 맴버를 찾을 수 없습니다."));

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.introduce(member.getIntroduce())
				.isMe(true)
				.build();
	}
	public MemberResponseDto getMember(Long memberId, String token) {
		String memberName = token != null ? JwtUtil.getMemberName(token, secretKey) : null;
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 id의 맴버를 찾을 수 없습니다."));

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.introduce(member.getIntroduce())
				.isMe(memberName != null && member.getMemberName().equals(memberName))
				.build();
	}

	public boolean checkMemberName(String memberName) {
		return memberRepository.findByMemberName(memberName.toLowerCase()).isEmpty();
	}
	public boolean checkNickname(String nickname) {
		return memberRepository.findByNickname(nickname).isEmpty();
	}

	public String join(MemberJoinRequestDto dto){
		if(dto.getMemberName().isEmpty() || dto.getPassword().isEmpty() || dto.getNickname().isEmpty()){
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, "MemberName, password, nickname은 필수 값 입니다.");
		}
		String memberName = dto.getMemberName().toLowerCase();

		if(memberName.length() > 20 || !RegexUtils.isAlphanumeric(memberName)){
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, "MemberName은 20자 이하의 영어와 숫자로만 이루어져 있어야 합니다.");
		}

		if(dto.getPassword().length() != 6 || !RegexUtils.isNumeric(dto.getPassword())){
			throw new AppException(ErrorCode.WRONG_PASSWORD_VALID, "password는 6자리 숫자로만 이루어져 있어야 합니다.");
		}

		if(dto.getNickname().length() > 10){
			throw new AppException(ErrorCode.WRONG_NICKNAME_VALID, "nickname은 10자를 초과할 수 없습니다.");
		}

		if(dto.getIntroduce().length() > 30){
			throw new AppException(ErrorCode.WRONG_INTRODUCE_VALID, "introduce는 30자를 초과할 수 없습니다.");
		}

		memberRepository.findByMemberName(memberName)
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NAME_DUPLICATED, "이미 사용중인 MemberName 입니다.");});

		memberRepository.findByNickname(dto.getNickname())
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, "이미 사용중인 nickname 입니다.");});

		memberRepository.save(MemberEntity.builder()
				.memberName(memberName)
				.password(encoder.encode(dto.getPassword()))
				.nickname(dto.getNickname())
				.introduce(dto.getIntroduce())
				.build());

		return  JwtUtil.createToken(dto.getMemberName(), secretKey, expireTimeMs);
	}

	public String login(MemberLoginRequestDto dto) {
		MemberEntity selectedMember = memberRepository.findByMemberName(dto.getMemberName().toLowerCase())
				.orElseThrow(() ->new AppException(ErrorCode.MEMBER_NAME_NOT_FOUND, "찾을 수 없는 memberName 입니다."));

		if (isLoginOverFailed(dto.getMemberName().toLowerCase())) {
			throw new AppException(ErrorCode.OVER_INVALID_PASSWORD, "너무 많은 로그인 시도를 했습니다. 잠시 후 다시 시도해주세요.");
		}

		if(!encoder.matches(dto.getPassword(), selectedMember.getPassword())){

			LoginLogEntity loginLog = LoginLogEntity.builder()
					.memberName(dto.getMemberName().toLowerCase())
					.state(false)
					.build();

			loginLogRepository.save(loginLog);

			throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
		}

		LoginLogEntity loginLog = LoginLogEntity.builder()
				.memberName(selectedMember.getMemberName().toLowerCase())
				.state(true)
				.build();

		loginLogRepository.save(loginLog);

		return JwtUtil.createToken(selectedMember.getMemberName(), secretKey, expireTimeMs);
	}


	private boolean isLoginOverFailed(String memberName) {
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<LoginLogEntity> loginLogPage = loginLogRepository.findByMemberName(memberName, pageRequest);

		LocalDateTime currentTime = LocalDateTime.now();

		return loginLogPage.getNumberOfElements() == 5 &&
				loginLogPage.getContent().stream().allMatch(log -> !log.isState()) &&
				ChronoUnit.MINUTES.between(loginLogPage.getContent().get(0).getCreatedAt(), currentTime) < 1;
	}
}