package dev.kurama.api.core.filter;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kurama.api.core.constant.SecurityConstant;
import dev.kurama.api.core.domain.DomainResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
    throws IOException {
    var domainResponse = DomainResponse.builder()
      .reason(UNAUTHORIZED.getReasonPhrase())
      .status(UNAUTHORIZED.value())
      .title(SecurityConstant.UNAUTHORIZED_MESSAGE)
      .build();
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setStatus(UNAUTHORIZED.value());
    OutputStream outputStream = response.getOutputStream();
    var mapper = new ObjectMapper();
    mapper.writeValue(outputStream, domainResponse);
    outputStream.flush();
  }
}
