package org.immregistries.virs.fhir;

import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Resource;
import org.immregistries.virs.model.VirsCandidate;
import org.immregistries.virs.model.VirsIssue;
import org.immregistries.virs.model.VirsResolutionResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public final class FhirOperationUtil {

    public static final String IMMUNIZATION_PARAMETER_NAME = "immunization";
    public static final String RESOLUTION_STATUS_PARAMETER_NAME = "resolutionStatus";
    public static final String RESOLVED_IMMUNIZATION_PARAMETER_NAME = "resolvedImmunization";
    public static final String CONFIDENCE_PARAMETER_NAME = "confidence";
    public static final String CANDIDATE_PARAMETER_NAME = "candidate";
    public static final String MATCH_TYPE_PART_NAME = "matchType";
    public static final String SUMMARY_PARAMETER_NAME = "summary";
    public static final String OUTCOME_PARAMETER_NAME = "outcome";
    public static final String RESOLUTION_STATUS_RESOLVED = "resolved";
    public static final String RESOLUTION_STATUS_PROBABLE = "probable";
    public static final String RESOLUTION_STATUS_MULTIPLE_CANDIDATES = "multiple-candidates";
    public static final String RESOLUTION_STATUS_UNRESOLVED = "unresolved";
    public static final String RESOLUTION_STATUS_CONFLICT = "conflict";
    public static final String RESOLUTION_STATUS_INVALID_REQUEST = "invalid-request";
    public static final String CVX_SYSTEM = "http://hl7.org/fhir/sid/cvx";
    public static final String MVX_SYSTEM = "http://hl7.org/fhir/sid/mvx";
    public static final String ISSUE_CODE_SYSTEM = "https://immregistries.org/fhir/CodeSystem/virs-issue-code";

    private FhirOperationUtil() {
    }

    public static Parameters parseParameters(String body) {
        IParser parser = FhirContextProvider.getContext().newJsonParser();
        return parser.parseResource(Parameters.class, body);
    }

    public static Immunization requireImmunization(Parameters parameters) {
        Resource resource = findResourceParameter(parameters, IMMUNIZATION_PARAMETER_NAME);
        if (resource == null) {
            return null;
        }
        if (!(resource instanceof Immunization immunization)) {
            return null;
        }
        return immunization;
    }

    public static Resource findResourceParameter(Parameters parameters, String parameterName) {
        for (Parameters.ParametersParameterComponent component : parameters.getParameter()) {
            if (parameterName.equals(component.getName()) && component.hasResource()) {
                return component.getResource();
            }
        }
        return null;
    }

    public static String extractLotNumber(Immunization immunization) {
        if (immunization == null || !immunization.hasLotNumber() || immunization.getLotNumberElement().isEmpty()) {
            return null;
        }
        return immunization.getLotNumber();
    }

    public static String normalizeLotNumber(String lotNumber) {
        if (lotNumber == null) {
            return null;
        }
        return lotNumber.trim().toUpperCase(Locale.ROOT);
    }

    public static String extractCvx(Immunization immunization) {
        if (immunization == null || !immunization.hasVaccineCode()) {
            return null;
        }
        for (Coding coding : immunization.getVaccineCode().getCoding()) {
            if (CVX_SYSTEM.equals(coding.getSystem()) && coding.hasCode()) {
                return coding.getCode();
            }
        }
        return null;
    }

    public static String extractMvx(Immunization immunization) {
        if (immunization == null || !immunization.hasManufacturer()
                || !immunization.getManufacturer().hasIdentifier()) {
            return null;
        }
        Identifier identifier = immunization.getManufacturer().getIdentifier();
        if (MVX_SYSTEM.equals(identifier.getSystem()) && identifier.hasValue()) {
            return identifier.getValue();
        }
        return null;
    }

    public static Immunization copyImmunization(Immunization immunization) {
        if (immunization == null) {
            return null;
        }
        Immunization copy = (Immunization) immunization.copy();
        if (!copy.hasStatus() || copy.getStatusElement().isEmpty()) {
            copy.setStatus(Immunization.ImmunizationStatus.COMPLETED);
        }
        return copy;
    }

    public static void setCvx(Immunization immunization, String cvxCode) {
        if (immunization == null) {
            return;
        }
        if (!immunization.hasVaccineCode()) {
            immunization.setVaccineCode(new CodeableConcept());
        }
        CodeableConcept vaccineCode = immunization.getVaccineCode();
        vaccineCode.getCoding().removeIf(coding -> CVX_SYSTEM.equals(coding.getSystem()));
        vaccineCode.addCoding()
                .setSystem(CVX_SYSTEM)
                .setCode(cvxCode);
    }

    public static void setMvx(Immunization immunization, String mvxCode) {
        if (immunization == null) {
            return;
        }
        if (!immunization.hasManufacturer()) {
            immunization.setManufacturer(new Reference());
        }
        Reference manufacturer = immunization.getManufacturer();
        manufacturer.setIdentifier(new Identifier()
                .setSystem(MVX_SYSTEM)
                .setValue(mvxCode));
    }

    public static OperationOutcome buildOutcome(OperationOutcome.IssueSeverity severity,
            OperationOutcome.IssueType issueType,
            String message) {
        OperationOutcome outcome = new OperationOutcome();
        outcome.addIssue()
                .setSeverity(severity)
                .setCode(issueType)
                .setDiagnostics(message);
        return outcome;
    }

    public static OperationOutcome buildOutcome(List<VirsIssue> issues) {
        OperationOutcome outcome = new OperationOutcome();
        for (VirsIssue issue : issues) {
            OperationOutcome.OperationOutcomeIssueComponent operationIssue = outcome.addIssue();
            operationIssue.setSeverity(mapSeverity(issue.severity()));
            operationIssue.setCode(mapIssueType(issue.issueType()));
            operationIssue.setDiagnostics(issue.summary());
            operationIssue.setDetails(new CodeableConcept()
                    .addCoding(new Coding()
                            .setSystem(ISSUE_CODE_SYSTEM)
                            .setCode(issue.code())
                            .setDisplay(issue.summary()))
                    .setText(issue.summary()));
        }
        return outcome;
    }

    public static Parameters buildResolutionResponse(VirsResolutionResult result) {
        Parameters response = new Parameters();
        response.addParameter()
                .setName(RESOLUTION_STATUS_PARAMETER_NAME)
                .setValue(new CodeType(result.resolutionStatus()));
        if (result.resolvedImmunization() != null) {
            response.addParameter()
                    .setName(RESOLVED_IMMUNIZATION_PARAMETER_NAME)
                    .setResource(result.resolvedImmunization());
        }
        response.addParameter()
                .setName(CONFIDENCE_PARAMETER_NAME)
                .setValue(new DecimalType(BigDecimal.valueOf(result.confidence())));
        for (VirsCandidate candidate : result.candidates()) {
            Parameters.ParametersParameterComponent candidateParameter = response.addParameter();
            candidateParameter.setName(CANDIDATE_PARAMETER_NAME);
            candidateParameter.addPart()
                    .setName(IMMUNIZATION_PARAMETER_NAME)
                    .setResource(candidate.immunization());
            candidateParameter.addPart()
                    .setName(CONFIDENCE_PARAMETER_NAME)
                    .setValue(new DecimalType(BigDecimal.valueOf(candidate.confidence())));
            candidateParameter.addPart()
                    .setName(MATCH_TYPE_PART_NAME)
                    .setValue(new CodeType(candidate.matchType()));
            candidateParameter.addPart()
                    .setName(SUMMARY_PARAMETER_NAME)
                    .setValue(new StringType(candidate.summary()));
        }
        response.addParameter()
                .setName(OUTCOME_PARAMETER_NAME)
                .setResource(buildOutcome(result.issues()));
        response.addParameter()
                .setName(SUMMARY_PARAMETER_NAME)
                .setValue(new StringType(result.summary()));
        return response;
    }

    private static OperationOutcome.IssueSeverity mapSeverity(String severity) {
        if (severity == null) {
            return OperationOutcome.IssueSeverity.INFORMATION;
        }
        return switch (severity) {
            case "warning" -> OperationOutcome.IssueSeverity.WARNING;
            case "error" -> OperationOutcome.IssueSeverity.ERROR;
            default -> OperationOutcome.IssueSeverity.INFORMATION;
        };
    }

    private static OperationOutcome.IssueType mapIssueType(String issueType) {
        if (issueType == null) {
            return OperationOutcome.IssueType.INFORMATIONAL;
        }
        return switch (issueType) {
            case "processing" -> OperationOutcome.IssueType.PROCESSING;
            case "business-rule" -> OperationOutcome.IssueType.BUSINESSRULE;
            case "not-found" -> OperationOutcome.IssueType.NOTFOUND;
            case "invalid" -> OperationOutcome.IssueType.INVALID;
            case "conflict" -> OperationOutcome.IssueType.CONFLICT;
            default -> OperationOutcome.IssueType.INFORMATIONAL;
        };
    }
}