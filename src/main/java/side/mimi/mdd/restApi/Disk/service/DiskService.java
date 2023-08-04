package side.mimi.mdd.restApi.Disk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
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

	public DiskResponseDto postDisk(DiskPostRequestDto dto, String token) {
		MemberEntity member = memberService.getMemberByJwt(token);

		if(dto.getDiskName().length() > 30) throw new AppException(ErrorCode.OVER_LONG_DISK_NAME, "DiskName 값은 30자를 초과할 수 없습니다.");
		if(dto.getContent().length() > 300) throw new AppException(ErrorCode.OVER_LONG_CONTENT, "content 값은 300자를 초과할 수 없습니다.");

		//isPrivate Default값 부여
		Boolean isPrivate = false;
		if(dto.getIsPrivate() != null){
			isPrivate = dto.getIsPrivate();
		}

		DiskEntity disk = DiskEntity.builder()
				.diskName(dto.getDiskName())
				.content(dto.getContent())
				.diskColor(dto.getDiskColor())
				.isPrivate(isPrivate)
				.member(member)
				.build();

		diskRepository.save(disk);

		return DiskResponseDto.builder()
				.diskId(disk.getDiskId())
				.diskName(disk.getDiskName())
				.content(disk.getContent())
				.diskColor(disk.getDiskColor())
				.isPrivate(disk.isPrivate())
				.isMine(true)
				.createdAt(disk.getCreatedAt())
				.modifiedAt(disk.getModifiedAt())
				.build();
	}
}
