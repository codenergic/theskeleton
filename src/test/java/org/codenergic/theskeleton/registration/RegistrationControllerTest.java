package org.codenergic.theskeleton.registration;

import org.codenergic.theskeleton.core.test.InjectUserDetailsService;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.user.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {RegistrationController.class}, secure = false)
@InjectUserDetailsService
public class RegistrationControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private RegistrationService registrationService;
	@MockBean
	private TokenStoreService tokenStoreService;
	@MockBean
	private ProviderSignInUtils providerSignInUtils;

	@Test
	public void testRegisterUserAlreadyExist() throws Exception {
		String errorMsg = "Username or email already exists";
		when(registrationService.registerUser(any())).thenThrow(new RegistrationException(errorMsg));
		MockHttpServletRequestBuilder request = post("/registration")
			.param("username", "testuser")
			.param("password", "securepassword")
			.param("email", "theskeleton-test@codenergic.org");
		MockHttpServletResponse response = mockMvc.perform(request)
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains(errorMsg);
	}

	@Test
	public void testRegisterUser() throws Exception {
		UserEntity user = new UserEntity().setId("123");
		when(registrationService.registerUser(any())).thenReturn(user);
		when(tokenStoreService.sendTokenNotification(any(),any())).thenReturn(any());
		MockHttpServletRequestBuilder request = post("/registration")
			.param("username", "testuser")
			.param("password", "securepassword")
			.param("email", "theskeleton-test@codenergic.org");
		MockHttpServletResponse response = mockMvc.perform(request)
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("We have sent");
	}
}
