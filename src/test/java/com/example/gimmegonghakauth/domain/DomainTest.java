package com.example.gimmegonghakauth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.dao.GonghakRepository;
import com.example.gimmegonghakauth.dao.MajorsDao;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DomainTest {

    @Autowired
    private GonghakRepository gonghakRepository;
    @Autowired
    private MajorsDao majorsDao;

    private final MajorsDomain majorsDomain = MajorsDomain.builder()
        .id(1L)
        .major("건설환경공학과").build();

    @Transactional
    @BeforeEach
    void beforeEachInputTestData(){

        MajorsDomain majorsDomain = MajorsDomain.builder()
            .id(1L)
            .major("건설환경공학과").build();
        majorsDao.save(majorsDomain);
//        gonghakRepository.save(majorsDomain);
    }

    @Transactional
    @Test
    void normalSaveTest(){

        //given
        AbeekDomain normalAbeekDomain = abeekDomain(majorsDomain);

        //when
        AbeekDomain savedAbeekDomain = gonghakRepository.save(normalAbeekDomain);

        //then
        assertThat(savedAbeekDomain.getMajorsDomain().getMajor()).isEqualTo("건설환경공학과");
        assertThat(savedAbeekDomain.getYear()).isEqualTo(19);
        assertThat(savedAbeekDomain.getMinCredit()).isEqualTo(20);
        assertThat(savedAbeekDomain.getAbeekType()).isEqualTo(AbeekTypeConst.MAJOR);
    }

    @Test
    @DisplayName("MajorDomain이 불완전 할 때 runtime")
    void emptyMajorDomainSaveTest(){
        //given
        AbeekDomain errorAbeekDomain = abeekDomain(majorsDomain);
        errorAbeekDomain.setMajorsDomain(new MajorsDomain());

        //when
        //then
        assertThatThrownBy(() -> gonghakRepository.save(errorAbeekDomain))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("범위 밖의 학년")
    void outOfRangeYear(){
        AbeekDomain errorAbeekDomain = abeekDomain(majorsDomain);
        errorAbeekDomain.setYear(25);

        assertThatThrownBy(() -> gonghakRepository.save(errorAbeekDomain))
            .isInstanceOf(RuntimeException.class);
    }

    private AbeekDomain abeekDomain(MajorsDomain majorsDomain){
        return AbeekDomain.builder()
            .id(1L)
            .majorsDomain(majorsDomain)
            .year(19)
            .abeekType(AbeekTypeConst.MAJOR)
            .minCredit(20)
            .note("이것은 테스트 abeek 도메인 입니다.").build();
    }
}
