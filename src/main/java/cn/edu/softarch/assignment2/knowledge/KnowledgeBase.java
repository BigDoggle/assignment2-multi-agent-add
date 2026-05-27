package cn.edu.softarch.assignment2.knowledge;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record KnowledgeBase(String addMethod, String hotelPricingSystem, String systemPolicy) {
    public static KnowledgeBase fromClasspath() {
        return new KnowledgeBase(
                load("/knowledge/add-3.0.md"),
                load("/knowledge/hotel-pricing-system.md"),
                load("/prompts/system-policy.md")
        );
    }

    public String combined() {
        return "# Prior Knowledge\n\n" + addMethod + "\n\n" + hotelPricingSystem + "\n\n" + systemPolicy;
    }

    private static String load(String path) {
        try (var stream = KnowledgeBase.class.getResourceAsStream(path)) {
            Objects.requireNonNull(stream, "Missing classpath resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + path, e);
        }
    }
}
