package com.hufs.algoing.aisolved.entity;

import com.hufs.algoing.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name="problem_num")
    private Problem problem;

}
