package dev.kurama.api.pact;

import dev.kurama.api.core.hateoas.processor.BuildInfoModelProcessor;
import dev.kurama.api.core.rest.BuildInfoController;
import dev.kurama.support.ImportMappers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"application.version=0.0.0-SNAPSHOT", "application.run=420-1",
  "application.stage=feature", "git.branch=devops/UM-60", "git.build.time=2022-10-23T13:20:09+0200"})
@WebMvcTest(controllers = BuildInfoController.class)
@Import({BuildInfoModelProcessor.class})
@ImportMappers
public class BuildInfoControllerBase extends PactBase {

}
