package side.mimi.mdd.configuration;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.utils.JwtUtil;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		//토큰 요구하지 않는 API 토큰 없이 요청 왔을 때 대비해서 Bearer 안 붙으면 무시
		if(authorization == null || !authorization.startsWith("Bearer ")){
			filterChain.doFilter(request, response);
			return;
		}
		try {
			String memberName = JwtUtil.getMemberName(authorization);
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(memberName, null, List.of(new SimpleGrantedAuthority("USER")));

			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			filterChain.doFilter(request, response);
		}catch (TokenExpiredException e){
			request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getMessage());
		}catch (JWTDecodeException e){
			request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getMessage());
		}
	}
}
