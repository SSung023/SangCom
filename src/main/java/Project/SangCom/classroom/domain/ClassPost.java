package Project.SangCom.classroom.domain;

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
    @Column(name = "classroom_id")
    private Long id;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<ClassTagMap> classTagMap = new ArrayList<>();

    /**
     * 반 공간 관련
     * grade: 학년, classes: 반
     */
    @NotNull
    private int grade;
    @NotNull
    private int classes;

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
