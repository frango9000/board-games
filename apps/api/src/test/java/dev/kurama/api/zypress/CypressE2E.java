package dev.kurama.api.zypress;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import dev.kurama.api.core.service.UserService;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import lombok.extern.flogger.Flogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DisableIfTestFails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@DisableIfTestFails
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@org.testcontainers.junit.jupiter.Testcontainers()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"integration-test", "e2e"})
@Flogger
class CypressE2E {

  @LocalServerPort
  private int port;

  @Autowired
  private UserService userService;

  @Value("${CYPRESS_RECORD_KEY:#{null}}")
  private String CYPRESS_RECORD_KEY;

  @Value("${CYPRESS_SAVE_VIDEO:#{false}}")
  private Boolean CYPRESS_SAVE_VIDEO;

  private static final int MAX_TOTAL_TEST_TIME_IN_MINUTES = 15;

  private final String cypressVersion = getNodeDependencyVersion("cypress");


  @Container
  static GenericContainer mailHogContainer = new GenericContainer<>(
    DockerImageName.parse("mailhog/mailhog:v1.0.1")).waitingFor(Wait.forLogMessage(".*Serving under.*", 1))
    .withExposedPorts(1025, 8025);

  @DynamicPropertySource
  static void configureMailHost(DynamicPropertyRegistry registry) {
    registry.add("spring.mail.host", mailHogContainer::getHost);
    registry.add("spring.mail.port", mailHogContainer::getFirstMappedPort);
  }

  @BeforeEach
  void setUp() {
    Testcontainers.exposeHostPorts(port);
    Testcontainers.exposeHostPorts(mailHogContainer.getMappedPort(1025));
    Testcontainers.exposeHostPorts(mailHogContainer.getMappedPort(8025));
    this.userService.setHost(String.format("http://%s:%d/app", GenericContainer.INTERNAL_HOST_HOSTNAME, port));
  }

  @Test
  void runElectronTests() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try (GenericContainer container = createCypressContainer(countDownLatch)) {

      container.start();
      countDownLatch.await(MAX_TOTAL_TEST_TIME_IN_MINUTES, TimeUnit.MINUTES);

      assertThat(container.getLogs()).contains("(Run Finished)");
      String[] formattedOutput = container.getLogs().replace("?", "-").split("\\(Run Finished\\)\n\n");
      assertThat(formattedOutput).hasSize(2);
      assertThat(formattedOutput[1]).contains("All specs passed!");
    }
  }


  @Disabled
  @Test
  void runChromeTests() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try (GenericContainer container = createCypressContainer(countDownLatch, "chrome")) {

      container.start();
      countDownLatch.await(MAX_TOTAL_TEST_TIME_IN_MINUTES, TimeUnit.MINUTES);

      assertThat(container.getLogs()).contains("(Run Finished)");
      String[] formattedOutput = container.getLogs().replace("?", "-").split("\\(Run Finished\\)\n\n");
      assertThat(formattedOutput).hasSize(2);
      assertThat(formattedOutput[1]).contains("All specs passed!");
    }
  }


  @Disabled
  @Test
  void runEdgeTests() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try (GenericContainer container = createCypressContainer(countDownLatch, "edge"
      // , "src/e2e/**/role-management.cy.ts"
                                                            )) {

      container.start();
      countDownLatch.await(MAX_TOTAL_TEST_TIME_IN_MINUTES, TimeUnit.MINUTES);

      assertThat(container.getLogs()).contains("(Run Finished)");
      String[] formattedOutput = container.getLogs().replace("?", "-").split("\\(Run Finished\\)\n\n");
      assertThat(formattedOutput).hasSize(2);
      assertThat(formattedOutput[1]).contains("All specs passed!");
    }
  }

  private GenericContainer createCypressContainer(CountDownLatch countDownLatch, String browser, String specPattern) {
    GenericContainer genericContainer = new GenericContainer<>("cypress/included:%s".formatted(cypressVersion))
      //
      .withCommand("--browser", !isEmpty(browser) ? browser : "electron")
      .withAccessToHost(true)
      .withFileSystemBind("../../", "/e2e", BindMode.READ_WRITE)
      .withWorkingDirectory("/e2e/apps/app-e2e")
      .withEnv("CYPRESS_baseUrl", String.format("http://%s:%d/app", GenericContainer.INTERNAL_HOST_HOSTNAME, port))
      .withEnv("CYPRESS_apiUrl", String.format("http://%s:%d/api", GenericContainer.INTERNAL_HOST_HOSTNAME, port))
      .withEnv("CYPRESS_emailUrl",
        String.format("http://%s:%d", GenericContainer.INTERNAL_HOST_HOSTNAME, mailHogContainer.getMappedPort(8025)))
      .withLogConsumer(outputFrame -> {
        String output = outputFrame.getUtf8String().replace("\n", "").replace("?", "-");
        switch (outputFrame.getType()) {
          case STDOUT -> {
            ArrayList<String> skippedLines = Lists.newArrayList("┐", "┘", "┤", "39m─────────────────────");
            if (!isEmpty(output) //
              && skippedLines.stream().noneMatch(output::contains)//
              && (!output.contains("-----------------") || output.contains("----------------------------------"))) {
              log.at(Level.INFO).log(output);
            }
          }
          case STDERR -> log.at(Level.WARNING).log(output);
          case END -> {
            log.at(Level.INFO).log(outputFrame.getType().name());
            countDownLatch.countDown();
          }
        }
      });

    if (!isEmpty(specPattern)) {
      genericContainer.withCommand("--spec", specPattern);
    }
    if (!isEmpty(CYPRESS_RECORD_KEY)) {
      genericContainer.withCommand("--record").withEnv("CYPRESS_RECORD_KEY", CYPRESS_RECORD_KEY);
    }
    if (isTrue(CYPRESS_SAVE_VIDEO)) {
      genericContainer.withEnv("CYPRESS_video", "true");
    }
    return genericContainer;
  }

  private GenericContainer createCypressContainer(CountDownLatch countDownLatch, String browser) {
    return createCypressContainer(countDownLatch, browser, null);
  }

  private GenericContainer createCypressContainer(CountDownLatch countDownLatch) {
    return createCypressContainer(countDownLatch, null, null);
  }


  public String getNodeDependencyVersion(String devDependency) {
    String filePath = "../../package.json";
    String version = "latest";
    try (FileReader reader = new FileReader(filePath); JsonReader jsonReader = Json.createReader(reader)) {
      JsonObject jsonObject = jsonReader.readObject();
      JsonObject dependencies = jsonObject.getJsonObject("devDependencies");
      for (Map.Entry<String, JsonValue> entry : dependencies.entrySet()) {
        if (entry.getKey().equals(devDependency)) {
          version = entry.getValue().toString().replaceAll("[^\\d.]", "");
        }
      }
    } catch (IOException e) {
      log.at(Level.WARNING).withCause(e).log("Error reading package.json");
    }
    return version;
  }
}
