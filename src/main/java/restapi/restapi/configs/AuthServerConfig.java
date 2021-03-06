package restapi.restapi.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerTokenServicesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import restapi.restapi.account.AccountService;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {


    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final TokenStore tokenStore;

    public AuthServerConfig(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AccountService accountService, TokenStore tokenStore) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("myApp")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read","write")
                .secret(this.passwordEncoder.encode("pass"))
                .accessTokenValiditySeconds(10 * 60)
                .refreshTokenValiditySeconds(6 * 10 * 60);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}
