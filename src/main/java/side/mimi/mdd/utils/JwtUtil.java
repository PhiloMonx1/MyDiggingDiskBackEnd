package side.mimi.mdd.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

	private static String secretKey;
	private static String refreshKey;
	private static Long expireTimeMs = 1000 * 60 * 60 * 24L;

	@Value("${jwt.secret}")
	public void setSecretKey(String secret) {
		JwtUtil.secretKey = secret;
	}
	@Value("${refreshKey.secret}")
	public void setRefreshKey(String refreshKey) {
		JwtUtil.refreshKey = refreshKey;
	}

	public static String createAccessToken(String memberName) {
		String issuer = "MDD";
		Algorithm hashKey = Algorithm.HMAC256(secretKey);
		Date issuedTime = new Date();
		Date expirationTime = new Date(issuedTime.getTime() + expireTimeMs);

		return JWT.create()
				.withIssuer(issuer)
				.withClaim("memberName", memberName.toLowerCase())
				.withIssuedAt(issuedTime)
				.withExpiresAt(expirationTime)
				.sign(hashKey);
	}

	public static String createRefreshToken(Long memberId) {
		String issuer = "MDD";
		Algorithm hashKey = Algorithm.HMAC256(refreshKey);
		Date issuedTime = new Date();
		Long refreshExpireTimeMs = 1000 * 60 * 60 * 24 * 7L;
		Date expirationTime = new Date(issuedTime.getTime() + refreshExpireTimeMs);

		return JWT.create()
				.withIssuer(issuer)
				.withClaim("memberId", memberId)
				.withIssuedAt(issuedTime)
				.withExpiresAt(expirationTime)
				.sign(hashKey);
	}

	public static DecodedJWT decodedToken(String token){
		if(token.startsWith("Bearer ")){
			token = token.split(" ")[1];
		}

		return JWT.require(Algorithm.HMAC256(secretKey))
				.build()
				.verify(token);
	}

	public static Boolean isExpiredToken(String token) {
		DecodedJWT decodedJWT = decodedToken(token);
		return decodedJWT.getExpiresAt().before(new Date());
	}

	public static String getMemberName(String token){
		DecodedJWT decodedJWT = decodedToken(token);
		return decodedJWT.getClaim("memberName").asString();
	}

}
