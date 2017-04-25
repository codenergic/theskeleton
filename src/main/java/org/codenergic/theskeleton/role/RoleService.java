package org.codenergic.theskeleton.role;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.role.impl.RoleServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
	static RoleService newInstance() {
		return new RoleServiceImpl();
	}

	RoleEntity findRoleByCode(@NotNull String code);

	RoleEntity findRoleById(@NotNull String id);

	Page<RoleEntity> findRoles(Pageable pageable);

	RoleEntity saveRole(@NotNull @Valid RoleEntity role);

	RoleEntity updateRole(@NotNull String cpde, @NotNull @Valid RoleEntity role);
}
