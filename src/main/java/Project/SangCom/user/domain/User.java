package Project.SangCom.user.domain;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.domain.Post;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Entity
@ToString(exclude = {"posts", "likes", "scraps", "comments"})
@Getter
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();



    @Column(unique = true, length = 10)
    private String nickname;

    @Column(unique = true, length = 30)
    @NotNull
    private String email;

    private String username;

    private String role; // ROLE_STUDENT,ROLE_GRADE1, 이런식으로 쉼표로 구분
    private String previewPin; // FREE, GRADE1 이런식으로 쉼표로 구분

    @Embedded
    private StudentInfo studentInfo = new StudentInfo();
    @Embedded
    private TeacherInfo teacherInfo = new TeacherInfo();

    @Builder
    public User(String username, String nickname, String email, String role, StudentInfo studentInfo, TeacherInfo teacherInfo) {
        // studentInfo, teacherInfo 추가 필요
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.studentInfo = studentInfo;
        this.teacherInfo = teacherInfo;
    }

    public User() {
    }


    //== 비지니스 코드 ==//
    public void updateUser(String givenRole, String username, String nickname, @Nullable StudentInfo studentInfo, @Nullable TeacherInfo teacherInfo){
        this.username = username;
        this.nickname = nickname;
        if (studentInfo != null){
            this.studentInfo = new StudentInfo(studentInfo.getGrade(), studentInfo.getClasses(), studentInfo.getNumber());
            this.role = "";
            this.role += givenRole; // role에 추가
            checkStudentRole();
        }

        if (teacherInfo != null || teacherInfo.getChargeGrade() != ""){
            this.teacherInfo = TeacherInfo.builder()
                    .chargeGrade(teacherInfo.getChargeGrade())
                    .chargeSubject(teacherInfo.getChargeSubject())
                    .statusMessage("")
                    .build();
            this.role = "";
            this.role += givenRole; // role 추가(재설정)
        }
    }
    // 교사의 상태메시지 갱신
    public void updateStatusMessage(String statusMessage){
        this.teacherInfo = TeacherInfo.builder()
                .chargeGrade(teacherInfo.getChargeGrade())
                .chargeSubject(teacherInfo.getChargeSubject())
                .statusMessage(statusMessage)
                .build();
    }
    // role 추가
    public void addRole(Role role){
        this.role += ",";
        this.role += role.getKey();
    }
    public void resetRole(Role role){
        this.role = role.getKey();
    }
    public void checkStudentRole(){
        this.role += ",";
        switch (studentInfo.getGrade()){
            case "1":
                this.role += Role.GRADE1.getKey();
                break;
            case "2":
                this.role += Role.GRADE2.getKey();
                break;
            case "3":
                this.role += Role.GRADE3.getKey();
                break;
        }
    }


    @Override
    public boolean equals(Object obj) {
        // StudentInfo, TeacherInfo 제외한 비교
        User target = (User) obj;
        return (Objects.equals(this.id, target.getId())) && (this.nickname.equals(target.nickname))
                && (this.email.equals(target.email)) && (this.username.equals(target.username))
                && (this.role == target.role);
    }


    //=== UserDetails 관련 메서드 ===//
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authList = new ArrayList<>();
        for (String role : role.split(",")) {
            authList.add(new SimpleGrantedAuthority(role));
        }
        return authList;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
