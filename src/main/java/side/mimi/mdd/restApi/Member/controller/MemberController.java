package side.mimi.mdd.restApi.Member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberTokenResponseDto;
import side.mimi.mdd.restApi.Member.service.MemberService;

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
	public ResponseEntity<MemberTokenResponseDto> join(@RequestBody MemberJoinRequestDto dto){
		return ResponseEntity.ok().body(memberService.join(dto));
	}

	/**
	 * 로그인
	 */
	@PostMapping("/login")
	public ResponseEntity<MemberTokenResponseDto> login(@RequestBody MemberLoginRequestDto dto){
		return ResponseEntity.ok().body(memberService.login(dto));
	}

	/**
	 * 회원 정보 수정
	 */
	@PatchMapping("")
	public ResponseEntity<Long> modifyMemberInfo(@RequestBody MemberModifyRequestDto dto, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.modifyMemberInfo(dto, token));
	}

	/**
	 * 회원 탈퇴
	 */
	@DeleteMapping("")
	public ResponseEntity<Boolean> removeMember(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.removeMember(token));
	}

	@GetMapping("/reissue")
	public ResponseEntity<MemberTokenResponseDto> reissueToken(@RequestBody String refreshToken){
		return ResponseEntity.ok().body(memberService.reissueToken(refreshToken));
	}

}