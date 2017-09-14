package org.codenergic.theskeleton.registration;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends PagingAndSortingRepository<RegistrationEntity, String> {
	RegistrationEntity findByToken(String token);
}
