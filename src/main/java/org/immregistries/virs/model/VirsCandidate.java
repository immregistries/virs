package org.immregistries.virs.model;

import org.hl7.fhir.r4.model.Immunization;

public record VirsCandidate(Immunization immunization, double confidence, String matchType, String summary) {
}