package com.hufs.algoing.aisolved.entity;

import com.hufs.algoing.hint.entity.Hint;
import com.hufs.algoing.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name="aiSolved")
@NoArgsConstructor
@AllArgsConstructor
public class AISolved {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="read_level")
    private Long readLevel;

    @Column(name="opt_level")
    private Long optLevel;

    @Column(name="dup_level")
    private Long dupLevel;

    // 가독성
    @Column(name="read_tip")
    private String readTip;

    // 최적화 기법
    @Column(name="opt_tip")
    private String optTip;

    // 중복성
    @Column(name="dup_tip")
    private String dupTip;

    // 핵심 패턴
    @Column(name="pattern")
    private String pattern;

    @OneToOne
    @JoinColumn(name="problem_id")
    private Problem problem;

    @OneToMany(mappedBy = "aiSolved", cascade = CascadeType.ALL)
    private List<Hint> hints = new ArrayList<>();

    @Override
    public String toString() {
        return "AISolved{" +
                "id=" + id +
                ", readLevel=" + readLevel +
                ", optLevel=" + optLevel +
                ", dupLevel=" + dupLevel +
                ", readTip='" + readTip + '\'' +
                ", optTip='" + optTip + '\'' +
                ", dupTip='" + dupTip + '\'' +
                ", pattern='" + pattern + '\'' +
                ", problem=" + (problem != null ? problem.getProblemId() : null) +  // 문제 ID만 출력 (문제 객체가 null일 수 있기 때문)
                '}';
    }

    public void addHint(Hint hint) {
        if (this.hints == null) {
            this.hints = new ArrayList<>();
        }
        this.hints.add(hint);
        hint.updateAISolved(this);
    }


}
