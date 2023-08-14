package side.mimi.mdd.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;

import java.util.Date;

@Component
public class JwtUtil {

	private static String secretKey;
	private static String refreshKey;
	private static Long expireTimeMs = 1000L * 60 * 60 * 24L;

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

		if(memberName.equals("testMember".toLowerCase())){
			expirationTime = new Date(issuedTime.getTime() + 15000);
		}

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
		Long refreshExpireTimeMs = 1000 * 60 * 60 * 24 * 180L;
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

		try {
			return JWT.require(Algorithm.HMAC256(secretKey))
					.build()
					.verify(token);
		}catch (TokenExpiredException e){
			throw new AppException(ErrorCode.EXPIRED_TOKEN, ErrorCode.EXPIRED_TOKEN.getMessage());
		}catch (JWTDecodeException e){
			throw new AppException(ErrorCode.NOT_DECODE_TOKEN, ErrorCode.NOT_DECODE_TOKEN.getMessage());
		}catch (SignatureVerificationException e){
			throw new AppException(ErrorCode.WRONG_TYPE_TOKEN, ErrorCode.WRONG_TYPE_TOKEN.getMessage());
		}
	}
	public static Long verifyRefreshToken(String refreshToken) {
		try {
			return JWT.require(Algorithm.HMAC256(refreshKey))
					.build()
					.verify(refreshToken).getClaim("memberId").asLong();

		} catch (TokenExpiredException e) {
			throw new AppException(ErrorCode.EXPIRED_TOKEN, ErrorCode.EXPIRED_TOKEN.getMessage());
		} catch (JWTDecodeException e) {
			throw new AppException(ErrorCode.NOT_DECODE_TOKEN, ErrorCode.NOT_DECODE_TOKEN.getMessage());
		} catch (SignatureVerificationException e) {
			throw new AppException(ErrorCode.WRONG_TYPE_TOKEN, ErrorCode.WRONG_TYPE_TOKEN.getMessage());
		}
	}

	public static String getMemberName(String token){
		DecodedJWT decodedJWT = decodedToken(token);
		return decodedJWT.getClaim("memberName").asString();
	}

}
