package side.mimi.mdd.restApi.Disk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.restApi.Disk.dto.request.DiskModifyRequestDto;
import side.mimi.mdd.restApi.Disk.dto.request.DiskPostRequestDto;
import side.mimi.mdd.restApi.Disk.dto.response.DiskResponseDto;
import side.mimi.mdd.restApi.Disk.model.DiskEntity;
import side.mimi.mdd.restApi.Disk.repository.DiskRepository;
import side.mimi.mdd.restApi.Member.model.MemberEntity;
import side.mimi.mdd.restApi.Member.service.MemberService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiskService {
	private final DiskRepository diskRepository;
	private final MemberService memberService;

	/**
	 * 나의 디스크 모두 조회
	 */
	public List<DiskResponseDto> getMyDisks(String token) {
		//TODO : 오버패칭 유지할 것인지에 대한 판단 필요
		MemberEntity member = memberService.getMemberByJwt(token);
		List<DiskEntity> myDisks = diskRepository.findAllByMemberMemberId(member.getMemberId());

		return myDisks.stream().map(disk -> DiskResponseDto.builder()
						.diskId(disk.getDiskId())
						.diskName(disk.getDiskName())
						.content(disk.getContent())
						.diskColor(disk.getDiskColor())
						.isPrivate(disk.isPrivate())
						.isBookmark(disk.getIsBookmark() != null)
						.likeCount(disk.getLikeCount())
						.diskOwnerId(disk.getMember().getMemberId())
						.diskOwnerNickname(disk.getMember().getNickname())
						.isMine(true)
						.createdAt(disk.getCreatedAt())
						.modifiedAt(disk.getModifiedAt())
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * 특정 디스크 조회
	 */
	public DiskResponseDto getDiskById(Long diskId, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		DiskEntity disk = diskRepository.findById(diskId)
				.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		return DiskResponseDto.builder()
				.diskId(disk.getDiskId())
				.diskName(disk.getDiskName())
				.content(disk.getContent())
				.diskColor(disk.getDiskColor())
				.isPrivate(disk.isPrivate())
				.isBookmark(disk.getIsBookmark() != null)
				.likeCount(disk.getLikeCount())
				.diskOwnerId(disk.getMember().getMemberId())
				.diskOwnerNickname(disk.getMember().getNickname())
				.isMine(member != null && disk.getMember().getMemberId() == member.getMemberId())
				.createdAt(disk.getCreatedAt())
				.modifiedAt(disk.getModifiedAt())
				.build();
	}

	/**
	 * 디스크 작성
	 */
	public DiskResponseDto postDisk(DiskPostRequestDto dto, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);
		List<DiskEntity> bookmarkedDiskList = diskRepository.findAllByMemberMemberIdAndIsBookmarkNotNullOrderByIsBookmarkDesc(member.getMemberId());

		if(dto.getDiskName().length() > 30) throw new AppException(ErrorCode.OVER_LONG_DISK_NAME, ErrorCode.OVER_LONG_DISK_NAME.getMessage());
		if(dto.getContent().length() > 300) throw new AppException(ErrorCode.OVER_LONG_CONTENT, ErrorCode.OVER_LONG_CONTENT.getMessage());
		if(dto.getIsBookmark() != null && dto.getIsBookmark() && bookmarkedDiskList.size() >= 3)
			throw new AppException(ErrorCode.BOOKMARK_DISK_LIMIT, ErrorCode.BOOKMARK_DISK_LIMIT.getMessage());

		//isPrivate, isBookmark Default값 부여
		Boolean isPrivate = false;
		LocalDateTime isBookmark = null;
		if(dto.getIsPrivate() != null) isPrivate = dto.getIsPrivate();
		if(dto.getIsBookmark() != null && dto.getIsBookmark()) isBookmark = LocalDateTime.now();

		DiskEntity disk = DiskEntity.builder()
				.diskName(dto.getDiskName())
				.content(dto.getContent())
				.diskColor(dto.getDiskColor())
				.isPrivate(isPrivate)
				.isBookmark(isBookmark)
				.likeCount(0)
				.member(member)
				.build();

		diskRepository.save(disk);

		return DiskResponseDto.builder()
				.diskId(disk.getDiskId())
				.diskName(disk.getDiskName())
				.content(disk.getContent())
				.diskColor(disk.getDiskColor())
				.isPrivate(disk.isPrivate())
				.isBookmark(disk.getIsBookmark() != null)
				.likeCount(disk.getLikeCount())
				.diskOwnerId(disk.getMember().getMemberId())
				.diskOwnerNickname(disk.getMember().getNickname())
				.isMine(true)
				.createdAt(disk.getCreatedAt())
				.modifiedAt(disk.getModifiedAt())
				.build();
	}

	/**
	 * 디스크 수정
	 */
	public DiskResponseDto modifyDisk(Long diskId, DiskModifyRequestDto dto, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);
		List<DiskEntity> bookmarkedDiskList = diskRepository.findAllByMemberMemberIdAndIsBookmarkNotNullOrderByIsBookmarkDesc(member.getMemberId());

		DiskEntity myDisk = diskRepository.findById(diskId)
						.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		if(myDisk.getMember().getMemberId() != member.getMemberId()) throw new AppException(ErrorCode.NOT_DISK_OWNER, ErrorCode.NOT_DISK_OWNER.getMessage());
		if(dto.getDiskName() != null && dto.getDiskName().length() > 30) throw new AppException(ErrorCode.OVER_LONG_DISK_NAME, ErrorCode.OVER_LONG_DISK_NAME.getMessage());
		if(dto.getContent() != null && dto.getContent().length() > 300) throw new AppException(ErrorCode.OVER_LONG_CONTENT, ErrorCode.OVER_LONG_CONTENT.getMessage());

		if (bookmarkedDiskList.size() >= 3 && bookmarkedDiskList.stream().noneMatch(bookmarkedDisk -> bookmarkedDisk.getDiskId().equals(myDisk.getDiskId())))
			throw new AppException(ErrorCode.BOOKMARK_DISK_LIMIT, ErrorCode.BOOKMARK_DISK_LIMIT.getMessage());

		myDisk.modifyDisk(dto);
		diskRepository.save(myDisk);

		return DiskResponseDto.builder()
				.diskId(myDisk.getDiskId())
				.diskName(myDisk.getDiskName())
				.content(myDisk.getContent())
				.diskColor(myDisk.getDiskColor())
				.isPrivate(myDisk.isPrivate())
				.isBookmark(myDisk.getIsBookmark() != null)
				.likeCount(myDisk.getLikeCount())
				.diskOwnerId(myDisk.getMember().getMemberId())
				.diskOwnerNickname(myDisk.getMember().getNickname())
				.isMine(true)
				.createdAt(myDisk.getCreatedAt())
				.modifiedAt(myDisk.getModifiedAt())
				.build();
	}

	/**
	 * 디스크 삭제
	 */
	public Boolean deleteDisk(Long diskId, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		DiskEntity myDisk = diskRepository.findById(diskId)
				.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		if(myDisk.getMember().getMemberId() != member.getMemberId()) throw new AppException(ErrorCode.NOT_DISK_OWNER, ErrorCode.NOT_DISK_OWNER.getMessage());

		diskRepository.delete(myDisk);
		return true;
	}

	/**
	 * 디스크 좋아요
	 */
	public Integer likedDisk(Long diskId) {
		DiskEntity disk = diskRepository.findById(diskId)
				.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		disk.likedDisk();

		diskRepository.save(disk);
		return disk.getLikeCount();
	}

	/**
	 * 디스크 북마크
	 */
	public Boolean bookmarkDisk(Long diskId, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		DiskEntity disk = diskRepository.findById(diskId)
				.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));
		if(disk.getMember().getMemberId() != member.getMemberId()) throw new AppException(ErrorCode.NOT_DISK_OWNER, ErrorCode.NOT_DISK_OWNER.getMessage());

		List<DiskEntity> bookmarkedDiskList = diskRepository.findAllByMemberMemberIdAndIsBookmarkNotNullOrderByIsBookmarkDesc(member.getMemberId());
		if (bookmarkedDiskList.size() >= 3 && bookmarkedDiskList.stream().noneMatch(bookmarkedDisk -> bookmarkedDisk.getDiskId().equals(disk.getDiskId())))
			throw new AppException(ErrorCode.BOOKMARK_DISK_LIMIT, ErrorCode.BOOKMARK_DISK_LIMIT.getMessage());

			disk.bookmarkDisk();
		diskRepository.save(disk);
		return (disk.getIsBookmark() != null);
	}
}
