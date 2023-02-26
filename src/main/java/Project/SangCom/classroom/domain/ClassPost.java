package Project.SangCom.classroom.domain;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
public class ClassPost extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classPost_id")
    private Long id;

    @OneToMany(mappedBy = "classPost", cascade = CascadeType.ALL)
    private List<ClassTagMap> classTagMap = new ArrayList<>();

    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    /**
     * 반 공간 관련 정봅
     * grade: 학년, classes: 반
     */
    @NotNull
    private int grade;
    @NotNull
    private int classes;

    private Long userId; // User 엔티티 대신 PK 저장

    @NotNull
    private String title;
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;


    @Builder
    public ClassPost(int grade, int classes, String title, String content) {
        this.grade = grade;
        this.classes = classes;
        this.title = title;
        this.content = content;
    }
}
