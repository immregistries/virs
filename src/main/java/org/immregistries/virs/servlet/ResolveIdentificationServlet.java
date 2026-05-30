package org.immregistries.virs.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Resource;
import org.immregistries.virs.fhir.FhirContextProvider;
import org.immregistries.virs.fhir.FhirOperationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@WebServlet("/fhir/Immunization/$resolve-identification")
public class ResolveIdentificationServlet extends HttpServlet {

    private static final String FHIR_JSON_CONTENT_TYPE = "application/fhir+json";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().contains("json")) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Request must have a JSON FHIR content type.");
            return;
        }

        String requestBody = readBody(req);
        Parameters requestParameters;
        try {
            requestParameters = FhirOperationUtil.parseParameters(requestBody);
        } catch (RuntimeException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Malformed FHIR JSON payload: " + ex.getMessage());
            return;
        }

        if (requestParameters == null) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Request body must be a FHIR Parameters resource.");
            return;
        }

        Immunization immunization = FhirOperationUtil.requireImmunization(requestParameters);
        if (immunization == null) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Parameters.parameter[name=immunization] must contain an Immunization resource.");
            return;
        }

        if (!immunization.hasLotNumber() || immunization.getLotNumberElement().isEmpty()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Immunization.lotNumber is required.");
            return;
        }

        Parameters responseBody = FhirOperationUtil.buildResolutionResponse(immunization);
        writeResponse(resp, HttpServletResponse.SC_OK, responseBody);
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buffer = new char[4096];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, read);
            }
        }
        return builder.toString();
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        OperationOutcome outcome = FhirOperationUtil.buildOutcome(
                OperationOutcome.IssueSeverity.ERROR,
                OperationOutcome.IssueType.INVALID,
                message);
        writeResponse(resp, status, outcome);
    }

    private void writeResponse(HttpServletResponse resp, int status, Resource resource) throws IOException {
        resp.setStatus(status);
        resp.setContentType(FHIR_JSON_CONTENT_TYPE);
        resp.getWriter().write(FhirContextProvider.getContext().newJsonParser().encodeResourceToString(resource));
    }
}