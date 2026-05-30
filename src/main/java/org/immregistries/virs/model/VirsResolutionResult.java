package org.immregistries.virs.model;

import org.hl7.fhir.r4.model.Immunization;

import java.util.List;

public record VirsResolutionResult(String resolutionStatus,
        double confidence,
        Immunization resolvedImmunization,
        List<VirsCandidate> candidates,
        List<VirsIssue> issues,
        String summary) {

    public VirsResolutionResult {
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
        issues = issues == null ? List.of() : List.copyOf(issues);
    }
}