# VIRS

Vaccine Identification and Resolution Service

VIRS is a prototype demonstrator that explores a possible future standard interface for resolving vaccine identifiers and related product metadata.

This project is intended for IIS implementers, IIS vendors, EHR vendors, HL7/FHIR implementers, public health architects, standards organizations, and collaborators who want to evaluate what a common vaccine resolution service contract could look like.

## Why VIRS Exists

Immunization data often arrives with incomplete, inconsistent, or ambiguous vaccine identification details. VIRS explores how software systems might submit identification signals and receive structured, machine-actionable resolution outcomes.

Examples of the problem space include:

- lot number normalization
- vaccine product identification
- CVX/NDC/MVX reconciliation
- candidate matching
- conflict detection
- data quality feedback
- future support for recall and inventory workflows

VIRS should be read as an interface and semantics demonstrator, not a production-grade intelligence engine.

## Design Principles

### FHIR First

The primary interface is a FHIR operation.

- Input is provided using FHIR `Immunization` content (wrapped in `Parameters`).
- Output is returned as FHIR `Parameters` plus `OperationOutcome` details.

### Expressibility Over Intelligence

The core objective is to prove the response model can express:

- exact matches
- normalized matches
- inferred corrections
- conflicts
- unresolved values
- multiple candidates
- evidence
- confidence

The current matching logic is intentionally simple so the interface semantics can be evaluated independently.

### Separation of Interface and Knowledge

The API contract is intended to remain stable while knowledge sources evolve over time.

Future implementations may combine:

- curated reference data
- IIS contributions
- manufacturer data
- AI-assisted pattern recognition
- international vaccine code systems

## Current Demonstration Capabilities

The current demonstrator uses hard-coded scenarios to validate interface behavior.

| Scenario | Result |
|---|---|
| Exact Match | Resolved |
| Funding Suffix Parsing | Resolved |
| O/Zero Correction | Probable |
| Conflicting CVX | Conflict |
| Unknown Lot | Unresolved |
| Multiple Candidates | Multiple Candidates |

## FHIR Operation

Endpoint:

```http
POST /fhir/Immunization/$resolve-identification
```

Operation contract:

```text
FHIR Parameters
	-> immunization (Immunization resource)
```

Returns:

```text
FHIR Parameters
	-> resolutionStatus
	-> resolvedImmunization
	-> confidence
	-> candidate(s)
	-> OperationOutcome
	-> summary
```

Short request example:

```json
{
	"resourceType": "Parameters",
	"parameter": [
		{
			"name": "immunization",
			"resource": {
				"resourceType": "Immunization",
				"status": "completed",
				"lotNumber": "TY00T5"
			}
		}
	]
}
```

Short response example:

```json
{
	"resourceType": "Parameters",
	"parameter": [
		{ "name": "resolutionStatus", "valueCode": "resolved" },
		{ "name": "confidence", "valueDecimal": 1.0 },
		{ "name": "summary", "valueString": "Exact known lot match." }
	]
}
```

## Demonstration Client

VIRS includes a browser-based demonstration client at:

```text
/client
```

The client allows a user to:

- enter vaccine information
- submit requests to the FHIR operation
- inspect structured results
- review raw FHIR request/response payloads

## Project Status

```text
Prototype Demonstrator
```

Current limitations are explicit:

- no authentication
- no persistence
- no database
- no production validation
- no authoritative vaccine knowledge source

## Future Directions

### Additional Input Formats

- HL7 v2 RXA
- CDC SOAP/WSDL
- JSON convenience bindings

### Additional Resolution Capabilities

- NDC validation
- manufacturer inference
- lot normalization
- expiration support
- recall support

### Knowledge Sources

- IIS contributed data
- manufacturer data
- CDC reference content
- IVC
- NUVA

### Standards Exploration

VIRS is exploring whether this style of interface could become a community standard over time.

No claim is made that such a standard already exists. The current work is intended to support collaborative design, testing, and refinement.

## Architecture Overview

```text
+-------------+
| IIS / EHR   |
+-------------+
			 |
			 v
+-------------------------+
| VIRS FHIR Operation     |
+-------------------------+
			 |
			 v
+-------------------------+
| Resolution Engine       |
+-------------------------+
			 |
			 v
+-------------------------+
| Knowledge Sources       |
+-------------------------+
```

## Build and Deployment (Short)

Build the WAR file:

```bash
mvn clean package
```

Deploy to Tomcat by copying `target/virs.war` into Tomcat's `webapps` directory, then start or restart Tomcat.

## Collaboration

Contributions are welcome from implementers and standards participants interested in interface semantics, evidence modeling, and interoperable vaccine resolution workflows.
