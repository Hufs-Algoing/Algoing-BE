package com.hufs.algoing.snapshot.Service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.SnapShotNotFoundException;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.snapshot.dto.SnapShotDTO;
import com.hufs.algoing.snapshot.entity.Snapshot;
import com.hufs.algoing.snapshot.repository.SnapShotRepository;
import com.hufs.algoing.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnapShotService {
    private final SnapShotRepository snapShotRepository;

    // 해당 유저의 가장 최근 스냅샷 반환
    public SnapShotDTO getRecentSnapShot(Long userId){

        Snapshot snapshot = snapShotRepository
                .getRecentSnapshot(userId)
                .orElseThrow(() -> new SnapShotNotFoundException(ErrorStatus.SNAPSHOT_NOT_FOUND));

        return SnapShotDTO.fromEntity(snapshot);
    }

    public List<SnapShotDTO> getSnapshotHistory(Long userId) {
        List<Snapshot> snapshots = snapShotRepository.getSnapshotHistory(userId);
        return snapshots.stream()
                .map(SnapShotDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void checkAndSaveSnapShot(User user, Review review){
        Optional<Snapshot> recent = snapShotRepository.getRecentSnapshot(user.getUserId());

        if(recent.isPresent()){
            boolean isChangedOverLimit = isChangeOverLimit(recent.get(), review);
            boolean isOverOneDay = isOverOneDay(recent.get().getCreatedAt());

            if (isChangedOverLimit || isOverOneDay) {
                Snapshot snapshot = Snapshot.builder()
                        .user(user)
                        .readbility(review.getReadbility())
                        .optimization(review.getOptimization())
                        .duplicate(review.getDuplicate())
                        .build();
                snapShotRepository.save(snapshot);
            }
        } else{ // 스냅샷이 없는 경우, 첫 스냅샷 무조건 저장
            Snapshot snapshot = Snapshot.builder()
                    .user(user)
                    .readbility(review.getReadbility())
                    .optimization(review.getOptimization())
                    .duplicate(review.getDuplicate())
                    .build();
            snapShotRepository.save(snapshot);
        }

    }

    // 5퍼센트 이상 변화 유무 확인
    private boolean isChangeOverLimit(Snapshot recent, Review review) {

        return Math.abs(recent.getReadbility() - review.getReadbility()) / 30.0 >= 0.05
                || Math.abs(recent.getOptimization() - review.getOptimization()) / 30.0 >= 0.05
                || Math.abs(recent.getDuplicate() - review.getDuplicate()) / 30.0 >= 0.05;
    }

    // 마지막 스냅샷으로부터 하루 이상 지남 유무 확인
    private boolean isOverOneDay(LocalDateTime lastSnapshotTime) {
        return Duration.between(lastSnapshotTime, LocalDateTime.now()).toHours() >= 24;
    }


}
