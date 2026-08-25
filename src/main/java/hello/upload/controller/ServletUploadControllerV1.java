package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadControllerV1 {

  @GetMapping("/upload")
  public String newFile() {
    return "upload-form";
  }

  @PostMapping("/upload")
  public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
    log.info("request={}", request);

    String itemName = request.getParameter("itemName");
    log.info("itemName={}", itemName);

    Collection<Part> parts = request.getParts(); // 여기서 의미하는 parts는 클라이언트에서 서버로 요청을 보낼 때, 각 데이터를 나눈 것.
    log.info("parts={}", parts);

    return "upload-form";
  }

}
