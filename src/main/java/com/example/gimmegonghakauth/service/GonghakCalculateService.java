package com.example.gimmegonghakauth.service;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.dao.GonghakRepository;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.dto.GonghakCoursesByMajorDto;
import com.example.gimmegonghakauth.dto.GonghakResultDto;
import com.example.gimmegonghakauth.dto.GonghakResultDto.ResultPointDto;
import com.example.gimmegonghakauth.dto.GonghakStandardDto;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GonghakCalculateService {

    private final GonghakRepository gonghakRepository;

    @Transactional(readOnly = true)
    public Optional<GonghakResultDto> getResultRatio(UserDomain userDomain) {
        //standard
        Optional<GonghakStandardDto> standard = gonghakRepository.findStandard(userDomain.getStudentId(), userDomain.getMajorsDomain());
        log.info("standard = {}",standard.get().getStandards());

        //default user abeek 학점 상태 map
        Map<AbeekTypeConst, Double> userAbeekCredit = getUserAbeekCreditDefault(standard.get().getStandards());

        log.info("default user abeek 학점 상태 map userAbeekCredit = {}",userAbeekCredit);

        //user 공학 상태 테이블
        List<GonghakCoursesByMajorDto> userCoursesByMajorByGonghakCoursesWithCompletedCourses = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithCompletedCourses(
            userDomain.getStudentId(), userDomain.getMajorsDomain());

        userCoursesByMajorByGonghakCoursesWithCompletedCourses.forEach(
            gonghakCoursesByMajorDto -> {
                log.info("user 공학 상태 테이블 course name ={}",gonghakCoursesByMajorDto.getCourseName());
                log.info("user 공학 상태 테이블 getCredit ={}",gonghakCoursesByMajorDto.getCredit());
                log.info("user 공학 상태 테이블 getDesignCredit ={}",gonghakCoursesByMajorDto.getDesignCredit());
            }
        );

        //user
        stackUserGonghakCredit(userCoursesByMajorByGonghakCoursesWithCompletedCourses, userAbeekCredit);

        log.info("학점 계산 후 학점 상태 map userAbeekCredit = {}",userAbeekCredit);

        Map<AbeekTypeConst, ResultPointDto> userResultRatio = getUserGonghakResultRatio(userAbeekCredit, standard);

        log.info("비율 결과 userResultRatio = {}",userResultRatio);
        userResultRatio.forEach(
            (abeekTypeConst, resultPointDto) -> {
                log.info("resultPointDto.getStandardPoint() = {}",resultPointDto.getStandardPoint());
                log.info("resultPointDto.getStandardPoint() = {}",resultPointDto.getStandardPoint());
            }
        );

        return Optional.of(new GonghakResultDto(userResultRatio));
    }

    private Map<AbeekTypeConst, Double> getUserAbeekCreditDefault(Map<AbeekTypeConst, Integer> standards) {
        Map<AbeekTypeConst, Double> userAbeekCredit = new ConcurrentHashMap<>();
        Arrays.stream(AbeekTypeConst.values()).forEach(abeekTypeConst -> {
            if(standards.containsKey(abeekTypeConst)){
                userAbeekCredit.put(abeekTypeConst,0.0);
            }
        });
        return userAbeekCredit;
    }

    private Map<AbeekTypeConst, ResultPointDto> getUserGonghakResultRatio(Map<AbeekTypeConst, Double> userAbeekCredit,
        Optional<GonghakStandardDto> standard) {

        Map<AbeekTypeConst, ResultPointDto> userResultRatio = new ConcurrentHashMap<>();
        Arrays.stream(AbeekTypeConst.values()).forEach(abeekTypeConst -> {
                if(userAbeekCredit.containsKey(abeekTypeConst)){
                    getRatio(userAbeekCredit, standard, abeekTypeConst, userResultRatio);
                }
            }
        );
        return userResultRatio;
    }

    private void getRatio(Map<AbeekTypeConst, Double> userAbeekCredit,
        Optional<GonghakStandardDto> standard, AbeekTypeConst abeekTypeConst,
        Map<AbeekTypeConst, ResultPointDto> userResultRatio) {

        userResultRatio.put(
            abeekTypeConst, new ResultPointDto(userAbeekCredit.get(abeekTypeConst),standard.get().getStandards()
                        .get(abeekTypeConst))
        );
    }

    private void stackUserGonghakCredit(
        List<GonghakCoursesByMajorDto> userCoursesByMajorByGonghakCoursesWithCompletedCourses,
        Map<AbeekTypeConst, Double> userAbeekCredit) {
        userCoursesByMajorByGonghakCoursesWithCompletedCourses.forEach(gonghakCoursesByMajorDto -> {
            log.info("교과목= {}",gonghakCoursesByMajorDto.getCourseName());
            switch (gonghakCoursesByMajorDto.getCourseCategory()){
                case 전선, 전공주제, 전필, 전공:
                    stackCredit(AbeekTypeConst.MAJOR, gonghakCoursesByMajorDto, userAbeekCredit);break;
                case 전문교양:
                    stackCredit(AbeekTypeConst.PROFESSIONAL_NON_MAJOR,gonghakCoursesByMajorDto, userAbeekCredit); break;
                case MSC:
                    stackCredit(AbeekTypeConst.MSC,gonghakCoursesByMajorDto, userAbeekCredit); break;
                case BSM:
                    stackCredit(AbeekTypeConst.BSM,gonghakCoursesByMajorDto,userAbeekCredit);break;
            }
            stackCredit(AbeekTypeConst.DESIGN, gonghakCoursesByMajorDto, userAbeekCredit);
            stackCredit(AbeekTypeConst.MINIMUM_CERTI,gonghakCoursesByMajorDto, userAbeekCredit);
        });
    }

    private void stackCredit(AbeekTypeConst abeekTypeConst, GonghakCoursesByMajorDto gonghakCoursesByMajorDto,
        Map<AbeekTypeConst, Double> userAbeekCredit) {
        double inputCredit = getInputCredit(abeekTypeConst, gonghakCoursesByMajorDto);
        log.info("abeekTypeConst= {}",abeekTypeConst);
        log.info("userAbeekCredit.get(abeekTypeConst)= {}",userAbeekCredit.get(abeekTypeConst));
        userAbeekCredit.put(abeekTypeConst, userAbeekCredit.get(abeekTypeConst) + inputCredit);

    }

    private double getInputCredit(AbeekTypeConst abeekTypeConst,
        GonghakCoursesByMajorDto gonghakCoursesByMajorDto) {
        if(abeekTypeConst == AbeekTypeConst.DESIGN) return gonghakCoursesByMajorDto.getDesignCredit();
        else return (double) gonghakCoursesByMajorDto.getCredit();
    }


}
