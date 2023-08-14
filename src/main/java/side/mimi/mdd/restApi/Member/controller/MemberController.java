package side.mimi.mdd.restApi.Member.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberTokenResponseDto;
import side.mimi.mdd.restApi.Member.service.MemberService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	/**
	 * 마이페이지
	 */
	@GetMapping("/mypage")
	public ResponseEntity<MemberResponseDto> getMyPage(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.getMyPage(token));
	}

	/**
	 * 회원 단일 조회
	 */
	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long memberId, @RequestHeader(name="Authorization", required = false) String token){
		return ResponseEntity.ok().body(memberService.getMember(memberId, token));
	}

	/**
	 * 회원 계정 (memberName) 중복체크
	 */
	@GetMapping("/check/{memberName}")
	public ResponseEntity<Boolean> checkMemberName(@PathVariable String memberName){
		return ResponseEntity.ok().body(memberService.checkMemberName(memberName));
	}

	/**
	 * 닉네임 중복체크
	 */
	@GetMapping("/check/nick/{nickname}")
	public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname){
		return ResponseEntity.ok().body(memberService.checkNickname(nickname));
	}

	/**
	 * 회원가입
	 */
	@PostMapping("/join")
	public ResponseEntity<MemberTokenResponseDto> join(@RequestBody MemberJoinRequestDto dto, HttpServletResponse response){
		return ResponseEntity.ok().body(memberService.join(dto, response));
	}

	/**
	 * 로그인
	 */
	@PostMapping("/login")
	public ResponseEntity<MemberTokenResponseDto> login(@RequestBody MemberLoginRequestDto dto,  HttpServletResponse response){
		return ResponseEntity.ok().body(memberService.login(dto, response));
	}

	/**
	 * 회원 정보 수정
	 */
	@PatchMapping("")
	public ResponseEntity<MemberResponseDto> modifyMemberInfo(@RequestPart(value = "data", required = false)  MemberModifyRequestDto dto,
	                                             @RequestHeader(name="Authorization") String token,
	                                             @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
		return ResponseEntity.ok().body(memberService.modifyMemberInfo(dto, token, file));
	}

	/**
	 * 회원 탈퇴
	 */
	@DeleteMapping("")
	public ResponseEntity<Boolean> removeMember(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.removeMember(token));
	}

	/**
	 * 토큰 재발급
	 */
	@GetMapping("/reissue")
	public ResponseEntity<MemberTokenResponseDto> reissueToken(@RequestBody String refreshToken, HttpServletResponse response){
		return ResponseEntity.ok().body(memberService.reissueToken(refreshToken, response));
	}

	@GetMapping("/test/token")
	public ResponseEntity<MemberTokenResponseDto> testToken(HttpServletResponse response){
		return ResponseEntity.ok().body(memberService.testToken(response));
	}
}