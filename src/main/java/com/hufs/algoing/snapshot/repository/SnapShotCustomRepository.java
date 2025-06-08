package com.hufs.algoing.snapshot.repository;


import com.hufs.algoing.snapshot.entity.Snapshot;

import java.util.List;
import java.util.Optional;

public interface SnapShotCustomRepository {
    Optional<Snapshot> getRecentSnapshot(Long userId);
    List<Snapshot> getSnapshotHistory(Long userId);
}
