package com.example.Vinayaga.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectLocationResponse {
    private Long projectId;
    private String projectName;
    private String location;
    private Double latitude;
    private Double longitude;
    private String shareableMapUrl;
}
