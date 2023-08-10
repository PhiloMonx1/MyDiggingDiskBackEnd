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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiskService {
	private final DiskRepository diskRepository;
	private final MemberService memberService;

	public List<DiskResponseDto> getMyDisks(String token) {
		MemberEntity member = memberService.getMemberByJwt(token);
		List<DiskEntity> myDisks = diskRepository.findAllByMemberMemberId(member.getMemberId());

		return myDisks.stream().map(disk -> DiskResponseDto.builder()
						.diskId(disk.getDiskId())
						.diskName(disk.getDiskName())
						.content(disk.getContent())
						.diskColor(disk.getDiskColor())
						.isPrivate(disk.isPrivate())
						.isMine(true)
						.createdAt(disk.getCreatedAt())
						.modifiedAt(disk.getModifiedAt())
						.build())
				.collect(Collectors.toList());
	}

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
				.isMine(member != null && disk.getMember().getMemberId() == member.getMemberId())
				.createdAt(disk.getCreatedAt())
				.modifiedAt(disk.getModifiedAt())
				.build();
	}

	public DiskResponseDto postDisk(DiskPostRequestDto dto, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		if(dto.getDiskName().length() > 30) throw new AppException(ErrorCode.OVER_LONG_DISK_NAME, ErrorCode.OVER_LONG_DISK_NAME.getMessage());
		if(dto.getContent().length() > 300) throw new AppException(ErrorCode.OVER_LONG_CONTENT, ErrorCode.OVER_LONG_CONTENT.getMessage());

		//isPrivate, isFavorite Default값 부여
		Boolean isPrivate = false;
		Boolean isFavorite = false;
		if(dto.getIsPrivate() != null) isPrivate = dto.getIsPrivate();
		if(dto.getIsFavorite() != null) isFavorite = dto.getIsFavorite();

		DiskEntity disk = DiskEntity.builder()
				.diskName(dto.getDiskName())
				.content(dto.getContent())
				.diskColor(dto.getDiskColor())
				.isPrivate(isPrivate)
				.isFavorite(isFavorite)
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
				.isFavorite(disk.isFavorite())
				.likeCount(disk.getLikeCount())
				.isMine(true)
				.createdAt(disk.getCreatedAt())
				.modifiedAt(disk.getModifiedAt())
				.build();
	}

	public Long modifyDisk(Long diskId, DiskModifyRequestDto dto, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		DiskEntity myDisk = diskRepository.findById(diskId)
						.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		if(myDisk.getMember().getMemberId() != member.getMemberId()) throw new AppException(ErrorCode.NOT_DISK_OWNER, ErrorCode.NOT_DISK_OWNER.getMessage());
		if(dto.getDiskName() != null && dto.getDiskName().length() > 30) throw new AppException(ErrorCode.OVER_LONG_DISK_NAME, ErrorCode.OVER_LONG_DISK_NAME.getMessage());
		if(dto.getContent() != null && dto.getContent().length() > 300) throw new AppException(ErrorCode.OVER_LONG_CONTENT, ErrorCode.OVER_LONG_CONTENT.getMessage());

		myDisk.modifyDisk(dto);
		diskRepository.save(myDisk);

		return myDisk.getDiskId();
	}

	public Boolean deleteDisk(Long diskId, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		DiskEntity myDisk = diskRepository.findById(diskId)
				.orElseThrow(() ->new AppException(ErrorCode.NOT_FOUND_DISK, ErrorCode.NOT_FOUND_DISK.getMessage()));

		if(myDisk.getMember().getMemberId() != member.getMemberId()) throw new AppException(ErrorCode.NOT_DISK_OWNER, ErrorCode.NOT_DISK_OWNER.getMessage());

		diskRepository.delete(myDisk);
		return true;
	}
}
