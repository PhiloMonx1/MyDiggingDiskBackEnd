package side.mimi.mdd.restApi.Disk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.mimi.mdd.restApi.Disk.dto.request.DiskModifyRequestDto;
import side.mimi.mdd.restApi.Disk.dto.request.DiskPostRequestDto;
import side.mimi.mdd.restApi.Disk.dto.response.DiskResponseDto;
import side.mimi.mdd.restApi.Disk.service.DiskService;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;

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

	@PatchMapping("/{diskId}")
	public ResponseEntity<DiskResponseDto> modifyDisk(@PathVariable Long diskId, @RequestBody DiskModifyRequestDto dto, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.modifyDisk(diskId, dto, token));
	}

	@DeleteMapping("/{diskId}")
	public ResponseEntity<Boolean> deleteDisk(@PathVariable Long diskId, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.deleteDisk(diskId, token));
	}

	@PostMapping("/{diskId}")
	public ResponseEntity<Boolean> likedDisk(@PathVariable Long diskId){
		return ResponseEntity.ok().body(diskService.likedDisk(diskId));
	}
}
