package com.hufs.algoing.snapshot.repository;

import com.hufs.algoing.snapshot.entity.Snapshot;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hufs.algoing.snapshot.entity.QSnapshot.snapshot;

@Repository
@RequiredArgsConstructor
public class SnapShotCustomRepositoryImpl implements SnapShotCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Snapshot> getRecentSnapshot(Long userId) {
        Snapshot result = jpaQueryFactory
                .selectFrom(snapshot)
                .where(snapshot.user.userId.eq(userId))
                .orderBy(snapshot.createdAt.desc())
                .fetchFirst();
        return Optional.ofNullable(result);
    }

    @Override
    public List<Snapshot> getSnapshotHistory(Long userId) {
        return jpaQueryFactory
                .selectFrom(snapshot)
                .where(snapshot.user.userId.eq(userId))
                .orderBy(snapshot.createdAt.asc())
                .fetch();
    }

}
