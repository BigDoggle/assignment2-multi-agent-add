package cn.edu.softarch.assignment2.knowledge;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeBaseTest {
    @Test
    void loadsAssignmentKnowledgeAndPolicy() {
        KnowledgeBase base = KnowledgeBase.fromClasspath();

        assertThat(base.addMethod()).contains("Step 1 Review Inputs");
        assertThat(base.hotelPricingSystem()).contains("QA-3 Availability");
        assertThat(base.systemPolicy()).contains("AI paradigm: multi-agent");
        assertThat(base.combined()).contains("Qwen3-Max");
    }
}
