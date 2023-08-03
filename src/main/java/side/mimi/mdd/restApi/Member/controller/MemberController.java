package side.mimi.mdd.restApi.Member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Member.dto.request.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.request.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.dto.response.MemberResponseDto;
import side.mimi.mdd.restApi.Member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/mypage")
	public ResponseEntity<MemberResponseDto> getMyPage(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.getMyPage(token));
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long memberId, @RequestHeader(name="Authorization", required = false) String token){
		return ResponseEntity.ok().body(memberService.getMember(memberId, token));
	}

	@GetMapping("/check/{memberName}")
	public ResponseEntity<Boolean> checkMemberName(@PathVariable String memberName){
		return ResponseEntity.ok().body(memberService.checkMemberName(memberName));
	}

	@GetMapping("/check/nick/{nickname}")
	public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname){
		return ResponseEntity.ok().body(memberService.checkNickname(nickname));
	}

	@PostMapping("/join")
	public ResponseEntity<String> join(@RequestBody MemberJoinRequestDto dto){
		String token = memberService.join(dto);
		return ResponseEntity.ok().body(token);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody MemberLoginRequestDto dto){
		String token = memberService.login(dto);
		return ResponseEntity.ok().body(token);
	}

	@DeleteMapping("")
	public ResponseEntity<Boolean> removeMember(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(memberService.removeMember(token));
	}
}