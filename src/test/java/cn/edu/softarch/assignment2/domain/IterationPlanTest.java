package cn.edu.softarch.assignment2.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IterationPlanTest {
    @Test
    void defaultPlanContainsFourAssignmentIterations() {
        assertThat(IterationPlan.assignmentDefault())
                .extracting(IterationPlan::title)
                .containsExactly(
                        "Establishing an Overall System Structure",
                        "Identifying Structures to Support Primary Functionality",
                        "Addressing Reliability and Availability Quality Attributes",
                        "Addressing Development and Operations"
                );
    }
}
