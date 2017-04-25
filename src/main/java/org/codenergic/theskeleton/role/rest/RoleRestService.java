package org.codenergic.theskeleton.role.rest;

import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleRestService {
	@Autowired
	private RoleService roleService;

	@RequestMapping(path = "/{code}", method = RequestMethod.GET)
	public RoleEntity findRoleByCode(@PathVariable("code") String code) {
		return roleService.findRoleByCode(code);
	}

	@RequestMapping(method = RequestMethod.GET)
	public Page<RoleEntity> findRoles(Pageable pageable) {
		return roleService.findRoles(pageable);
	}

	@RequestMapping(method = RequestMethod.POST)
	public RoleEntity saveRole(@RequestBody RoleEntity role) {
		return roleService.saveRole(role);
	}

	@RequestMapping(path = "/{code}", method = RequestMethod.PUT)
	public RoleEntity updateRole(@PathVariable("code") String code, @RequestBody RoleEntity role) {
		return roleService.updateRole(code, role);
	}
}
