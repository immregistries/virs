package org.immregistries.virs.fhir;

import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;

import java.math.BigDecimal;

public final class FhirOperationUtil {

    public static final String IMMUNIZATION_PARAMETER_NAME = "immunization";
    public static final String RESOLUTION_STATUS_PARAMETER_NAME = "resolutionStatus";
    public static final String RESOLVED_IMMUNIZATION_PARAMETER_NAME = "resolvedImmunization";
    public static final String CONFIDENCE_PARAMETER_NAME = "confidence";
    public static final String OUTCOME_PARAMETER_NAME = "outcome";
    public static final String RESOLUTION_STATUS_RESOLVED = "resolved";
    public static final String STATIC_DEMONSTRATION_MESSAGE = "Static demonstration response. Resolution logic will be added later.";

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

    public static Parameters buildResolutionResponse(Immunization immunization) {
        Parameters response = new Parameters();
        response.addParameter()
                .setName(RESOLUTION_STATUS_PARAMETER_NAME)
                .setValue(new CodeType(RESOLUTION_STATUS_RESOLVED));
        response.addParameter()
                .setName(RESOLVED_IMMUNIZATION_PARAMETER_NAME)
                .setResource(immunization);
        response.addParameter()
                .setName(CONFIDENCE_PARAMETER_NAME)
                .setValue(new DecimalType(BigDecimal.ONE));
        response.addParameter()
                .setName(OUTCOME_PARAMETER_NAME)
                .setResource(buildOutcome(OperationOutcome.IssueSeverity.INFORMATION,
                        OperationOutcome.IssueType.INFORMATIONAL,
                        STATIC_DEMONSTRATION_MESSAGE));
        return response;
    }
}