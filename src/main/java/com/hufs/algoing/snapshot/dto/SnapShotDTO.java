package com.hufs.algoing.snapshot.dto;

import com.hufs.algoing.snapshot.entity.Snapshot;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
@Schema(description = "유저의 코드 리뷰 스냅샷 데이터, 그래프는 정규화된 점수를 기반으로 생성됩니다.")
public class SnapShotDTO {

    @Schema(description = "가독성 점수 (원점수)", example = "21")
    private Long readbility;

    @Schema(description = "최적화 점수 (원점수)", example = "24")
    private Long optimization;

    @Schema(description = "중복성 점수 (원점수)", example = "18")
    private Long duplicate;

    @Schema(description = "가독성 점수 (정규화된 백분율)", example = "70.0")
    private double normalizedReadbility;

    @Schema(description = "최적화 점수 (정규화된 백분율)", example = "80.0")
    private double normalizedOptimization;

    @Schema(description = "중복성 점수 (정규화된 백분율)", example = "60.0")
    private double normalizedDuplicate;

    @Schema(description = "스냅샷 생성 일시", example = "2025-06-08T12:34:56")
    private LocalDateTime createdAt;

    public static SnapShotDTO fromEntity(Snapshot entity) {
        Long readbility = entity.getReadbility();
        Long optimization = entity.getOptimization();
        Long duplicate = entity.getDuplicate();

        String normalizedReadbility = String.format("%.2f", (readbility / 30.0) * 100);
        String normalizedOptimization = String.format("%.2f", (optimization / 30.0) * 100);
        String normalizedDuplicate = String.format("%.2f", (duplicate / 30.0) * 100);

        return SnapShotDTO.builder()
                .readbility(readbility)
                .optimization(optimization)
                .duplicate(duplicate)
                .normalizedReadbility(Double.parseDouble(normalizedReadbility))
                .normalizedOptimization(Double.parseDouble(normalizedOptimization))
                .normalizedDuplicate(Double.parseDouble(normalizedDuplicate))
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
