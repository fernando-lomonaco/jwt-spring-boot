package academy.lomonaco.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ApplicationUser implements AbastractEntity {

	private static final long serialVersionUID = -5382321668933592553L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@NotNull(message = "O campo username é obrigatorio")
	@Column(nullable = false)
	private String username;
	@NotNull(message = "O campo password é obrigatorio")
	@Column(nullable = false)
	@ToString.Exclude
	private String password;
	@NotNull(message = "O campo role é obrigatorio")
	@Column(nullable = false)
	// TODOS USUARIOS COMECAM COMO USER
	private String role = "USER";

	// copy constructor
	public ApplicationUser(@NotNull ApplicationUser applicationUser) {
		this.id = applicationUser.getId();
		this.username = applicationUser.getUsername();
		this.password = applicationUser.getPassword();
		this.role = applicationUser.getRole();

	}

}
