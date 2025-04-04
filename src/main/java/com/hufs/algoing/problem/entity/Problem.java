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
    private Long id;

    @Column(name = "problem_num" , nullable = false, unique = true)
    private Long problemNum;

    private String title;

    private String tag;

    private Long time;

    private Long memory;

    private Long level;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "content")
    @Lob
    private String content;

    @Builder
    public Problem(Long id, Long problemNum, String title, String tag, Long time, Long memory, Long level,
                   String input, String output, String content) {
        this.id = id;
        this.problemNum = problemNum;
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.memory = memory;
        this.level = level;
        this.input = input;
        this.output = output;
        this.content = content;
    }
}
