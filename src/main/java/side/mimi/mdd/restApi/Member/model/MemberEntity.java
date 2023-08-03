package side.mimi.mdd.restApi.Member.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name="MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberEntity {

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
}
