package pt.isec.pd.splitwise.server_api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pt.isec.pd.splitwise.server_api.repository.DataWrapper;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {
	private final DataWrapper dataWrapper;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			String email = authentication.getName();
			String password = authentication.getCredentials().toString();

			User authUser = dataWrapper.getUserDAO().getUserByEmail(email);
			if (authUser == null)
				return null;

			if (authUser.getPassword().equals(password))
				return new UsernamePasswordAuthenticationToken(email, password, null);
		} catch ( SQLException e ) {
			log.error("UserAuthenticationProvider.authenticate: {}", e.getMessage()); //TODO: improve this
			throw new RuntimeException(e);
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
