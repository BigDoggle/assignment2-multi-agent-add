package cn.edu.softarch.assignment2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Runtime settings owned by the assignment workflow.
 */
@ConfigurationProperties(prefix = "assignment")
public record AssignmentProperties(String model, String outputDir) {
}
