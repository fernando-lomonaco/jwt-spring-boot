package academy.lomonaco.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import academy.lomonaco.core.model.ApplicationUser;

public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {

	ApplicationUser findByUsername(String username);

}
