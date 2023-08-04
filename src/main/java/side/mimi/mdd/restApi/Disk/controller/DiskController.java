package side.mimi.mdd.restApi.Disk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Disk.dto.request.DiskPostRequestDto;
import side.mimi.mdd.restApi.Disk.dto.response.DiskResponseDto;
import side.mimi.mdd.restApi.Disk.service.DiskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disks")
public class DiskController {
	private final DiskService diskService;

	@PostMapping("")
	public ResponseEntity<DiskResponseDto> postDisk(@RequestBody DiskPostRequestDto dto, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.postDisk(dto, token));
	}
}
