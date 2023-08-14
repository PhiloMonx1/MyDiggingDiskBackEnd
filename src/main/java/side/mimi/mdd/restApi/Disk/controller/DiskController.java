package side.mimi.mdd.restApi.Disk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import side.mimi.mdd.restApi.Disk.dto.request.DiskModifyRequestDto;
import side.mimi.mdd.restApi.Disk.dto.request.DiskPostRequestDto;
import side.mimi.mdd.restApi.Disk.dto.response.DiskResponseDto;
import side.mimi.mdd.restApi.Disk.service.DiskService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disks")
public class DiskController {
	private final DiskService diskService;

	/**
	 * 나의 디스크 모두 조회
	 */
	@GetMapping("/mydisks")
	public ResponseEntity<List<DiskResponseDto>> getMyDisks(@RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.getMyDisks(token));
	}

	/**
	 * 특정 디스크 조회
	 */
	@GetMapping("/{diskId}")
	public ResponseEntity<DiskResponseDto> getDiskById(@PathVariable Long diskId, @RequestHeader(name="Authorization", required = false) String token){
		return ResponseEntity.ok().body(diskService.getDiskById(diskId, token));
	}

	/**
	 * 디스크 작성
	 */
	@PostMapping("")
	public ResponseEntity<DiskResponseDto> postDisk(@RequestPart(value = "data", required = false) DiskPostRequestDto dto,
	                                                @RequestHeader(name="Authorization") String token,
	                                                @RequestPart(value = "file", required = false) MultipartFile[] files) throws IOException {
		return ResponseEntity.ok().body(diskService.postDisk(dto, token, files));
	}

	/**
	 * 디스크 수정
	 */
	@PatchMapping("/{diskId}")
	public ResponseEntity<DiskResponseDto> modifyDisk(@PathVariable Long diskId,
	                                                  @RequestPart(value = "data", required = false) DiskModifyRequestDto dto,
	                                                  @RequestHeader(name="Authorization") String token,
	                                                  @RequestPart(value = "file", required = false) MultipartFile[] files) throws IOException {
		return ResponseEntity.ok().body(diskService.modifyDisk(diskId, dto, token, files));
	}

	/**
	 * 디스크 삭제
	 */
	@DeleteMapping("/{diskId}")
	public ResponseEntity<Boolean> deleteDisk(@PathVariable Long diskId, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.deleteDisk(diskId, token));
	}

	/**
	 * 디스크 좋아요
	 */
	@PostMapping("like/{diskId}")
	public ResponseEntity<Integer> likedDisk(@PathVariable Long diskId){
		return ResponseEntity.ok().body(diskService.likedDisk(diskId));
	}

	/**
	 * 디스크 북마크
	 */
	@PostMapping("bookmark/{diskId}")
	public ResponseEntity<Boolean> bookmarkDisk(@PathVariable Long diskId, @RequestHeader(name="Authorization") String token){
		return ResponseEntity.ok().body(diskService.bookmarkDisk(diskId, token));
	}
}
