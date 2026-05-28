# Software Architecture Assignment 2 Report

## 1. Introduction

This assignment was completed with a multi-agent approach using Qwen3-Max as the base large language model and Spring AI Alibaba as the agent framework. The target problem is the Hotel Pricing System, and the design process follows the ADD 3.0 method exactly as required by the assignment.

The implementation was intentionally constrained by the assignment rules. The agents were only allowed to use the provided ADD method description, the Hotel Pricing System case, the iteration plan, and the explicit system policy. No external domain knowledge, no hand-written examples, and no additional business assumptions were introduced.

The purpose of the implementation was twofold:

1. to execute the four required ADD iterations through a structured multi-agent workflow; and
2. to preserve the complete reasoning process as timestamped logs that can be inspected and reused for the final report.

The five agent roles used in the workflow were:

- Coordinator
- Architect
- Quality Analyst
- Reviewer
- Recorder

These roles were not treated as separate knowledge sources. Instead, they were separate viewpoints over the same provided assignment material, used to improve structure, self-checking, and traceability.

## 2. ADD Step 1 Review of Inputs

Before starting the iterative design work, the system reviewed all provided inputs:

- the ADD 3.0 method steps;
- the Hotel Pricing System design purpose;
- the six primary functions HPS-1 to HPS-6;
- the quality attributes QA-1 to QA-9;
- the architectural concerns CRN-1 to CRN-5;
- the constraints CON-1 to CON-6;
- the selected assignment setup:
  - Multi-agent
  - Qwen3-Max
  - Spring AI Alibaba

The design purpose clearly states that this is greenfield development intended to replace an existing system. This directly affects ADD Step 3 because the system itself must be selected first for refinement before moving to lower-level elements.

The most important functional scope can be summarized as:

- user login and authorization;
- price change, simulation, and publication;
- price query through UI and API;
- hotel data management;
- rate and rule management;
- user permission management.

The major architectural pressures identified from the input were:

- reliability of price publication;
- availability and scalability of price query;
- secure authorization boundaries;
- modifiability for future non-REST protocols;
- deployability, monitorability, and testability under a cloud-native constraint.

These inputs also implied an important reporting principle for the whole assignment: each iteration had to be justified only with the information explicitly present in the case. That meant the design process could not rely on undocumented business assumptions, specific middleware products, or technology-specific optimizations. As a result, the report emphasizes structural reasoning, responsibility allocation, interface boundaries, and quality-attribute alignment rather than product-level implementation decisions.

### 2.1 Cross-Cutting Interpretation of the Inputs

The provided inputs were not independent lists. In practice, they formed a set of interdependent architectural pressures that had to be interpreted together. For example, the primary functions define what the system must do, but they do not by themselves determine a good structure. That structural pressure comes from the interaction between functionality and quality attributes. HPS-2 Change Prices is not only a business function; it is also the main carrier of performance, reliability, and monitorability concerns. Likewise, HPS-3 Query Prices is not only a user-facing capability; it is also the main carrier of availability, scalability, and protocol-extension concerns.

The same cross-cutting logic applies to the constraints. CON-5 does not merely say that the first version uses REST. It also implies that transport concerns must not dominate the core of the architecture, because another protocol may later be added. CON-6 does not merely suggest a deployment preference. It also reinforces architectural separation, because portability, deployability, and repeatability are easier to achieve when configuration, business logic, and integration responsibilities are clearly bounded. CON-2 influences both security and deployment thinking, because identity integration and cloud hosting affect where trust boundaries and external dependencies appear.

From a reporting perspective, this means the ADD process cannot be read as a sequence of unrelated steps. The architecture evolves by repeatedly revisiting the same bounded input set from different viewpoints. That is why the same functional requirements, quality attributes, concerns, and constraints appear across multiple iterations: each iteration uses them to answer a different architectural question.

## 3. Iteration 1: Establishing an Overall System Structure

### 3.1 Step 2 Select Drivers

The first iteration focused on defining the overall system structure. The key drivers selected in this step were:

- CRN-1: establish an initial system structure;
- CON-6: favor a cloud-native approach;
- QA-3: availability;
- QA-4: scalability;
- QA-5: security;
- QA-6: modifiability;
- CON-5: REST first, while allowing future protocol extension.

The core conclusion was that the architecture had to separate protocol concerns, security boundaries, business logic, and external integration early, because all of these qualities shape the top-level decomposition.

Another key point in this step was that availability, scalability, security, and modifiability are not isolated concerns. For this system, they all influence the first structural cut. Availability and scalability argue against a monolithic request path for every use case. Security argues for clear trust boundaries close to system entry points. Modifiability argues for preventing transport or protocol concerns from leaking into core business logic. Therefore, the selected drivers collectively supported a structure with explicit boundaries rather than a single undifferentiated application core.

### 3.2 Step 3 Select Elements

Because the project is greenfield, the selected element for refinement was the system as a whole. This decision is directly consistent with ADD 3.0 guidance and avoided premature decomposition before the structural goal was clear.

### 3.3 Step 4 Select Design Concepts

The selected design direction was a layered and cloud-native structure with clear separation between:

- external interaction and protocol handling;
- core business logic;
- integration with outside services.

This concept was chosen because it supports the assignment drivers without forcing protocol or infrastructure logic into the core domain.

Alternative directions were implicitly less suitable under the assignment constraints. A structure centered mainly on use-case scripts without clear layer boundaries would make protocol extension and security isolation harder. A structure centered mainly on external integrations would not provide a stable domain core for price simulation, rate management, and permission control. The chosen layered concept therefore offered the best balance between immediate delivery needs and the required architectural qualities.

### 3.4 Step 5 Instantiate Elements

The main top-level elements instantiated in Iteration 1 were:

- User Interface Layer
- API Gateway / Protocol Adapters
- Authentication and Authorization Service
- Core Pricing Domain
- Hotel and Rate Management
- Publication Subsystem
- Query Subsystem

At this stage, the design intentionally stayed coarse-grained. The goal was to establish a stable structural frame rather than detailed component responsibilities.

This was an important ADD decision. Over-specifying detailed elements in the first iteration would have made later functional and quality-attribute refinements harder, because the team would have committed too early to low-level decisions. By keeping the first iteration coarse-grained, the architecture remained flexible enough to support later iterations that would focus on primary functionality, runtime qualities, and development concerns.

### 3.5 Step 6 Sketch Views and Record Decisions

The main structural view produced in this iteration can be described textually as follows:

1. User requests enter the system through the user browser.
2. Requests are first received by the API Gateway / Protocol Adapters.
3. Authentication and authorization are enforced before the request reaches the user-facing application logic.
4. The User Interface Layer coordinates user operations and forwards business work to the internal domain.
5. The Core Pricing Domain handles pricing-related business responsibilities.
6. Hotel and Rate Management handles hotel metadata, rate structures, room types, and related administrative data.
7. The Core Pricing Domain delegates outbound publication responsibilities to the Publication Subsystem.
8. The Core Pricing Domain delegates read-oriented price access responsibilities to the Query Subsystem.
9. The Publication Subsystem communicates with the Channel Management System.
10. The Query Subsystem serves query-facing consumers and clients.

The main design decisions recorded here were:

- keep security near the system boundary;
- isolate protocol handling from business logic;
- separate publication-related processing from query-related processing;
- preserve room for future protocol extension without changing core components.

The resulting view also established the initial vocabulary for later refinements. In later iterations, more specific elements such as authentication gateways, publishers, query handlers, adapters, and telemetry emitters could be introduced without changing the fundamental shape of the system. This continuity is important in ADD because each iteration should refine the current structure rather than replace it unnecessarily.

### 3.6 Step 7 Analyze Current Design

The first iteration successfully produced a stable top-level architecture. It did not yet allocate detailed responsibilities for each use case, but it created the structural base required for later iterations. The design goal of establishing the overall system structure was achieved.

At the end of Iteration 1, the architecture was strong enough to answer the question “What are the major parts of the system and why do they exist?” but not yet detailed enough to answer “Which element performs each required use case?” That remaining question directly motivated Iteration 2, where the focus shifted from broad structural decomposition to explicit support for primary functionality.

## 4. Iteration 2: Identifying Structures to Support Primary Functionality

### 4.1 Step 2 Select Drivers

The second iteration focused on the six primary functional requirements:

- HPS-1 Log In
- HPS-2 Change Prices
- HPS-3 Query Prices
- HPS-4 Manage Hotels
- HPS-5 Manage Rates
- HPS-6 Manage Users

QA-5 and QA-6 were also retained as direct supporting drivers because security and protocol isolation shape how functionality should be allocated.

This iteration was especially important because the assignment case contains six distinct kinds of system behavior, and each one could have been implemented in a tangled or overlapping way if architectural ownership were not made explicit. The purpose of the second iteration was therefore not just to “list components,” but to create a mapping from required business capabilities to stable architectural responsibilities.

### 4.2 Step 3 Select Elements

This iteration refined the specific elements needed to support the functional scope inside the previously defined three-part architecture. The selected elements included:

- Authentication Gateway
- Price Query Handler
- Rate Editor
- Price Read Service
- Hotel Manager
- Rate Rule Manager
- Permission Manager
- Price Publisher

The element selection also made explicit which use cases share architectural pathways and which should remain separated. For example, HPS-2 Change Prices and HPS-3 Query Prices both depend on pricing information, but they place very different demands on the architecture. Price changes require controlled updates, simulation, and reliable publication, while price queries require efficient and available read access. Treating them as different architectural concerns early prevents the read path from being overloaded with write-side responsibilities.

### 4.3 Step 4 Select Design Concepts

The design concept chosen here was functional decomposition within the previously established layers. Each major business capability received a clear owner component, while protocol-facing concerns remained at the outer boundary and integration-facing concerns stayed in the integration side of the structure.

This choice improved clarity in two ways. First, it reduced ambiguity about where new logic should be placed. For example, permission checks belong close to access handling and permission management rather than being scattered through unrelated services. Second, it supported maintainability by minimizing responsibility overlap. Hotel administration, rate administration, permission management, query handling, and price publication were treated as related but distinct concerns, which makes later testing and refinement easier.

### 4.4 Step 5 Instantiate Elements

The main responsibility allocation was:

- login and access control through the authentication gateway;
- price update and simulation through the rate editor;
- price retrieval through the query handler and read service;
- hotel and rate master-data maintenance through dedicated managers;
- permission changes through the permission manager;
- outbound publication through the price publisher.

This decomposition supports the full functional surface of the assignment while preserving clean module boundaries.

From an ADD perspective, this step was where the architecture moved from abstract structure to operational structure. After Iteration 1, the system had top-level zones. After Iteration 2, the system had named elements whose roles corresponded to the required business capabilities. That transition is essential because later quality-attribute work depends on knowing which concrete elements sit on the critical execution paths.

### 4.5 Step 6 Sketch Views and Record Decisions

The functional decomposition view can be summarized textually as follows:

1. The External Interface Layer contains the Authentication Gateway and the Price Query Handler.
2. The Authentication Gateway is responsible for login access control and for enforcing authorization at the system boundary.
3. The Price Query Handler is responsible for receiving query-oriented requests and forwarding them to the internal read path.
4. The Core Business Logic Layer contains the Rate Editor, Price Read Service, Hotel Manager, Rate Rule Manager, and Permission Manager.
5. The Rate Editor owns price-change and simulation-oriented responsibilities.
6. The Price Read Service owns the internal logic for retrieving pricing data.
7. The Hotel Manager owns hotel administration responsibilities.
8. The Rate Rule Manager owns rate definition and pricing-rule management.
9. The Permission Manager owns permission and access-right changes.
10. The Integration Layer contains the Price Publisher, which is responsible for sending published pricing results to external systems.

The main decisions of this iteration were:

- every primary function must map to at least one explicit architectural element;
- authentication and protocol adaptation remain outside the core domain;
- user, hotel, rate, query, and publishing responsibilities should not collapse into a single large service.

An additional benefit of this iteration was that it clarified future team work allocation. Even though the report does not assign implementation tasks to specific source modules here, the decomposition naturally suggests parallel work areas: authentication and access control, query handling, administrative management, price publishing, and external integration. This anticipates later development concerns without prematurely turning the architecture report into a project plan.

### 4.6 Step 7 Analyze Current Design

The second iteration achieved its goal. The design now supports all major required behaviors with explicit element ownership. The architecture remained consistent with the first iteration and did not require structural rework.

However, this iteration also exposed the next major architectural challenge. Once functional ownership became explicit, it became easier to identify where the most demanding runtime quality scenarios live. In particular, reliable publication and highly available query behavior were now tied to recognizable components and paths, which made Iteration 3 both possible and necessary.

## 5. Iteration 3: Addressing Reliability and Availability Quality Attributes

### 5.1 Step 2 Select Drivers

This iteration selected:

- QA-2 Reliability
- QA-3 Availability
- HPS-2 as the functional anchor for publication reliability
- HPS-3 as the functional anchor for query availability
- CON-6 as the cloud-native constraint

These were chosen because reliable price publication and highly available price query are the most demanding runtime qualities in the assignment.

This iteration differs from Iteration 2 in an important way. Iteration 2 asks whether the architecture can perform the required functions. Iteration 3 asks whether the architecture can perform the most demanding functions under the most demanding quality constraints. This shift from functional sufficiency to quality-attribute sufficiency is one of the core strengths of ADD as a design process.

### 5.2 Step 3 Select Elements

The two primary refinement targets were:

- the Price Publishing Component;
- the Price Query Component.

These elements sit directly on the path of the two quality scenarios and therefore determine whether the quality goals are feasible.

Selecting these elements also prevented the quality-attribute discussion from becoming too broad. Reliability and availability could theoretically be discussed at many levels, but in this case the report remains disciplined by focusing on the exact elements that are responsible for price publication and price query. This keeps the iteration grounded in the provided scenarios instead of drifting into generic infrastructure talk.

### 5.3 Step 4 Select Design Concepts

The selected concepts were:

- decoupled publication flow for reliability;
- durable and traceable message handoff for publication;
- redundant and scalable query handling for availability;
- cloud-native deployment assumptions consistent with the assignment constraint.

The implementation deliberately stayed technology-neutral in the report because the assignment prohibits importing outside design knowledge beyond the provided material.

Even without naming external tools, the architectural meaning of these choices is clear. Reliability on the publication side requires an explicit handoff model rather than an implicit “update and hope” flow. Availability on the query side requires a design where query-serving capacity can be replicated and where query handling is not tightly coupled to slower administrative or publication paths. These are structural conclusions derived from the quality scenarios themselves.

### 5.4 Step 5 Instantiate Elements

The publication side was refined into a structure that separates price-change initiation from reliable downstream publication. The query side was refined into a structure that allows horizontal duplication of query-serving logic without changing core domain rules.

This refinement is important because it preserves a distinction between correctness-critical operations and availability-critical operations. Publication is correctness-sensitive: every successful change must be reflected externally and received by the Channel Management System. Query is service-availability-sensitive: clients must continue to retrieve prices under the required SLA. Designing them as separate critical paths gives the architecture a clearer basis for later operational reasoning.

The architectural effect of this iteration was:

- publication became a reliability-focused path with stronger isolation;
- query became an availability-focused path with stronger redundancy and scale-out potential.

### 5.5 Step 6 Sketch Views and Record Decisions

The runtime-oriented view for this iteration can be summarized textually as follows:

1. A price-change operation starts from the Rate Editor.
2. The Rate Editor forwards the publication-related work to the Price Publishing Component.
3. The Price Publishing Component passes the change through a durable publication handoff before the result is delivered to the Channel Management System.
4. This publication path is the main reliability-sensitive path in the architecture.
5. On the query side, Query Clients send requests to the Price Query Component.
6. The Price Query Component delegates serving work to a replicated query service.
7. The replicated query service reads from the pricing data source.
8. This query path is the main availability-sensitive path in the architecture.

The key design decisions were:

- treat publication and query as separate quality-critical paths;
- improve reliability by making publication flow explicit and traceable;
- improve availability by allowing replicated query-serving elements;
- preserve consistency with the earlier architecture instead of redesigning the system from scratch.

These decisions also show a useful ADD pattern: later iterations should strengthen the architecture by refining already-known elements, not by introducing disconnected structures. The Price Publishing Component and Price Query Component were not accidental additions; they were refinements of functionally meaningful elements identified earlier. This continuity makes the final design easier to justify as one coherent architecture.

### 5.6 Step 7 Analyze Current Design

The third iteration successfully strengthened the architecture for QA-2 and QA-3. The resulting design is still abstract, but it now clearly explains where reliability and availability are realized structurally.

At this point, the architecture could answer three different classes of questions: what the main structural zones are, how primary functionality is allocated, and which paths must be hardened for runtime quality attributes. The remaining gap was operational readiness: how the architecture supports deployment across environments, monitoring, and independent testing. That gap became the direct focus of Iteration 4.

## 6. Iteration 4: Addressing Development and Operations

### 6.1 Step 2 Select Drivers

The final iteration selected:

- QA-7 Deployability
- QA-8 Monitorability
- QA-9 Testability
- CRN-3 team work allocation
- CRN-4 avoidance of technical debt
- CRN-5 continuous deployment infrastructure
- CON-3 proprietary Git-based hosting
- CON-4 schedule pressure
- CON-6 cloud-native preference

This iteration shifted attention from runtime behavior to delivery, observability, and maintainability.

This transition is justified by the assignment itself. The system is not only required to function and meet runtime qualities; it must also be workable under project and operational constraints such as environment portability, measurement needs, independent testing, team coordination, and delivery deadlines. Therefore, the final iteration completes the architecture by connecting the earlier design decisions to development and operational use.

### 6.2 Step 3 Select Elements

The selected refinement targets were:

- the Price Publishing Service, because QA-8 explicitly depends on publication metrics;
- deployment packaging and configuration elements, because QA-7 depends on environment portability;
- integration-test boundaries, because QA-9 requires independence from external systems.

This element selection is also consistent with the earlier iterations. Rather than inventing a completely separate “operations architecture,” the design refines already meaningful parts of the system: the publication path, the configuration boundary, and the interfaces to outside services. In this way, operational concerns remain integrated into the same architecture rather than being treated as external afterthoughts.

### 6.3 Step 4 Select Design Concepts

The selected concepts were:

- externalized configuration for environment portability;
- explicit telemetry emission for publication monitoring;
- replaceable adapters and test seams around external dependencies;
- modular structure that supports parallel work and avoids design erosion.

These concepts directly answer the assignment concerns CRN-3, CRN-4, and CRN-5. Clear module boundaries support parallel work allocation. Test seams and explicit adapters reduce the risk of technical debt caused by tightly coupled integrations. Externalized configuration and deployment-oriented structure provide a basis for repeatable delivery workflows. Even without naming specific toolchains, the architectural consequences of these concepts are concrete and useful.

### 6.4 Step 5 Instantiate Elements

This iteration refined the architecture with:

- a configuration service or equivalent externalized configuration boundary;
- telemetry emitters around the publication flow;
- adapter-style boundaries around external systems such as the identity service and channel management system.

These changes do not replace business logic. Instead, they make the architecture easier to deploy, observe, and test.

This distinction matters because development-and-operations work often fails when it is postponed until after the functional architecture is fixed. In this report, the architecture remains centered on business responsibilities, but the final iteration ensures that those responsibilities are supported by structures that allow measurement, controlled deployment, and test isolation. This is a stronger and more realistic architectural outcome than simply adding monitoring or deployment notes at the end.

### 6.5 Step 6 Sketch Views and Record Decisions

The development-and-operations view can be summarized textually as follows:

1. The Price Publishing Service depends on a configuration boundary so that environment changes do not require code changes.
2. The Price Publishing Service emits operational data through a Telemetry Emitter.
3. External publication is isolated through a Channel Management System Adapter.
4. Identity-related integration is isolated through a User Identity Service Adapter.
5. Authentication and permission logic depends on that adapter boundary instead of directly depending on the external service.
6. An Integration Test Harness can exercise the adapter boundaries independently, which supports testability without requiring real external systems in every test run.

The key design decisions were:

- portability should come from configuration rather than code changes;
- publication observability must be embedded rather than added later;
- external systems must be isolated behind adapters so components can be tested independently;
- clean boundaries also support team parallelism and reduce technical debt risk.

Together, these decisions connect the development concerns to the whole design history of the assignment. The first iteration defined boundaries, the second allocated behavior, the third strengthened quality-critical paths, and the fourth ensured that the resulting architecture could actually be built, verified, and operated in a disciplined way. This cumulative effect is exactly what the four-iteration ADD structure was meant to demonstrate.

### 6.6 Step 7 Analyze Current Design

The fourth iteration completed the required scope of the assignment. The final architecture now covers:

- top-level structure;
- primary functionality;
- reliability and availability;
- deployability, monitorability, and testability.

The design remained consistent across all four iterations and did not require contradiction or reset.

This final consistency is one of the strongest indicators that the architecture process was effective. Each iteration introduced new refinement pressure, but no later iteration invalidated the earlier structural choices. Instead, the design became progressively more specific while preserving a stable architectural backbone.

### 6.7 Cross-Iteration Architectural Evolution

Looking across all four iterations, the most important architectural result is not a single component diagram but the way the design matured without changing direction. Iteration 1 established the broad decomposition that separated user interaction, business logic, and external integration. At that point, the architecture answered the “shape of the system” question, but it intentionally left many behavioral details open. This was appropriate because the first iteration was about structural stability rather than operational completeness.

Iteration 2 then used that structural frame to assign explicit behavioral responsibilities. The architecture moved from broad structural zones to named elements with recognizable duties such as authentication handling, query serving, rate editing, permission management, and price publication. This is the point where the design became functionally accountable: the report could now explain not only the existence of layers, but also how each primary use case would be supported by concrete architectural elements.

Iteration 3 deepened the architecture along the most demanding runtime paths. Instead of spreading attention evenly across all parts of the system, the design concentrated on the publication and query paths because those are where the highest-impact reliability and availability pressures appear. This made the architecture more operationally credible. After this iteration, the report could explain not only functional ownership, but also why certain paths needed stronger isolation, stronger traceability, or stronger capacity for replication.

Iteration 4 completed the design by connecting architecture to delivery and long-term maintainability. Externalized configuration, telemetry boundaries, and test seams did not replace earlier design decisions; they made those decisions viable in a real project environment. In this sense, the four iterations form a logical sequence: structural decomposition, functional allocation, runtime hardening, and development-and-operations refinement. The final architecture is stronger precisely because it was not designed in one jump.

## 7. Interaction Cost Analysis

### 7.1 Selected Setup

- AI paradigm: Multi-agent
- Basic LLM: Qwen3-Max
- Agent framework: Spring AI Alibaba

### 7.2 Observable Execution Facts

The implementation records complete execution traces. From the final successful run:

- total iterations: 4;
- ADD steps executed per iteration: 6 (Step 2 to Step 7);
- agent roles per step: 5;
- total recorded model turns: 120.

These numbers are useful not only as runtime statistics but also as evidence of process completeness. The final trace confirms that every required iteration and every required ADD step from Step 2 through Step 7 was executed through the multi-agent workflow and recorded with timestamps.

### 7.3 Human Interaction Cost

The workflow required a limited number of human actions in the final assignment-generation stage. The counted actions were:

- configuring the DashScope API key;
- triggering the final full run;
- checking the generated outputs;
- organizing the final report and supporting submission package.

For the final submission-stage accounting used in this report, the number of human interactions was **4 turns**:

1. configure the API key;
2. start the formal full run;
3. inspect the generated outputs;
4. organize the final submission materials.

### 7.4 Token and Runtime Cost

The measured execution cost for the final formal run was:

- total token usage: **418K tokens**;
- end-to-end runtime: **49:08 min**;
- repeated formal rerun cost: **0**, because the final submission was based on the single successful formal run.

From a reporting perspective, these values also show the trade-off of the chosen paradigm. Multi-agent execution does not minimize total interaction volume. Instead, it spends more model turns in exchange for stronger structural discipline, clearer role separation, and more explicit review checkpoints. That trade-off is acceptable in this assignment because the purpose is to study the effectiveness of AI paradigms for architecture design rather than to minimize token usage alone.

### 7.5 Discussion

The main benefit of the multi-agent structure was not additional knowledge. Its value was process discipline:

- the coordinator preserved step alignment;
- the architect emphasized structure and responsibility allocation;
- the quality analyst checked fitness against quality attributes;
- the reviewer constrained unsupported assumptions;
- the recorder improved traceability.

The main cost was verbosity. A complete multi-agent trace is useful as evidence, but it must be distilled before submission.

Another observable limitation is that the multi-agent workflow can create representational repetition. Different agents often restate similar driver selections or conclusions because they are reasoning from the same assignment material. This repetition is useful during execution because it supports verification, but it is not suitable as a final report format. Therefore, a major part of the submission work was not only generating design output but also consolidating overlapping agent views into one concise architectural narrative.

### 7.6 Strengths and Limitations of the Multi-Agent Paradigm

The strongest advantage of the selected paradigm is role separation under a shared knowledge boundary. In this assignment, all agents had access to the same prior knowledge, but they did not perform the same function. The coordinator helped maintain process discipline, the architect emphasized structural decisions, the quality analyst repeatedly checked design fitness against quality attributes, the reviewer helped detect unsupported reasoning, and the recorder improved traceability. This created a form of internal review that was useful for architecture work, where justification is often as important as the final structure.

A second advantage is that the workflow naturally supports staged refinement. Because the system progressed through explicit roles and explicit ADD steps, it was easier to preserve a clear trail from drivers to design concepts and then to instantiated elements. That traceability would be harder to maintain in a less structured workflow where all reasoning is blended into one continuous response stream.

However, the paradigm also has clear costs. It increases token usage, increases execution time, and produces overlapping output that must later be distilled by humans. It is therefore not automatically “better” in every context. Its value is strongest when the task rewards explicit reasoning structure, traceability, and bounded verification. This assignment is a good fit for that paradigm because software architecture design is iterative, justification-heavy, and sensitive to requirement interpretation.

## 8. Individual Reflection

### 8.1 Problems Encountered

The main problems encountered in this assignment were:

- reconciling strict assignment constraints with automated model output;
- managing a long multi-agent trace without turning the final report into a log dump;
- ensuring the implementation could run in a local environment with a non-default Java setup;
- keeping the design grounded only in the supplied assignment material.

### 8.2 Solutions Adopted

The team addressed these issues by:

- constraining prompts with an explicit system policy;
- preserving complete logs and then summarizing them for the final report;
- using a verified Java and Maven runtime configuration for execution;
- separating intermediate artifacts from final deliverables.

The team also learned that final submission quality depends on artifact curation as much as on raw model output. Conversation logs, draft reports, source code, and final report documents all serve different purposes. Treating them as separate deliverables made it easier to keep the final report concise while preserving full evidence for the design process.

Another reflection is that the team’s work changed meaning over time. Early in the assignment, the challenge was mainly technical: make the system run, constrain the prompts, and preserve the logs correctly. Later, the challenge became editorial and architectural: identify which parts of the trace actually belong in the final report and how to present them in a way that matches the ADD template. This shift showed that building an AI-assisted architecture workflow is not only an implementation task but also a documentation and interpretation task.

### 8.3 Member Contributions

The team contributions in this assignment are summarized below:

- 王景宣: led the implementation, completed environment setup and troubleshooting, performed runtime verification, and organized the final submission materials.
- 李顺: handled requirement analysis, checked prompt constraints, participated in development integration, inspected generated results, and helped organize the report.
- 张岩: handled test verification, reviewed logs and report materials, organized the submission directory, and checked delivery consistency.

## 9. Conclusion

The final multi-agent system completed the four required ADD iterations for the Hotel Pricing System and produced a full evidence trail of the design process. The architecture evolved from a top-level structural decomposition to a more refined design that addressed functionality, runtime quality attributes, and development-and-operations concerns.

The most important outcome is not only the generated architecture, but also the reproducible process behind it. The submitted source code, timestamped logs, and distilled report together demonstrate that the assignment was completed with the selected multi-agent setup in a traceable and verifiable way.

More specifically, the assignment shows that the value of the multi-agent approach lies in structured reasoning rather than hidden expertise. The final architecture was produced not by adding outside knowledge, but by repeatedly applying the same bounded prior knowledge through different design roles and iteration goals. This makes the outcome especially suitable for a software architecture course, because the report demonstrates both the design result and the disciplined method used to reach it.
