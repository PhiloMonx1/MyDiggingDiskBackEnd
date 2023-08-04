package side.mimi.mdd.restApi.Member.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.mimi.mdd.restApi.Disk.model.DiskEntity;
import side.mimi.mdd.restApi.Member.dto.request.MemberModifyRequestDto;
import side.mimi.mdd.utils.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name="MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID")
	private Long memberId;

	@Column(name = "MEMBER_NAME", nullable = false, unique = true)
	private String memberName;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "NICKNAME", length = 10, nullable = false, unique = true)
	private String nickname;

	@Column(name = "INTRODUCE", length = 30)
	private String introduce;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DiskEntity> diskList = new ArrayList<>();

	public void modifyMemberInfo(MemberModifyRequestDto dto){
		if(!dto.getNickname().isEmpty()) nickname = dto.getNickname();
		if(!dto.getIntroduce().isEmpty()) introduce = dto.getIntroduce();
	}
}
