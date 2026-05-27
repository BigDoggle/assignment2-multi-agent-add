package cn.edu.softarch.assignment2.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentPropertiesTest {
    @Test
    void bindsAssignmentPropertiesFromYaml() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> sources = loader.load("application", new ClassPathResource("application.yml"));
        sources.forEach(environment.getPropertySources()::addLast);

        AssignmentProperties properties = Binder.get(environment)
                .bind("assignment", Bindable.of(AssignmentProperties.class))
                .orElseThrow(() -> new IllegalStateException("assignment properties were not bound"));

        assertThat(properties.model()).isEqualTo("qwen3-max");
        assertThat(properties.outputDir()).isEqualTo("out/assignment2");
    }
}
