package side.mimi.mdd.configuration;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import side.mimi.mdd.utils.JwtUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		}catch (AppException e){
			ErrorCode errorCode = e.getErrorCode();
			HttpStatus httpStatus = errorCode.getHttpStatus();
			String message = e.getMessage();

			Map<String, Object> responseBody = new LinkedHashMap<>();
			responseBody.put("HttpStatusCode", httpStatus.value());
			responseBody.put("HttpStatusMessage", httpStatus.getReasonPhrase());
			responseBody.put("ErrorCode", errorCode.name());
			responseBody.put("errorMessage", message);

			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(httpStatus.value());
			response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
			return;
		}
		filterChain.doFilter(request, response);
	}
}
