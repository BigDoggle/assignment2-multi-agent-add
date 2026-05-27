package cn.edu.softarch.assignment2.domain;

import java.util.List;

/**
 * Describes one ADD design iteration and the ADD steps executed in it.
 */
public record IterationPlan(int number, String title, List<AddStep> steps) {
    public IterationPlan {
        steps = List.copyOf(steps);
    }

    /**
     * Returns the four required assignment iterations in their execution order.
     */
    public static List<IterationPlan> assignmentDefault() {
        List<AddStep> iterationSteps = List.of(
                AddStep.STEP_2_SELECT_DRIVERS,
                AddStep.STEP_3_SELECT_ELEMENTS,
                AddStep.STEP_4_SELECT_DESIGN_CONCEPTS,
                AddStep.STEP_5_INSTANTIATE_ELEMENTS,
                AddStep.STEP_6_SKETCH_VIEWS_AND_RECORD_DECISIONS,
                AddStep.STEP_7_ANALYZE_DESIGN
        );
        return List.of(
                new IterationPlan(1, "Establishing an Overall System Structure", iterationSteps),
                new IterationPlan(2, "Identifying Structures to Support Primary Functionality", iterationSteps),
                new IterationPlan(3, "Addressing Reliability and Availability Quality Attributes", iterationSteps),
                new IterationPlan(4, "Addressing Development and Operations", iterationSteps)
        );
    }
}
