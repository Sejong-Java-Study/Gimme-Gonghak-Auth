package com.example.gimmegonghakauth.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Entity
@Getter
public class UserDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Long studentId;

    @NotNull
    private String password;
    @NotNull
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private MajorsDomain majorsDomain;

    @NotNull
    private String name;

    @Builder
    public UserDomain(Long studentId, String password, String email, MajorsDomain majorsDomain,
        String name) {
        this.studentId = studentId;
        this.password = password;
        this.email = email;
        this.majorsDomain = majorsDomain;
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public UserDomain() {

    }
}