package side.mimi.mdd.restApi.Member.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.mimi.mdd.utils.BaseEntity;

@Builder
@Entity
@Table(name="LOGIN_LOG")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginLogEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LOG_ID")
	private Long logId;

	@Column(name = "MEMBER_NAME", nullable = false)
	private String memberName;

	@Column(name = "STATE", nullable = false)
	private boolean state;
}
