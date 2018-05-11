package com.siwoo.springfacebooksociallogindemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableWebSecurity
public class SpringFacebookSocialLoginDemoApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(SpringFacebookSocialLoginDemoApplication.class, args);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rest/loginpages", "/rest/userinfo").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login().userInfoEndpoint().customUserType(GitHubUser.class,"github");
    }

    @Bean
    ViewResolver viewResolver() {
        return new InternalResourceViewResolver("/static/",".html");
    }

    @Controller
    public static class MainController {

        @Autowired
        private ClientRegistrationRepository clientRegistrationRepository;

        @Autowired
        private OAuth2AuthorizedClientService authorizedClientService;

        @ResponseBody
        @GetMapping("/rest/loginpages")
        public Map login(HttpServletRequest request) {
            String host = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            Map map = new HashMap();
            map.put("facebook", host + "/oauth2/authorization/facebook");
            map.put("google", host + "/oauth2/authorization/google");
            map.put("github", host + "/oauth2/authorization/github");
            return map;
        }

        @ResponseBody
        @GetMapping("/rest/userinfo")
        public Principal principal(Principal principal) {
            System.out.println(principal);
            return principal;
        }

        @RequestMapping("/")
        public void index(OAuth2AuthenticationToken authentication, HttpServletResponse httpResponse, HttpServletRequest request) throws IOException {
            String host = request.getScheme() + "://" + request.getServerName() + ":" + 4200 + request.getContextPath();
            OAuth2AuthorizedClient authorizedClient =
                    this.authorizedClientService.loadAuthorizedClient(
                            authentication.getAuthorizedClientRegistrationId(),
                            authentication.getName());

            System.out.println(authentication.getPrincipal());
            System.out.println(authorizedClient.getAccessToken());
            System.out.println(host);
            httpResponse.sendRedirect(host+"/");
        }

    }
}
