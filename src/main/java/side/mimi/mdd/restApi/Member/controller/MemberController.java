package side.mimi.mdd.restApi.Member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Member.dto.MemberJoinRequestDto;
import side.mimi.mdd.restApi.Member.dto.MemberLoginRequestDto;
import side.mimi.mdd.restApi.Member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

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
}