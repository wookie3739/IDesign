package com.my.interrior.client.shop.vignette.entity;


import com.my.interrior.client.util.AppObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="vignette")
public class VignetteEntity extends AppObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "v_no", updatable = false)
    private Long no;

    @Builder.Default
    @ColumnDefault("now()")
    @Column(name = "v_write_time", nullable = false, updatable = false)
    private LocalDateTime writeTime = LocalDateTime.now();

    @Column(nullable = false, name = "v_title")
    private String title;

    @Column(nullable = false, name = "v_price")
    private Integer price;

    @Column(nullable = false, columnDefinition = "TEXT", name = "v_content")
    private String content;
}
