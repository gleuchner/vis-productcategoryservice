package de.hska.muon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableHystrix
@EnableHystrixDashboard
@EnableResourceServer
@RibbonClient("productcategory-service")
public class ProductandcategoryserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductandcategoryserviceApplication.class, args);
    }


    @Value("${OAuth2ClientId:my-client-with-secret}")
    private String oAuth2ClientId;

    @Value("${OAuth2ClientSecret:secret}")
    private String oAuth2ClientSecret;

    @Value("${oauth.token:http://authserver:8763/oauth/token}")
    private String accessTokenUri;

    @LoadBalanced
    @Bean
    public RestTemplate oAuthRestTemplate() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setId("1");
        resourceDetails.setClientId(oAuth2ClientId);
        resourceDetails.setClientSecret(oAuth2ClientSecret);
        resourceDetails.setAccessTokenUri(accessTokenUri);
        List<String> scopes = new ArrayList<>();
        scopes.add("read");
        scopes.add("write");
        scopes.add("trust");
        resourceDetails.setScope(scopes);

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());

        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
        rf.setReadTimeout(10000);
        rf.setConnectTimeout(1000);
        return restTemplate;
    }

    @Primary
    @Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(
                "http://authserver:8763/oauth/check_token");
        tokenService.setClientId("my-client-with-secret");
        tokenService.setClientSecret("secret");
        return tokenService;
    }
}
