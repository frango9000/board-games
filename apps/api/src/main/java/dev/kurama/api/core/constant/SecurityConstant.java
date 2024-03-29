package dev.kurama.api.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstant {

  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String JWT_TOKEN_HEADER = "Jwt-Token";
  public static final String JWT_REFRESH_TOKEN_HEADER = "Jwt-Refresh-Token";
  public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
  public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
  public static final String AUTH_ISSUER = "api";
  public static final String AUTH_AUDIENCE = "app";
  public static final String AUTHORITIES = "authorities";
  public static final String FORBIDDEN_MESSAGE = "Authentication required";
  public static final String UNAUTHORIZED_MESSAGE = "Insufficient permissions";

}
