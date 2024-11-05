package io.nugulticket.s3file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class S3FileService {

    // S3
    private final AmazonS3Client s3Client;

    // 이미지 파일 크기 제한
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 이미지를 등록하고 URL 추출하는 메서드
     * @param image 이미지 정보
     * @param bucket AWS만든 Bucket 이름
     * @return String 타입의 url
     */
    public String uploadFile(MultipartFile image, String bucket) {
        try {
            // 이미지 파일 유효성 검증
            validateFile(image);

            // 이미지 이름 변경
            String originalFileName = image.getOriginalFilename();
            String fileName = changeFileName(originalFileName);

            // S3에 파일을 보낼 때 파일의 종류와 크기를 알려주기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            metadata.setContentDisposition("inline");

            // S3에 파일 업로드
            s3Client.putObject(bucket, fileName, image.getInputStream(), metadata);

            return s3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new ApiException(ErrorStatus._FILE_UPLOAD_FAILED);
        }
    }

    /**
     *  파일 유효성 검사 (크기와 파일 형식 제한)
     * @param file 검사를 진행할 이미지 파일
     */
    private void validateFile(MultipartFile file) {
        // 이미지 파일이 비어있을 경우
        if (file.isEmpty()) {
            throw new ApiException(ErrorStatus._EMPTY_FILE);
        }
        //파일 크기가 5MB 초과하였을 경우
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(ErrorStatus._FILE_SIZE_EXCEEDED);
        }
        // 파일 형식 제한
        if (!isSupportedContentType(file.getContentType())) {
            throw new ApiException(ErrorStatus._UNSUPPORTED_FILE_FORMAT);
        }
    }

    /**
     * 파일 확장자 검사
     * @param contentType 검사를 진행할 파일의 확장자
     * @return true : 지원하는 확장자 / false : 지원하지 않는 확장자
     */
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg");
    }


    /**
     * 이미지 파일 이름 변경하는 메서드
     * @param originalFileName 원본 파일 이름
     * @return 이미지 등록 날짜 + 원본 파일 이름 형태의 문자열
     */
    private String changeFileName(String originalFileName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter) + "_" + originalFileName;
    }


    /**
     * 등록된 사진 기존 URL 원본 파일이름으로 디코딩하는 메서드
     * @param url 현재 등록된 사진의 URL
     * @return String 타입의 원본 파일 이름
     */
    public String extractFileNameFromUrl(String url) {
        // URL 마지막 슬래시의 위치를 찾아서 인코딩된 파일 이름 가져오기
        String encodedFileName = url.substring(url.lastIndexOf("/") + 1);

        // 인코딩된 파일 이름을 디코딩 해서 진짜 원본 파일 이름 가져오기
        return URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
    }
}

