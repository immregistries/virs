package org.immregistries.virs.model;

public record VirsIssue(String severity, String issueType, String code, String summary) {
}