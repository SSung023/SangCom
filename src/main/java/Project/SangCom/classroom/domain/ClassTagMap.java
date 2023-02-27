package Project.SangCom.classroom.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@ToString
public class ClassTagMap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_tag_map_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classPost_id")
    private ClassPost classPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Hashtag hashtag;



    //=== 연관관계 편의 메서드===//
    public void addClassPost(ClassPost classPost){
        this.classPost = classPost;
        classPost.getClassTagMap().add(this);
    }
    public void addHashtag(Hashtag hashtag){
        this.hashtag = hashtag;
        hashtag.getClassTagMaps().add(this);
    }
}
