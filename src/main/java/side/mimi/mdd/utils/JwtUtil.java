package side.mimi.mdd.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {

	public static String createToken(String memberName, String secretKey, long expireTimeMs) {
		String issuer = "JWT";
		Algorithm hashKey = Algorithm.HMAC256(secretKey);
		Date issuedTime = new Date();
		Date expirationTime = new Date(issuedTime.getTime() + expireTimeMs);

		return JWT.create()
				.withIssuer(issuer)
				.withClaim("memberName", memberName)
				.withIssuedAt(issuedTime)
				.withExpiresAt(expirationTime)
				.sign(hashKey);

	}

	public static DecodedJWT decodedToken(String token, String secretKey){
		return JWT.require(Algorithm.HMAC256(secretKey))
				.build()
				.verify(token);
	}

	public static Boolean isExpiredToken(String token, String secretKey) {
		DecodedJWT decodedJWT = decodedToken(token, secretKey);
		return decodedJWT.getExpiresAt().before(new Date());
	}

	public static String getMemberName(String token, String secretKey){
		DecodedJWT decodedJWT = decodedToken(token, secretKey);
		return decodedJWT.getClaim("memberName").asString();
	}

}
