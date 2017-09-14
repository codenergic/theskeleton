package org.codenergic.theskeleton.registration;

import org.codenergic.theskeleton.core.data.AbstractEntity;
import org.codenergic.theskeleton.user.UserEntity;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "ts_registration")
public class RegistrationEntity extends AbstractEntity {

	@NotNull
	@Column(length = 200, unique = true)
	private String token;
	@OneToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@Column(name = "expiry_date")
	private Date expiryDate;

	public String getToken() {
		return token;
	}

	public RegistrationEntity setToken(String token) {
		this.token = token;
		return this;
	}

	public boolean isTokenExpired() {
		return DateTime.now().getMillis() > this.expiryDate.getTime();
	}

	public UserEntity getUser() {
		return user;
	}

	public RegistrationEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public RegistrationEntity setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
		return this;
	}
}
