package side.mimi.mdd.restApi.Disk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Disk.dto.request.DiskPostRequestDto;
import side.mimi.mdd.restApi.Disk.dto.response.DiskResponseDto;
import side.mimi.mdd.restApi.Disk.service.DiskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disks")
public class DiskController {
	private final DiskService diskService;

	@GetMapping("/mydisks")
	public ResponseEntity<List<DiskResponseDto>> getMyDisks(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.getMyDisks(token));
	}

	@GetMapping("/{diskId}")
	public ResponseEntity<DiskResponseDto> getDiskById(@PathVariable Long diskId, @RequestHeader(name="Authorization", required = false) String token){
		return ResponseEntity.ok().body(diskService.getDiskById(diskId, token));
	}

	@PostMapping("")
	public ResponseEntity<DiskResponseDto> postDisk(@RequestBody DiskPostRequestDto dto, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.postDisk(dto, token));
	}
}
