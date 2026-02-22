package com.ims.eims.notice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Notice Entity.
 *
 * <p>
 * Look at that! A simple POJO annotated with {@code @Entity}.
 * JPA takes care of mapping this to our database table.
 * We use Lombok's {@code @Data} to generate getters, setters, equals, hashCode, and toString automatically.
 * It removes all the boilerplate code, leaving us with just the essence of our domain model.
 * Simply amazing!
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    // Explicit constructor if needed, but Lombok covers us!
    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
