package org.codenergic.theskeleton.role.impl;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRepository;
import org.codenergic.theskeleton.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public RoleEntity findRoleByCode(String code) {
		return roleRepository.findByCode(code);
	}

	@Override
	public RoleEntity findRoleById(String id) {
		return roleRepository.findOne(id);
	}

	@Override
	public Page<RoleEntity> findRoles(Pageable pageable) {
		return roleRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public RoleEntity saveRole(RoleEntity role) {
		role.setId(null);
		return roleRepository.save(role);
	}

	@Override
	@Transactional
	public RoleEntity updateRole(String code, RoleEntity role) {
		RoleEntity e = findRoleByCode(code);
		Assert.notNull(e, "Role not found");
		e.setCode(role.getCode());
		e.setDescription(role.getDescription());

		return e;
	}
}
