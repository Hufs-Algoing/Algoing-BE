package com.hufs.algoing.hint.entity;

import com.hufs.algoing.aisolved.entity.AISolved;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="content")
    private String content;

    @Column(name="order")
    private int order;

    @ManyToOne
    @JoinColumn(name="aiSolved_id" )
    private AISolved aiSolved;

    public void updateAISolved(AISolved solved) {
        this.aiSolved = solved;
    }
}
