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
@ToString(callSuper = true, includeFieldNames = true)
@NoArgsConstructor
public class Hashtag extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL)
    private List<ClassTagMap> classTagMaps = new ArrayList<>();

    @NotNull
    private String tagName;



    @Builder
    public Hashtag(String tagName) {
        this.tagName = tagName;
    }
}
