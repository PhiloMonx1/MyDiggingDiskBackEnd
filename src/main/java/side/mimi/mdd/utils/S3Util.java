package side.mimi.mdd.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import side.mimi.mdd.exception.AppException;
import side.mimi.mdd.exception.ErrorCode;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {
	private final S3Client s3Client;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	public String uploadFile(MultipartFile file) throws IOException {
		if(file.getOriginalFilename().equals("")) return null;

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		List<String> allowedExtensions = Arrays.asList("png", "jpg", "jpeg");
		if (file != null && !allowedExtensions.contains(fileExtension)) throw new AppException(ErrorCode.NOT_SUPPORTED_FILE_TYPE, ErrorCode.NOT_SUPPORTED_FILE_TYPE.getMessage());

		String contentType = file.getContentType();
		if (contentType == null || contentType.isEmpty()) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		s3Client.putObject(
				PutObjectRequest.builder()
						.bucket(bucketName)
						.key(fileName)
						.contentType(contentType)
						.acl(ObjectCannedACL.PUBLIC_READ)
						.build(),
				RequestBody.fromBytes(file.getBytes())
		);
		return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(fileName).build()).toString();
	}
}

