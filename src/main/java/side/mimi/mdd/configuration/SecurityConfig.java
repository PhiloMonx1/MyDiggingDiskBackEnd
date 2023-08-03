package side.mimi.mdd.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import side.mimi.mdd.restApi.Member.service.MemberService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private String frontendUrl = "http://localhost:3000";
	private final MemberService memberService;
	@Value("${jwt.secret}")
	private String secretKey;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.httpBasic(httpBasic -> httpBasic.disable())
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors
						.configurationSource(request -> {
							CorsConfiguration configuration = new CorsConfiguration();
							configuration.setAllowedOrigins(List.of(frontendUrl));
							configuration.setAllowedMethods(
									List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
							configuration.setAllowedHeaders(List.of("*"));
							configuration.setExposedHeaders(List.of("*"));
							configuration.setAllowCredentials(true);
							return configuration;
						}))
				.headers(header -> header
						//h2-console XFrame 관련 설정. (개발단계)
						.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
				.authorizeHttpRequests(authorize -> authorize
						//h2-console (개발단계 허용)
						.requestMatchers(PathRequest.toH2Console()).permitAll()
						//전체 허용 (개발단계 전체 API 허용)
//						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).permitAll()
						//회원 가입 로그인.
						.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/api/v1/members/join")).permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/members/login")).permitAll()

						.anyRequest().authenticated())
				.sessionManagement(sessionManagement -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}

