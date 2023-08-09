package side.mimi.mdd.restApi.Member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberTokenResponseDto;
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

	/**
	 * 마이페이지
	 */
	public MemberResponseDto getMyPage(String token) {

		MemberEntity member = getMemberByJwt(token);

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.introduce(member.getIntroduce())
				.isMe(true)
				.build();
	}

	/**
	 * 회원 단일 조회
	 */
	public MemberResponseDto getMember(Long memberId, String token) {

		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, ErrorCode.NOT_FOUND_MEMBER.getMessage()));

		MemberEntity memberByJwt = getMemberByJwt(token);

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.introduce(member.getIntroduce())
				.isMe(memberByJwt != null && member.getMemberName().equals(memberByJwt.getMemberName()))
				.build();
	}

	/**
	 * 회원 계정 (memberName) 중복체크
	 */
	public boolean checkMemberName(String memberName) {
		return memberRepository.findByMemberName(memberName.toLowerCase()).isEmpty();
	}

	/**
	 * 닉네임 중복체크
	 */
	public boolean checkNickname(String nickname) {
		return memberRepository.findByNickname(nickname).isEmpty();
	}


	/**
	 * 회원가입
	 */
	public MemberTokenResponseDto join(MemberJoinRequestDto dto){

		if(dto.getMemberName().isEmpty() || dto.getPassword().isEmpty() || dto.getNickname().isEmpty()) throw new AppException(ErrorCode.EMPTY_JOIN_REQUEST, ErrorCode.EMPTY_JOIN_REQUEST.getMessage());
		String memberName = dto.getMemberName().toLowerCase();

		if(memberName.length() > 20 || !RegexUtils.isAlphanumeric(memberName))
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, ErrorCode.WRONG_MEMBER_NAME_VALID.getMessage());
		if(dto.getPassword().length() != 6 || !RegexUtils.isNumeric(dto.getPassword()))
			throw new AppException(ErrorCode.WRONG_PASSWORD_VALID, ErrorCode.WRONG_PASSWORD_VALID.getMessage());
		if(dto.getNickname().length() > 10) throw new AppException(ErrorCode.WRONG_NICKNAME_VALID, ErrorCode.WRONG_NICKNAME_VALID.getMessage());
		if(dto.getIntroduce().length() > 30) throw new AppException(ErrorCode.WRONG_INTRODUCE_VALID, ErrorCode.WRONG_INTRODUCE_VALID.getMessage());

		memberRepository.findByMemberName(memberName)
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NAME_DUPLICATED, ErrorCode.MEMBER_NAME_DUPLICATED.getMessage());});
		memberRepository.findByNickname(dto.getNickname())
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, ErrorCode.MEMBER_NICKNAME_DUPLICATED.getMessage());});

		MemberEntity member = MemberEntity.builder()
				.memberName(memberName)
				.password(encoder.encode(dto.getPassword()))
				.nickname(dto.getNickname())
				.introduce(dto.getIntroduce())
				.build();

		memberRepository.save(member);

		MemberResponseDto memberResponseDto = MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.introduce(member.getIntroduce())
				.isMe(true)
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();

		return  MemberTokenResponseDto.builder()
				.memberInfo(memberResponseDto)
				.accessToken(JwtUtil.createAccessToken(member.getMemberName()))
				.refreshToken(JwtUtil.createRefreshToken(member.getMemberId()))
				.build();
	}

	/**
	 * 로그인
	 */
	public MemberTokenResponseDto login(MemberLoginRequestDto dto) {
		MemberEntity selectedMember = memberRepository.findByMemberName(dto.getMemberName().toLowerCase())
				.orElseThrow(() ->new AppException(ErrorCode.MEMBER_NAME_NOT_FOUND, ErrorCode.MEMBER_NAME_NOT_FOUND.getMessage()));

		if (isLoginOverFailed(dto.getMemberName().toLowerCase()))
			throw new AppException(ErrorCode.OVER_INVALID_PASSWORD, ErrorCode.OVER_INVALID_PASSWORD.getMessage());


		if(!encoder.matches(dto.getPassword(), selectedMember.getPassword())){
			LoginLogEntity loginLog = LoginLogEntity.builder()
					.memberName(dto.getMemberName().toLowerCase())
					.state(false)
					.build();

			loginLogRepository.save(loginLog);
			throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
		}

		LoginLogEntity loginLog = LoginLogEntity.builder()
				.memberName(selectedMember.getMemberName().toLowerCase())
				.state(true)
				.build();

		loginLogRepository.save(loginLog);

		MemberResponseDto memberResponseDto = MemberResponseDto.builder()
				.memberId(selectedMember.getMemberId())
				.memberName(selectedMember.getMemberName())
				.nickname(selectedMember.getNickname())
				.introduce(selectedMember.getIntroduce())
				.isMe(true)
				.createdAt(selectedMember.getCreatedAt())
				.modifiedAt(selectedMember.getModifiedAt())
				.build();

		return  MemberTokenResponseDto.builder()
				.memberInfo(memberResponseDto)
				.accessToken(JwtUtil.createAccessToken(selectedMember.getMemberName()))
				.refreshToken(JwtUtil.createRefreshToken(selectedMember.getMemberId()))
				.build();
	}


	/**
	 * 회원 정보 수정
	 */
	public Long modifyMemberInfo(MemberModifyRequestDto dto, String token) {
		if(dto.getNickname().length() > 10) throw new AppException(ErrorCode.WRONG_NICKNAME_VALID, ErrorCode.WRONG_NICKNAME_VALID.getMessage());
		if(dto.getIntroduce().length() > 30) throw new AppException(ErrorCode.WRONG_INTRODUCE_VALID, ErrorCode.WRONG_INTRODUCE_VALID.getMessage());

		memberRepository.findByNickname(dto.getNickname())
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, ErrorCode.MEMBER_NICKNAME_DUPLICATED.getMessage());});

		MemberEntity member = getMemberByJwt(token);

		member.modifyMemberInfo(dto);
		memberRepository.save(member);
		return member.getMemberId();
	}

	/**
	 * 회원 탈퇴
	 */
	public boolean removeMember(String token) {
		MemberEntity member = getMemberByJwt(token);
		memberRepository.deleteById(member.getMemberId());
		return true;
	}


	/**
	 * 서비스 로직 함수 모음
	 * 1. isLoginOverFailed = 로그인 자동 공격 방지 함수 (비밀번호 5회 틀릴 시 1분 동안 true)
	 * 2. getMemberByJwt = 토큰에서 맴버 객체 뽑기
	 */
	private boolean isLoginOverFailed(String memberName) {
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<LoginLogEntity> loginLogPage = loginLogRepository.findByMemberName(memberName, pageRequest);

		LocalDateTime currentTime = LocalDateTime.now();

		return loginLogPage.getNumberOfElements() == 5 &&
				loginLogPage.getContent().stream().allMatch(log -> !log.isState()) &&
				ChronoUnit.MINUTES.between(loginLogPage.getContent().get(0).getCreatedAt(), currentTime) < 1;
	}

	public MemberEntity getMemberByJwt(String token) {
		if(token == null) return null;

		String memberName = JwtUtil.getMemberName(token);
		return memberRepository.findByMemberName(memberName)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, ErrorCode.NOT_FOUND_MEMBER.getMessage()));
	}
}