package com.hufs.algoing.problem.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity

@Table(name = "problem")
public class Problem {
    @Id
    @Column(name = "problem_id", nullable = false, unique = true)
    private Long problemId;

    private String title;

    @Column(name = "description")
    @Lob
    private String description;

    private String tag;

    private Long time;

    private Long memory;

    private Long level;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Builder
    public Problem(Long problemId, String title, String tag, Long time, Long memory, Long level,
                   String input, String output, String description) {
        this.problemId = problemId;
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.memory = memory;
        this.level = level;
        this.input = input;
        this.output = output;
        this.description = description;
    }
}
