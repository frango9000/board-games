package dev.kurama.api.core.rest;

import static com.google.common.collect.Sets.newHashSet;
import static dev.kurama.api.core.constant.RestPathConstant.INDEX_URL;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.walk;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.kurama.api.core.configuration.SecurityConfiguration;
import dev.kurama.api.core.filter.JWTAccessDeniedHandler;
import dev.kurama.api.core.filter.JWTAuthenticationEntryPoint;
import dev.kurama.api.core.utility.JWTTokenProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApplicationForwardController.class)
@Import(SecurityConfiguration.class)
class ApplicationForwardControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @TempDir
  static Path tempDir;

  @BeforeAll
  static void beforeAll() {
    prepareResources(tempDir);
  }

  @DisplayName("Route endpoint should forward")
  @ParameterizedTest(name = "if someone requests \"{0}\"")
  @ValueSource(strings = {"/", "/app", "/app/", "/app/some", "/app/some.html", "/app/some.css", "/app/test.js",
    "/app/a/b/c/d/", "/app/a/b/c/d/index.html", "/app/a/b/c/d/some.css", "/app/a/b/c/d/some.js"

  })
  void route_should_forward_if_someone_requests(String path) throws Exception {
    mockMvc.perform(get(path)).andExpect(status().isOk()).andExpect(forwardedUrl(INDEX_URL));
  }

  @DisplayName("Route endpoint should not forward")
  @ParameterizedTest(name = "if someone requests \"{0}\"")
  @ArgumentsSource(RouteShouldNotForwardArgumentsProvider.class)
  void route_should_not_forward_if_someone_requests(String path) throws Exception {
    mockMvc.perform(get(path)).andExpect(status().isOk()).andExpect(forwardedUrl(null));
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  private static void prepareResources(Path tempDir) {
    Path parentDir = tempDir.resolve("app");
    createDirectory(parentDir);

    Set<Pair<String, String>> namePatterns = newHashSet(of("index", "html"), of("favicon", "ico"));
    namePatterns.stream().forEach(p -> addResource(parentDir, p, false));

    namePatterns = newHashSet(of("main", "js"), of("polyfills", "js"), of("runtime", "js"), of("styles", "css"),
      of("common", "js"));
    namePatterns.stream().forEach(p -> addResource(parentDir, p, true));

    Path assetsDir = parentDir.resolve("assets");
    createDirectory(assetsDir);

    namePatterns = newHashSet(of("flag", "png"), of("flag", "jpg"));
    namePatterns.stream().forEach(p -> addResource(assetsDir, p, false));

    Path i18nDir = assetsDir.resolve("i18n");
    createDirectory(i18nDir);

    namePatterns = newHashSet(of("en", "json"), of("en", "json"), of("en", "json"));
    namePatterns.stream().forEach(p -> addResource(i18nDir, p, false));
  }

  @SneakyThrows
  private static void addResource(Path parentDir, Pair<String, String> namePattern, boolean randomize) {
    String name = String.format("%s%s.%s", namePattern.getLeft(), randomize ? ("." + randomAlphanumeric(8)) : "",
      namePattern.getRight());
    createFile(parentDir.resolve(name));
  }

  private static String relativePath(Path base, Path resource) {
    return "/" + base.relativize(resource).toString().replace('\\', '/');
  }

  @TestConfiguration
  static class AngularRouteControllerTestConfiguration implements WebMvcConfigurer {

    @Override
    @SneakyThrows
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
      walk(tempDir).filter(Files::isRegularFile).forEach(f -> addResourceHandler(registry, f, tempDir));
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, Path resource, Path base) {
      String handler = relativePath(base, resource);
      String location = "file:///%s/".formatted(base);
      registry.addResourceHandler(handler).addResourceLocations(location);
    }
  }

  static class RouteShouldNotForwardArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
      return walk(tempDir).filter(Files::isRegularFile).map(f -> relativePath(tempDir, f)).map(Arguments::arguments);
    }
  }


  @TestConfiguration
  protected static class ApplicationForwardControllerITConfig {

    @Bean
    @Qualifier("userDetailsService")
    public UserDetailsService UserDetailsService() {
      return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public JWTTokenProvider JWTTokenProvider() {
      return Mockito.mock(JWTTokenProvider.class);
    }

    @Bean
    public JWTAuthenticationEntryPoint JWTAuthenticationEntryPoint() {
      return Mockito.mock(JWTAuthenticationEntryPoint.class);
    }

    @Bean
    public JWTAccessDeniedHandler JWTAccessDeniedHandler() {
      return Mockito.mock(JWTAccessDeniedHandler.class);
    }

    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoder() {
      return Mockito.mock(BCryptPasswordEncoder.class);
    }
  }
}
