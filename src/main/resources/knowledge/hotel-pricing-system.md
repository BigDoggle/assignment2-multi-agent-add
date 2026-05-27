# Hotel Pricing System

## Design Purpose
This is greenfield development that completely replaces an existing system. The design activity makes initial decisions to support construction from scratch.

## Primary Functionality
- HPS-1 Log In: A commercial user or administrator provides credentials. The system validates them against a user identity service. After login, the user can only query and change hotels they are authorized for.
- HPS-2 Change Prices: An authorized user selects a hotel and dates, changes a base rate or fixed rate, simulates changes, then publishes changed prices to the Channel Management System and query APIs.
- HPS-3 Query Prices: A user or external system queries prices for a hotel through UI or query API.
- HPS-4 Manage Hotels: An administrator adds, changes, or modifies hotel information, tax rates, available rates, and room types.
- HPS-5 Manage Rates: An administrator adds, changes, or modifies rates and calculation business rules.
- HPS-6 Manage Users: An administrator changes permissions for a user.

## Quality Attributes
- QA-1 Performance: Publishing all rates and room types after a base rate change must take less than 100 ms. Use case HPS-2. Importance High. Difficulty High.
- QA-2 Reliability: 100% of multiple price changes must be published and received by the Channel Management System. Use case HPS-2. Importance High. Difficulty High.
- QA-3 Availability: Pricing query uptime SLA must be 99.9% outside maintenance windows. Use cases All. Importance High. Difficulty High.
- QA-4 Scalability: Initially support at least 100,000 price queries per day through API and handle up to 1,000,000 without average latency degradation greater than 20%. Use case HPS-3. Importance High. Difficulty High.
- QA-5 Security: Validate front-end login credentials against User Identity Service and show only authorized functions. Use cases All. Importance High. Difficulty Medium.
- QA-6 Modifiability: Add a non-REST price query endpoint such as gRPC without changing core components. Use cases All. Importance Medium. Difficulty Medium.
- QA-7 Deployability: Move the application between nonproduction environments with no code changes. Use cases All. Importance Medium. Difficulty Medium.
- QA-8 Monitorability: Provide a mechanism to collect 100% of needed performance and reliability measures for price publication. Use case HPS-2. Importance Medium. Difficulty Medium.
- QA-9 Testability: 100% of the system and elements support integration testing independently of external systems. Use cases All. Importance Medium. Difficulty Medium.

## Architectural Concerns
- CRN-1 Establish an overall initial system structure.
- CRN-2 Leverage the team's knowledge about Java technologies, Angular, and Kafka.
- CRN-3 Allocate work to members of the development team.
- CRN-4 Avoid introducing technical debt.
- CRN-5 Set up a continuous deployment infrastructure.

## Constraints
- CON-1 Users must interact through a web browser on Windows, OSX, Linux, and different devices.
- CON-2 Manage users through cloud provider identity service and host resources in the cloud.
- CON-3 Code must be hosted on the company's proprietary Git-based platform.
- CON-4 Initial release in 6 months, MVP demo in at most 2 months.
- CON-5 Initial integration with existing systems through REST APIs, later support for other protocols may be needed.
- CON-6 Favor a cloud-native approach.
