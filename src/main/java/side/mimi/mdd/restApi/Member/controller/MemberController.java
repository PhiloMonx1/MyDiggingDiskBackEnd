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
		memberService.join(dto);
		return ResponseEntity.ok().body("회원가입에 성공했습니다.");
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody MemberLoginRequestDto dto){
		String token = memberService.login(dto);
		return ResponseEntity.ok().body(token);
	}

	@GetMapping("/hello")
	public ResponseEntity<String> login(){
		return ResponseEntity.ok().body("hello");
	}
}