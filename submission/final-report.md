# Software Architecture Assignment 2 Report

Selected Task: Option 3. Multi-agent (Distributed reasoning + collaborative verification)

## I. Output Results of ADD

### ADD Step 1:

Before starting the iterative design work, the system reviewed all inputs explicitly provided by the assignment package: the ADD 3.0 method, the Hotel Pricing System case, the four-iteration plan, the required quality attributes, the architectural concerns, the system constraints, and the selected assignment setup of Multi-agent + Qwen3-Max + Spring AI Alibaba. Because the case is defined as a greenfield replacement project, the first architectural refinement target had to be the system as a whole rather than an existing subsystem.

The review of inputs also showed that the main architectural pressures are tightly connected rather than independent. The primary functions define the behavioral scope, but the quality attributes and constraints determine how those functions should be organized. Price publication introduces reliability pressure, price query introduces availability and scalability pressure, identity handling introduces security boundaries, and the cloud-native plus REST-first constraints introduce pressure for modularity, portability, and future protocol extensibility. Therefore, every later ADD step had to be grounded in the same bounded input set and could not rely on external domain assumptions, product knowledge, or handcrafted examples.

### 1) Output results of each step (Iteration 1: Establishing an Overall System Structure)

#### ADD Step 2:

The first iteration selected the following primary architectural drivers:

- CRN-1: establish an initial system structure;
- QA-3: availability;
- QA-4: scalability;
- QA-5: security;
- QA-6: modifiability;
- CON-5: REST first while allowing future protocol extension;
- CON-6: cloud-native preference.

These drivers were chosen because the first iteration had to answer the broadest structural question: what major parts the system should contain and why those boundaries are necessary. Availability and scalability argued against collapsing all responsibilities into one request-processing core. Security argued for explicit boundary control near system entry. Modifiability argued for separating protocol-facing logic from business logic so that future non-REST interaction modes could be introduced without rewriting the core pricing responsibilities.

#### ADD Step 3:

Because the assignment describes a greenfield system, the selected element for refinement was the complete system. This is consistent with ADD 3.0 practice: before lower-level refinement can happen, the architecture first needs a stable top-level decomposition. Selecting the whole system also avoided premature commitment to detailed components before the major structural boundaries were justified.

#### ADD Step 4:

The selected design concept was a layered and cloud-oriented structure with explicit separation among external interaction, business logic, and external integration. This concept was chosen because it directly supports the combination of security, scalability, availability, and protocol-modifiability concerns identified from the input.

Alternative directions were less suitable under the assignment rules. A flat use-case-oriented structure would make protocol extension and security isolation difficult. A structure dominated by external integration concerns would weaken the internal business core needed for pricing simulation, price change, permission control, and hotel management. The chosen layered structure therefore provided the best architectural basis for the later iterations.

Another reason for preferring this concept is that the first iteration needed a structure that could absorb later refinements without forcing a redesign. If the initial concept had been centered on one dominant function, such as query or publication, the later iterations would have had to retrofit other concerns into an already biased structure. The layered concept avoids that problem because it separates responsibilities at the right abstraction level: interaction at the boundary, business logic at the center, and external system contact at the edge.

This design concept also reduces architectural ambiguity. When a later requirement or quality concern appears, the team can ask whether it belongs to the interface boundary, the domain core, or the integration side. That question is much easier to answer in a layered structure than in a structure where all concerns are blended into one application core. In that sense, the selected concept serves not only runtime qualities but also later design decision consistency.

#### ADD Step 5:

The main top-level elements instantiated in this iteration were:

- User Interface Layer;
- API Gateway / Protocol Adapters;
- Authentication and Authorization Service;
- Core Pricing Domain;
- Hotel and Rate Management;
- Publication Subsystem;
- Query Subsystem.

These elements intentionally remained coarse-grained. The purpose of the first iteration was not to fully specify every responsibility, but to establish a stable architectural frame. By keeping the structure broad at this stage, later iterations could refine behavior, runtime quality paths, and development concerns without needing to replace the original decomposition.

This coarse-grained instantiation also supports architectural traceability. Each top-level element corresponds to a recurring pressure from the assignment inputs: security, business responsibility, query demand, publication demand, or integration demand. Because those pressures were already visible in the input review, the first iteration could justify every major element without inventing lower-level details that the assignment had not yet asked the design to settle.

In practical terms, this step created the minimum stable set of architectural containers needed for later refinement. It is easier to add finer internal responsibilities inside a clear top-level element than to restructure the system after functional and quality decisions have already accumulated. Therefore, the first iteration deliberately optimized for future refinement capacity rather than immediate detail density.

#### ADD Step 6:

The structural view created in this iteration can be described textually as follows:

1. User requests enter the system from browsers or external API clients.
2. Requests are first received by the API Gateway / Protocol Adapters.
3. Authentication and authorization checks are applied close to the entry boundary.
4. Valid requests are forwarded into the User Interface Layer and then into the Core Pricing Domain.
5. The Core Pricing Domain coordinates pricing-related business logic.
6. Hotel and Rate Management supports hotel metadata, room types, rates, and pricing-rule administration.
7. The Core Pricing Domain delegates publication responsibilities to the Publication Subsystem.
8. The Core Pricing Domain delegates read-oriented price access responsibilities to the Query Subsystem.
9. The Publication Subsystem communicates with the Channel Management System.
10. The Query Subsystem serves query-facing consumers through the chosen interface boundary.

The key decisions recorded in this step were to keep security near the system boundary, isolate protocol handling from core domain logic, and treat publication and query as distinct structural concerns. This view also established the vocabulary that would later be refined into more specific components without changing the overall architectural shape.

#### ADD Step 7:

The first iteration achieved its target. The architecture now had a stable top-level decomposition that could explain the major parts of the system and the reason each part exists. The design was still intentionally abstract, but it answered the essential first-iteration question of overall system structure.

At the same time, the analysis showed a clear remaining gap: the architecture could explain the main zones of the system, but it could not yet state which concrete element would own each required business function. That unresolved question directly motivated Iteration 2.

The first iteration can therefore be considered successful not because it answered every design question, but because it reduced uncertainty in the right way. After this step, the team no longer needed to debate the overall shape of the system. The unresolved issues were narrower and more actionable: function allocation, quality-path refinement, and operational support. This is exactly the kind of narrowing that ADD intends to achieve through early iterations.

### 2) Output results of each step (Iteration 2: Identifying Structures to Support Primary Functionality)

#### ADD Step 2:

The second iteration selected the six primary functional requirements as the main drivers:

- HPS-1 Log In;
- HPS-2 Change Prices;
- HPS-3 Query Prices;
- HPS-4 Manage Hotels;
- HPS-5 Manage Rates;
- HPS-6 Manage Users.

QA-5 Security and QA-6 Modifiability were also retained as supporting drivers because functional allocation cannot be separated from access control and protocol isolation. This iteration was critical because the case includes several distinct forms of behavior, and each behavior needed an explicit architectural owner rather than being buried inside a large undifferentiated service.

#### ADD Step 3:

The selected refinement targets were the functional elements inside the previously established architecture, especially:

- Authentication Gateway;
- Price Query Handler;
- Rate Editor;
- Price Read Service;
- Hotel Manager;
- Rate Rule Manager;
- Permission Manager;
- Price Publisher.

This element selection also clarified which use cases share a path and which should remain separate. Price change and price query both deal with pricing information, but they have different architectural pressures. Price change emphasizes controlled updates, simulation, and reliable external publication, while price query emphasizes efficient retrieval and highly available service.

#### ADD Step 4:

The chosen design concept was functional decomposition inside the layered structure established in Iteration 1. Each major business capability received a clear owner component, while protocol handling remained outside the domain core and external communication remained on the integration side.

This concept reduced responsibility overlap and improved maintainability. Login and authorization, hotel administration, rate management, permission management, price query, and price publication became related but distinct concerns. The architecture therefore became easier to reason about, easier to extend, and better prepared for later quality-attribute refinement.

The functional decomposition concept was also preferable because the assignment includes both operational functions and administrative functions. If all of them were merged into a generic business service, the architecture would quickly accumulate hidden coupling among user management, hotel management, rate administration, query serving, and publication flow. By assigning them to explicit owners, the report makes clear not only where each use case is handled, but also where it is not handled.

This step also improves design review quality. When functional ownership is explicit, later analysis of reliability, availability, testability, or monitorability can refer to named elements instead of vague “system behavior.” That precision is important in an academic architecture report because it makes every later refinement easier to justify against the earlier design record.

#### ADD Step 5:

The main functional allocation decided in this step was:

- login and access control through the Authentication Gateway;
- price update and simulation through the Rate Editor;
- price retrieval through the Price Query Handler and Price Read Service;
- hotel administration through the Hotel Manager;
- rate and rule administration through the Rate Rule Manager;
- permission changes through the Permission Manager;
- outbound publication through the Price Publisher.

After this step, the architecture moved from broad structural zones to operationally meaningful elements. This was important because later runtime-quality work depends on knowing which concrete elements sit on the critical execution paths.

The instantiation also strengthened cohesion. The Rate Editor is not merely a convenient label; it groups change-oriented pricing responsibilities that naturally belong together. The same logic applies to the Price Read Service, the Permission Manager, and the Hotel Manager. This cohesion reduces the likelihood that later modifications will spread logic across unrelated modules, which directly supports modifiability and team comprehension.

At the same time, the architecture preserved separation between user-facing request handling and internal business ownership. That separation matters because some elements exist mainly to receive requests, while others exist to apply domain rules. Keeping those roles distinct provides a cleaner basis for both future protocol extension and later runtime optimization.

#### ADD Step 6:

The functional view created in this iteration can be summarized textually as follows:

1. The External Interface Layer contains the Authentication Gateway and the Price Query Handler.
2. The Authentication Gateway is responsible for login access control and authorization enforcement at the system boundary.
3. The Price Query Handler receives query-oriented requests and forwards them to the internal read path.
4. The Core Business Logic Layer contains the Rate Editor, Price Read Service, Hotel Manager, Rate Rule Manager, and Permission Manager.
5. The Rate Editor owns price-change and simulation responsibilities.
6. The Price Read Service owns the internal retrieval of pricing data.
7. The Hotel Manager owns hotel administration.
8. The Rate Rule Manager owns rate definition and pricing-rule management.
9. The Permission Manager owns user-permission updates.
10. The Integration Layer contains the Price Publisher, which is responsible for sending published pricing results to the Channel Management System.

The main decisions recorded here were that every primary function must map to at least one explicit element, that protocol and authentication concerns should remain outside the domain core, and that administrative, query, and publishing responsibilities should not collapse into one large service.

#### ADD Step 7:

The second iteration met its target successfully. All required major behaviors now had explicit architectural ownership, and the results remained consistent with the top-level structure created in Iteration 1. No contradiction or structural reset was needed.

The analysis also made the next challenge clearer. Once functional ownership was explicit, it became possible to identify which elements carried the highest runtime pressure. In particular, publication reliability and query availability now sat on identifiable paths, which created the basis for Iteration 3.

This means the second iteration reached a productive stopping point. The team did not yet know every detail of runtime hardening, but it now knew exactly where runtime hardening had to occur. That is a strong sign of architectural progress because the remaining uncertainty had shifted from “who owns this behavior?” to “how should this already-identified behavior be strengthened under quality scenarios?”

### 3) Output results of each step (Iteration 3: Addressing Reliability and Availability Quality Attributes)

#### ADD Step 2:

The third iteration selected the following drivers:

- QA-2 Reliability;
- QA-3 Availability;
- HPS-2 as the functional anchor for reliable price publication;
- HPS-3 as the functional anchor for highly available price query;
- CON-6 as the cloud-native constraint.

These drivers were chosen because the most demanding runtime qualities in the assignment are not spread evenly across the system. They are concentrated mainly in the publication path and the query path. The purpose of this iteration was therefore to refine those quality-critical paths rather than to redesign the whole system again.

#### ADD Step 3:

The selected elements for refinement were:

- the Price Publishing Component;
- the Price Query Component.

These elements sit directly on the path of the two relevant quality scenarios. Reliability depends on whether price changes can be delivered outward in a controlled and traceable way, and availability depends on whether price queries can continue to be served under the required runtime pressure. Focusing on these elements kept the iteration disciplined and scenario-driven.

#### ADD Step 4:

The selected design concepts were:

- decoupled publication flow for reliability;
- durable and traceable publication handoff;
- redundant and horizontally scalable query handling for availability;
- continued alignment with the cloud-native structural preference.

The report intentionally remains technology-neutral because the assignment prohibits introducing external product knowledge. Even so, the structural meaning is clear. Reliable publication requires an explicit and traceable handoff model rather than an implicit update path. Highly available query service requires a structure that can be duplicated and scaled without coupling reads to slower administrative or publication-side behavior.

These concepts were also selected because they respect the architecture already established in earlier iterations. The report does not introduce an entirely new subsystem solely for quality attributes. Instead, it refines the previously identified publication and query elements. That continuity matters because a good ADD result should show accumulated refinement, not repeated replacement of the architectural baseline.

Another important justification is that reliability and availability pull the architecture in different directions. Reliability favors stronger control and traceability on the publication side, while availability favors lighter and more replicable handling on the query side. Treating them as different architectural paths avoids the common mistake of applying one quality strategy uniformly to all behavior even when the underlying scenarios are different.

#### ADD Step 5:

The publication side was refined into a path that separates price-change initiation from downstream publication. The query side was refined into a path that allows duplicated query-serving logic without changing the core business rules.

This refinement strengthened the distinction between correctness-sensitive operations and availability-sensitive operations. Publication is correctness-critical because every successful change must ultimately reach the external channel management side. Query is availability-critical because clients must continue to retrieve pricing information under the target service level. Keeping these paths separate makes the architecture more defensible and easier to evolve.

The instantiation in this iteration also improves analytical clarity. Once publication and query have separate structural paths, the report can explain failure impact, recovery expectation, and scalability expectation more precisely. Publication failures are evaluated in terms of correctness and traceability, while query stress is evaluated in terms of service continuity and response capacity. This is much stronger than treating “pricing” as one undifferentiated workload.

From a maintainability perspective, this separation also prevents optimization in one path from distorting another. Query-side replication should not force publication behavior to become eventually uncontrolled, and publication-side correctness mechanisms should not unnecessarily slow down read-heavy traffic. The architecture therefore becomes more balanced by giving each quality-critical path room to evolve according to its own dominant concern.

#### ADD Step 6:

The runtime-quality view created in this iteration can be summarized textually as follows:

1. A price-change operation starts from the Rate Editor.
2. The Rate Editor forwards publication-related work to the Price Publishing Component.
3. The Price Publishing Component passes the change through a durable publication handoff before delivery to the Channel Management System.
4. This publication path is treated as the reliability-sensitive execution path of the architecture.
5. On the query side, Query Clients send requests to the Price Query Component.
6. The Price Query Component delegates query-serving work to a replicated query service.
7. The replicated query service reads from the pricing data source through the designated read path.
8. This query path is treated as the availability-sensitive execution path of the architecture.

The main decisions recorded here were to separate publication and query as different quality-critical paths, to make publication flow explicit and traceable, and to support query availability through structurally replicable serving elements instead of overloading the write-oriented path.

#### ADD Step 7:

The third iteration successfully strengthened the design for reliability and availability. The architecture could now explain not only which components serve the main functions, but also which paths must be hardened and why.

This analysis also showed that one major dimension was still incomplete: development and operations. The architecture now addressed structure, functional allocation, and runtime quality, but it still needed explicit treatment of deployability, monitorability, and testability. That gap motivated Iteration 4.

The stopping point of Iteration 3 was therefore appropriate. The report had already identified the most important runtime paths and clarified the structural choices needed to support them. Extending the same iteration further into deployment, telemetry, and testing concerns would have mixed two different refinement goals. By ending the iteration here, the ADD process preserved clean focus and left operational refinement to a dedicated final pass.

### 4) Output results of each step (Iteration 4: Addressing Development and Operations)

#### ADD Step 2:

The final iteration selected the following drivers:

- QA-7 Deployability;
- QA-8 Monitorability;
- QA-9 Testability;
- CRN-3 Team work allocation;
- CRN-4 Avoidance of technical debt;
- CRN-5 Continuous deployment infrastructure;
- CON-3 Proprietary Git-based hosting;
- CON-4 Schedule pressure;
- CON-6 Cloud-native preference.

This driver set shifted attention from runtime execution to long-term delivery, verification, and maintainability. The assignment is not satisfied only by a structure that works conceptually; it also needs a structure that can be deployed, observed, tested, and developed under realistic project constraints.

#### ADD Step 3:

The selected refinement targets were:

- the Price Publishing Service, because QA-8 explicitly depends on publication metrics;
- deployment packaging and configuration elements, because QA-7 depends on environment portability;
- integration-test boundaries, because QA-9 requires independence from real external systems.

This selection kept the iteration connected to the previous design. Instead of inventing an unrelated “operations architecture,” the report refined already meaningful parts of the existing system.

#### ADD Step 4:

The selected design concepts were:

- externalized configuration for environment portability;
- explicit telemetry emission for publication monitoring;
- replaceable adapters and test seams around external dependencies;
- modular work boundaries that support parallel development and reduce design erosion.

These concepts respond directly to the listed concerns. Clear module boundaries support parallel work allocation. Externalized configuration supports repeatable deployment. Telemetry supports monitorability. Adapter boundaries support independent testing and reduce the risk of technical debt caused by tightly coupled integrations.

This concept set was also chosen because development and operations concerns are cumulative rather than isolated. A system is easier to deploy repeatedly when configuration is externalized, easier to observe when telemetry is explicit, and easier to test when external dependencies are isolated. These are different concerns, but they reinforce one another structurally. The architecture therefore benefits from treating them together in the final iteration rather than scattering them across unrelated earlier decisions.

Another reason for this design choice is project sustainability. The assignment case mentions schedule pressure, team work allocation, and continuous delivery concerns. Those pressures cannot be answered only by runtime structure. They require design decisions that reduce hidden coupling, make environment-specific behavior explicit, and create boundaries that support safe change. The selected concepts directly support those longer-term project needs.

#### ADD Step 5:

The architecture was refined with the following elements:

- a configuration boundary so that environment changes do not require code changes;
- telemetry emitters around the publication flow;
- adapter-style boundaries around the identity service and the Channel Management System;
- explicit test boundaries that allow integration-oriented verification without always depending on real external systems.

These refinements do not replace business logic. Instead, they make the business architecture deployable, observable, and testable in a controlled way.

This step also strengthens the relationship between architecture and implementation workflow. A configuration boundary supports environment transitions without code edits. Telemetry around publication provides visible evidence for operational behavior. Adapter boundaries let tests focus on internal logic without depending on real external services in every run. Together, these choices reduce friction not only for operation, but also for development, debugging, and verification.

The architecture at this point is therefore more than functionally complete. It is structured in a way that can support repeated execution, controlled release, and incremental maintenance. That is an important outcome because the final iteration is meant to show that the architecture can survive practical project use rather than exist only as a conceptual model.

#### ADD Step 6:

The development-and-operations view created in this iteration can be summarized textually as follows:

1. The Price Publishing Service depends on a configuration boundary so that deployment-environment changes do not require source-code modification.
2. The Price Publishing Service emits operational data through a telemetry boundary.
3. External publication is isolated through a Channel Management System Adapter.
4. Identity-related integration is isolated through a User Identity Service Adapter.
5. Authentication and permission logic depend on adapter boundaries rather than directly depending on external services.
6. An Integration Test Harness can exercise these boundaries independently, which improves testability and reduces coupling to real third-party systems during verification.

The key decisions recorded here were to obtain portability from configuration rather than code changes, to embed publication observability inside the architecture rather than adding it later, and to isolate external systems through adapters so that monitoring, deployment, and testing become first-class architectural concerns.

#### ADD Step 7:

The fourth iteration completed the assignment scope. The final architecture now covers the required top-level structure, primary functionality, reliability and availability concerns, and the development-and-operations concerns of deployability, monitorability, and testability.

The most important result is the consistency across iterations. Each iteration introduced additional refinement pressure, but no later iteration invalidated the earlier structural choices. Instead, the architecture evolved from top-level decomposition to functional mapping, then to runtime hardening, and finally to operational readiness.

This final stopping point is justified because the architecture now answers all questions explicitly posed by the assignment template. It explains the overall structure, functional ownership, reliability and availability treatment, and development-and-operations support. Further detail could still be added in a real project, but it would belong to detailed design or implementation planning rather than the required scope of this architecture report.

## II. Interaction Cost Analysis

### The way of completing the assignment

The assignment was completed with a Multi-agent approach. The five working roles were Coordinator, Architect, Quality Analyst, Reviewer, and Recorder. These roles were not treated as separate knowledge sources. They were separate reasoning viewpoints over the same bounded assignment material, which improved process discipline and traceability.

### The LLM used

The base large language model used in the assignment was Qwen3-Max. The agent framework used to organize the workflow and model calls was Spring AI Alibaba.

### Number of Human Interactions (turns)

For the final submission-stage accounting used in this report, the number of human interactions was 4 turns:

1. configure the DashScope API key;
2. start the formal full run;
3. inspect the generated outputs;
4. organize the final submission materials.

This counting method intentionally measures only the human actions directly related to the final assignment-generation stage rather than the earlier development-and-debugging phase.

### Token Consumption (K tokens)

The measured token consumption of the final formal run was 418K tokens.

This amount is consistent with the chosen paradigm. The multi-agent workflow records 4 iterations, 6 ADD steps per iteration from Step 2 to Step 7, and 5 agent roles per step, for a total of 120 recorded model turns. The purpose of this additional token usage is not to create more knowledge, but to create clearer structure, stronger self-checking, and better traceability.

### Time Cost (min)

The measured end-to-end time cost of the final formal run was 49:08 minutes, which is approximately 49.13 minutes.

The runtime is acceptable for this assignment because the chosen paradigm emphasizes architectural discipline and evidence preservation rather than minimal execution latency. The full log demonstrates that every required iteration and every required ADD step was completed and recorded with timestamps.

The main benefit of the multi-agent approach was process discipline. The coordinator preserved step alignment, the architect emphasized structure and responsibility allocation, the quality analyst repeatedly checked quality-attribute fitness, the reviewer constrained unsupported assumptions, and the recorder improved traceability. The main cost was output verbosity: a complete multi-agent execution trace is useful as evidence, but it must be distilled before it can become a concise report.

Another important observation is that the multi-agent workflow naturally produces representational repetition. Several agents may restate similar driver selections or structural conclusions because they are reasoning from the same bounded material. This redundancy is useful during execution because it supports verification, but it is not suitable as a final report format. Therefore, a meaningful portion of the assignment effort lies in consolidating overlapping outputs into one coherent architectural narrative.

## III. Individual Reflection

### 1) The problems encountered and the solutions adopted

The first major problem was how to keep the implementation strictly aligned with the assignment constraints. The case explicitly prohibits external domain knowledge, few-shot examples, and silent requirement reinterpretation. A general-purpose model can easily drift into those behaviors if its instructions are not tightly bounded. To address this, the prompts and role definitions were constrained by an explicit system policy that repeatedly anchored every agent to the supplied ADD method, case material, iteration plan, and rule set.

The second major problem was how to handle the very long multi-agent execution trace. A full successful run contains 120 recorded model turns, which is excellent as evidence but unsuitable as a final report format. The adopted solution was to preserve the full timestamped logs as a separate deliverable and then distill the architecture results into a concise report aligned with the official appendix template. This separation of raw evidence and final narrative made the submission both complete and readable.

The third problem was the local runtime environment. The project needed a non-default Java setup, and the default machine environment was not immediately suitable for execution. The adopted solution was to verify a working Java and Maven runtime explicitly and to keep the run commands deterministic. This reduced repeated environment-related failure and made the source code easier to reproduce.

The fourth problem was presentation quality. The model-generated draft could contain repeated explanations, layout noise, and section ordering that did not directly match the assignment template. The solution was not to invent new content, but to reorganize the already generated architecture results into the required report structure, tighten the language, and clearly separate final deliverables from intermediate artifacts.

### 2) A detailed account of your personal contributions to the group work

The group work was divided across implementation, verification, document consolidation, and submission preparation. The most important practical lesson from this collaboration is that completing an AI-assisted software-architecture assignment is not just an implementation task. It also requires prompt-boundary control, result verification, artifact organization, and report rewriting so that the final deliverables remain both accurate and submission-ready.

The contributions of the three members are summarized below.

#### Name (Chinese) / Contributions

- 王景宣: responsible for leading the implementation, completing environment setup and troubleshooting, performing runtime verification, and organizing the final submission materials.
- 李顺: responsible for requirement analysis, prompt-constraint checking, development integration, result inspection, and report organization.
- 张岩: responsible for test verification, review of logs and report materials, submission-directory organization, and delivery-consistency checking.
