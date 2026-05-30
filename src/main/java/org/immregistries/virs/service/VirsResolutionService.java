package org.immregistries.virs.service;

import org.hl7.fhir.r4.model.Immunization;
import org.immregistries.virs.fhir.FhirOperationUtil;
import org.immregistries.virs.model.VirsCandidate;
import org.immregistries.virs.model.VirsIssue;
import org.immregistries.virs.model.VirsResolutionResult;

import java.util.List;
import java.util.Locale;

public class VirsResolutionService {

    private static final String LOT_TY00T5 = "TY00T5";
    private static final String LOT_TYY00T5 = "TYY00T5";
    private static final String LOT_TYYOOT5 = "TYYOOT5";
    private static final String LOT_UNKNOWN_123 = "UNKNOWN123";
    private static final String LOT_ABC123 = "ABC123";
    private static final String CVX_COVID = "208";
    private static final String CVX_INFLUENZA = "140";
    private static final String MVX_PFIZER = "PFR";
    private static final String MVX_SKB = "SKB";

    public VirsResolutionResult resolve(Immunization submittedImmunization) {
        String submittedLot = FhirOperationUtil.extractLotNumber(submittedImmunization);
        String normalizedLot = FhirOperationUtil.normalizeLotNumber(submittedLot);
        if (normalizedLot == null || normalizedLot.isBlank()) {
            return invalidRequest("Immunization.lotNumber is required.");
        }

        if (LOT_TY00T5.equals(normalizedLot)) {
            return resolveTy00t5(submittedImmunization);
        }

        if (isFundingSuffixLot(normalizedLot)) {
            return resolveFundingSuffix(submittedImmunization);
        }

        if (LOT_TYYOOT5.equals(normalizedLot)) {
            return resolveZeroCorrection(submittedImmunization);
        }

        if (LOT_ABC123.equals(normalizedLot)) {
            return resolveMultipleCandidates(submittedImmunization);
        }

        if (LOT_UNKNOWN_123.equals(normalizedLot)) {
            return unresolved("Lot number was not found in the demonstration rule set.");
        }

        return unresolved("Lot number was not recognized by the demonstration service.");
    }

    private VirsResolutionResult resolveTy00t5(Immunization submittedImmunization) {
        String submittedCvx = FhirOperationUtil.extractCvx(submittedImmunization);
        if (submittedCvx != null && !CVX_COVID.equals(submittedCvx)) {
            Immunization resolved = buildResolvedImmunization(submittedImmunization, LOT_TY00T5, CVX_COVID, MVX_PFIZER);
            return new VirsResolutionResult(
                    FhirOperationUtil.RESOLUTION_STATUS_CONFLICT,
                    0.75,
                    resolved,
                    List.of(),
                    List.of(new VirsIssue(
                            "error",
                            "conflict",
                            "CVX_LOT_CONFLICT",
                            "Submitted CVX conflicts with the vaccine product associated with the lot.")),
                    "Submitted CVX conflicts with the vaccine product associated with the lot.");
        }

        Immunization resolved = buildResolvedImmunization(submittedImmunization, LOT_TY00T5, CVX_COVID, MVX_PFIZER);
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_RESOLVED,
                1.0,
                resolved,
                List.of(),
                List.of(new VirsIssue(
                        "info",
                        "informational",
                        "EXACT_LOT_MATCH",
                        "Exact known lot match.")),
                "Exact known lot match.");
    }

    private VirsResolutionResult resolveFundingSuffix(Immunization submittedImmunization) {
        Immunization resolved = buildResolvedImmunization(submittedImmunization, LOT_TY00T5, CVX_COVID, MVX_PFIZER);
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_RESOLVED,
                0.96,
                resolved,
                List.of(),
                List.of(new VirsIssue(
                        "info",
                        "informational",
                        "LOT_SUFFIX_PARSED",
                        "Lot suffix was parsed as a funding source hint and removed from the lot number.")),
                "Lot suffix was parsed as a funding source hint and removed from the lot number.");
    }

    private VirsResolutionResult resolveZeroCorrection(Immunization submittedImmunization) {
        Immunization resolved = buildResolvedImmunization(submittedImmunization, LOT_TYY00T5, CVX_COVID, MVX_PFIZER);
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_PROBABLE,
                0.82,
                resolved,
                List.of(),
                List.of(new VirsIssue(
                        "warning",
                        "business-rule",
                        "O_ZERO_CORRECTION",
                        "Lot appears to contain letter O where zero was expected.")),
                "Lot appears to contain letter O where zero was expected.");
    }

    private VirsResolutionResult resolveMultipleCandidates(Immunization submittedImmunization) {
        Immunization firstCandidate = buildResolvedImmunization(submittedImmunization, LOT_ABC123, CVX_COVID,
                MVX_PFIZER);
        Immunization secondCandidate = buildResolvedImmunization(submittedImmunization, LOT_ABC123, CVX_INFLUENZA,
                MVX_SKB);
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_MULTIPLE_CANDIDATES,
                0.60,
                null,
                List.of(
                        new VirsCandidate(firstCandidate, 0.60, "lot-only", "Possible COVID-19 vaccine product."),
                        new VirsCandidate(secondCandidate, 0.55, "lot-only", "Possible influenza vaccine product.")),
                List.of(new VirsIssue(
                        "warning",
                        "processing",
                        "MULTIPLE_CANDIDATES",
                        "Multiple plausible vaccine products were found for the submitted lot.")),
                "Multiple plausible vaccine products were found for the submitted lot.");
    }

    private VirsResolutionResult unresolved(String summary) {
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_UNRESOLVED,
                0.0,
                null,
                List.of(),
                List.of(new VirsIssue(
                        "warning",
                        "not-found",
                        "LOT_NOT_FOUND",
                        summary)),
                summary);
    }

    private VirsResolutionResult invalidRequest(String summary) {
        return new VirsResolutionResult(
                FhirOperationUtil.RESOLUTION_STATUS_INVALID_REQUEST,
                0.0,
                null,
                List.of(),
                List.of(new VirsIssue(
                        "error",
                        "invalid",
                        "INVALID_REQUEST",
                        summary)),
                summary);
    }

    private Immunization buildResolvedImmunization(Immunization submittedImmunization,
            String lotNumber,
            String cvx,
            String mvx) {
        Immunization resolved = FhirOperationUtil.copyImmunization(submittedImmunization);
        if (resolved == null) {
            resolved = new Immunization();
            resolved.setStatus(Immunization.ImmunizationStatus.COMPLETED);
        }
        resolved.setLotNumber(lotNumber);
        FhirOperationUtil.setCvx(resolved, cvx);
        FhirOperationUtil.setMvx(resolved, mvx);
        return resolved;
    }

    private boolean isFundingSuffixLot(String normalizedLot) {
        if (!normalizedLot.startsWith(LOT_TY00T5 + " ")) {
            return false;
        }
        String suffix = normalizedLot.substring((LOT_TY00T5 + " ").length()).trim().toUpperCase(Locale.ROOT);
        return switch (suffix) {
            case "PRIV", "PRIVATE", "PUB", "PUBLIC" -> true;
            default -> false;
        };
    }
}