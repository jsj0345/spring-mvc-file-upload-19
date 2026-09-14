package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

  @Value("${file.dir}")
  private String fileDir;

  public String getFullPath(String filename) {
    return fileDir + filename;
  }

  // 파일이 여러개 일 때
  public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
    List<UploadFile> storeFileResult = new ArrayList<>();
    for(MultipartFile multipartFile : multipartFiles) {
      if (!multipartFile.isEmpty()) {
        UploadFile uploadFile = storeFile(multipartFile);
        storeFileResult.add(uploadFile);
      }
    }

    return storeFileResult;
  }

  // 파일이 한개 일 때
  public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
    if (multipartFile.isEmpty()) {
      return null;
    }

    String originalFilename = multipartFile.getOriginalFilename();
    //image.png

    //서버에 저장하는 파일명
    String storeFileName = createStoreFileName(originalFilename);

    multipartFile.transferTo(new File(getFullPath(storeFileName))); // 서버에 저장

    return new UploadFile(originalFilename, storeFileName);
  }

  private String createStoreFileName(String originalFilename) {
    String uuid = UUID.randomUUID().toString();

    String ext = extractedExt(originalFilename);

    String storeFileName = uuid + "." + ext;

    return storeFileName;
  }

  private String extractedExt(String originalFilename) {
    int pos = originalFilename.indexOf(".");
    return originalFilename.substring(pos + 1);
  }


}
