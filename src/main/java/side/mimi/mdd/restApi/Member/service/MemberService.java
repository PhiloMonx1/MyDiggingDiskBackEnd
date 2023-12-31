package side.mimi.mdd.restApi.Member.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.restApi.Disk.repository.DiskRepository;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberTokenResponseDto;
import side.mimi.mdd.restApi.Member.model.LoginLogEntity;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.restApi.Member.model.TokenEntity;
import side.mimi.mdd.restApi.Member.repository.LoginLogRepository;
import side.mimi.mdd.restApi.Member.repository.MemberRepository;
import side.mimi.mdd.restApi.Member.repository.TokenRepository;
import side.mimi.mdd.utils.CombineRandomNickname;
import side.mimi.mdd.utils.JwtUtil;
import side.mimi.mdd.utils.RegexUtils;
import side.mimi.mdd.utils.S3Util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final LoginLogRepository loginLogRepository;
	private final DiskRepository diskRepository;
	private final TokenRepository tokenRepository;
	private final BCryptPasswordEncoder encoder;
	private final S3Util s3Util;
	private final CombineRandomNickname generator = new CombineRandomNickname();


	/**
	 * 마이페이지
	 */
	public MemberResponseDto getMyPage(String token) {
		MemberEntity member = getMemberByJwt(token);

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.interest(member.getInterest())
				.introduce(member.getIntroduce())
				.visitCount(member.getVisitCount())
				.likeCount(member.getLikeCount())
				.profileImg(member.getProfileImg())
				.isMe(true)
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();
	}

	/**
	 * 회원 단일 조회
	 */
	public MemberResponseDto getMember(Long memberId, String token) {

		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, ErrorCode.NOT_FOUND_MEMBER.getMessage()));

		MemberEntity memberByJwt = getMemberByJwt(token);

		//조회수 증가
		if(memberByJwt == null || (memberByJwt != null && !member.getMemberName().equals(memberByJwt.getMemberName()))) viewCntIncrease(member);

		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.interest(member.getInterest())
				.introduce(member.getIntroduce())
				.visitCount(member.getVisitCount())
				.profileImg(member.getProfileImg())
				.likeCount(member.getLikeCount())
				.isMe(memberByJwt != null && member.getMemberName().equals(memberByJwt.getMemberName()))
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();
	}

	/**
	 * 회원 계정 (memberName) 중복체크
	 */
	public boolean checkMemberName(String memberName) {
		return memberRepository.findByMemberName(memberName.toLowerCase().replaceAll(" ", "")).isEmpty();
	}

	/**
	 * 닉네임 중복체크
	 */
	public boolean checkNickname(String nickname) {
		return memberRepository.findByNickname(nickname.replaceAll(" ", "")).isEmpty();
	}

	/**
	 * 회원가입
	 */
	public MemberTokenResponseDto join(MemberJoinRequestDto dto, HttpServletResponse response){
		if(dto.getMemberName().isEmpty() || dto.getPassword().isEmpty()) throw new AppException(ErrorCode.EMPTY_JOIN_REQUEST, ErrorCode.EMPTY_JOIN_REQUEST.getMessage());
		String memberName = dto.getMemberName().toLowerCase().replaceAll(" ", "");
		String nickname = generator.getRandomNickname();

		if(memberName.length() < 8 || memberName.length() > 20 || !RegexUtils.isAlphanumeric(memberName))
			throw new AppException(ErrorCode.WRONG_MEMBER_NAME_VALID, ErrorCode.WRONG_MEMBER_NAME_VALID.getMessage());
		if(dto.getPassword().length() != 6 || !RegexUtils.isNumeric(dto.getPassword()))
			throw new AppException(ErrorCode.WRONG_PASSWORD_VALID, ErrorCode.WRONG_PASSWORD_VALID.getMessage());

		memberRepository.findByMemberName(memberName)
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NAME_DUPLICATED, ErrorCode.MEMBER_NAME_DUPLICATED.getMessage());});

		for (int i = 0; i < 10; i++) {
			if(memberRepository.findByNickname(nickname).isPresent()) {
				nickname = generator.getRandomNickname();
			}else break;
		}

		if(memberRepository.findByNickname(nickname).isPresent()) {
			String currentTimeMillisStr = String.valueOf(System.currentTimeMillis());
			nickname = currentTimeMillisStr.substring(currentTimeMillisStr.length() - 10);
		}

		memberRepository.findByNickname(nickname)
				.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, ErrorCode.MEMBER_NICKNAME_DUPLICATED.getMessage());});


		MemberEntity member = MemberEntity.builder()
				.memberName(memberName)
				.password(encoder.encode(dto.getPassword()))
				.nickname(nickname)
				.interest("")
				.introduce("")
				.visitCount(0)
				.likeCount(0)
				.profileImg("")
				.build();

		memberRepository.save(member);

		MemberResponseDto memberResponseDto = MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.interest(member.getInterest())
				.introduce(member.getIntroduce())
				.visitCount(member.getVisitCount())
				.likeCount(member.getLikeCount())
				.profileImg(member.getProfileImg())
				.isMe(true)
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();

		String accessToken = JwtUtil.createAccessToken(member.getMemberName());
		String refreshToken = JwtUtil.createRefreshToken(member.getMemberName() ,member.getMemberId());
		tokenRepository.save(TokenEntity.builder()
						.memberId(member.getMemberId())
						.token("Bearer " + refreshToken)
				.build());

		response.setHeader("accessToken", accessToken);
		response.setHeader("refreshToken", refreshToken);

		return  MemberTokenResponseDto.builder()
				.memberInfo(memberResponseDto)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	/**
	 * 로그인
	 */
	public MemberTokenResponseDto login(MemberLoginRequestDto dto,  HttpServletResponse response) {
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
				.interest(selectedMember.getInterest())
				.introduce(selectedMember.getIntroduce())
				.visitCount(selectedMember.getVisitCount())
				.likeCount(selectedMember.getLikeCount())
				.profileImg(selectedMember.getProfileImg())
				.isMe(true)
				.createdAt(selectedMember.getCreatedAt())
				.modifiedAt(selectedMember.getModifiedAt())
				.build();

		String accessToken = JwtUtil.createAccessToken(selectedMember.getMemberName());
		String refreshToken = JwtUtil.createRefreshToken(selectedMember.getMemberName() ,selectedMember.getMemberId());
		tokenRepository.save(TokenEntity.builder()
				.memberId(selectedMember.getMemberId())
				.token("Bearer " + refreshToken)
				.build());

		response.setHeader("accessToken", accessToken);
		response.setHeader("refreshToken", refreshToken);

		return  MemberTokenResponseDto.builder()
				.memberInfo(memberResponseDto)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}


	/**
	 * 회원 정보 수정
	 */
	public MemberResponseDto modifyMemberInfo(MemberModifyRequestDto dto, String token, MultipartFile file) throws IOException {
		if(dto != null){
			if(dto.getNickname().isEmpty() || dto.getNickname() != null && dto.getNickname().replaceAll(" ", "").length() > 10)
				throw new AppException(ErrorCode.WRONG_NICKNAME_VALID, ErrorCode.WRONG_NICKNAME_VALID.getMessage());
			if(dto.getInterest() != null && dto.getInterest().length() > 10) throw new AppException(ErrorCode.WRONG_INTEREST_VALID, ErrorCode.WRONG_INTEREST_VALID.getMessage());
			if(dto.getIntroduce() != null && dto.getIntroduce().length() > 30) throw new AppException(ErrorCode.WRONG_INTRODUCE_VALID, ErrorCode.WRONG_INTRODUCE_VALID.getMessage());
		}

		MemberEntity member = getMemberByJwt(token);

		String profileImg = member.getProfileImg();

		if(file != null){
			profileImg = s3Util.uploadFile(file);
			if(profileImg == null){
				profileImg = member.getProfileImg();
			}
		}

		if(dto.getIsDefault() != null && dto.getIsDefault() ) profileImg = "";

		if(dto != null && dto.getNickname() != null && !member.getNickname().equals(dto.getNickname())){
			memberRepository.findByNickname(dto.getNickname().replaceAll(" ", ""))
					.ifPresent(memberEntity -> {throw new AppException(ErrorCode.MEMBER_NICKNAME_DUPLICATED, ErrorCode.MEMBER_NICKNAME_DUPLICATED.getMessage());});
		}


		if(dto != null) member.modifyMemberInfo(dto, profileImg);
		memberRepository.save(member);
		return MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.interest(member.getInterest())
				.introduce(member.getIntroduce())
				.visitCount(member.getVisitCount())
				.likeCount(member.getLikeCount())
				.profileImg(member.getProfileImg())
				.isMe(true)
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();
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
	 * 토큰 재발급
	 */
	public MemberTokenResponseDto reissueToken(String refreshToken, HttpServletResponse response) {
		Long memberId = JwtUtil.verifyRefreshToken(refreshToken).getClaim("memberId").asLong();
		TokenEntity token = tokenRepository.findById(memberId)
				.orElseThrow(()-> new AppException(ErrorCode.BLACKLIST_TOKEN, ErrorCode.BLACKLIST_TOKEN.getMessage()));
		if(!token.getToken().equals(refreshToken)) throw new AppException(ErrorCode.BLACKLIST_TOKEN, ErrorCode.BLACKLIST_TOKEN.getMessage());
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, ErrorCode.NOT_FOUND_MEMBER.getMessage()));

		MemberResponseDto memberResponseDto = MemberResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.nickname(member.getNickname())
				.interest(member.getInterest())
				.introduce(member.getIntroduce())
				.visitCount(member.getVisitCount())
				.likeCount(member.getLikeCount())
				.profileImg(member.getProfileImg())
				.isMe(true)
				.createdAt(member.getCreatedAt())
				.modifiedAt(member.getModifiedAt())
				.build();

		String accessToken = JwtUtil.createAccessToken(member.getMemberName());
		if(refreshToken.startsWith("Bearer ")) refreshToken = refreshToken.split(" ")[1];

		response.setHeader("accessToken", accessToken);
		response.setHeader("refreshToken", refreshToken);

		return MemberTokenResponseDto.builder()
				.memberInfo(memberResponseDto)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	/**
	 * 맴버 좋아요
	 */
	public Integer likeMember(Long memberId) {
		MemberEntity member = memberRepository.findById(memberId)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, ErrorCode.NOT_FOUND_MEMBER.getMessage()));
		member.likeCntIncrease();
		memberRepository.save(member);
		return member.getLikeCount();
	}


	/**
	 * 서비스 로직 함수 모음
	 * 1. isLoginOverFailed = 로그인 자동 공격 방지 함수 (비밀번호 5회 틀릴 시 1분 동안 true)
	 * 2. getMemberByJwt = 토큰에서 맴버 객체 뽑기
	 * 3. viewCntIncrease = 맴버 페이지 조회 수 증가
	 * 4. getTotalLikesByMemberId = 맴버가 가진 모든 disk 좋아요 수 리턴
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

	private void viewCntIncrease(MemberEntity member){
		member.viewCntIncrease();
		memberRepository.save(member);
	}

	public Integer getTotalLikesByMemberId(Long memberId) {
		Integer totalLikes = diskRepository.getTotalLikesByMemberId(memberId);
		if (totalLikes == null) return 0;
		return totalLikes;
	}
}