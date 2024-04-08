package hideonbush3.springboot.happypet.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3FileService {
    private final String directory = "static/images/";

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile, String randomUuid) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] nameAndExt = originalFilename.split("\\.");
        String fileNameToSave = nameAndExt[0] + " " + randomUuid + "." + nameAndExt[1];

        String key = directory + fileNameToSave;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, key, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, key).toString();
    }

    public void deleteImage(String originalFilename)  {
        String key = directory + originalFilename;
        amazonS3.deleteObject(bucket, key);
    }
}