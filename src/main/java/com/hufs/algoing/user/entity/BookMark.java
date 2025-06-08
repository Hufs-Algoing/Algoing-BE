package com.hufs.algoing.user.entity;

import com.hufs.algoing.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table

public class BookMark {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookMarkId;
    
    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = Problem.class)
    @JoinColumn(name = "problem_id")
    private Problem problemId;

    private boolean status;

    public BookMark(User userId, Problem problemId, boolean status) {
        this.userId = userId;
        this.problemId = problemId;
        this.status = status;
    }

    public static BookMark registerBookMark(User userId, Problem problemId) {
        return new BookMark(userId, problemId, true); // 등록 시 status = true
    }

}
