package cn.tp.entities.bo;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceCompareConfidenceAndClusterIdBo {
    double faceCompareConfidence;
    Long clusterId;
}
