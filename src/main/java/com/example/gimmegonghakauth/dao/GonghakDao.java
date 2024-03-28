package com.example.gimmegonghakauth.dao;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.domain.AbeekDomain;
import com.example.gimmegonghakauth.domain.CompletedCoursesDomain;
import com.example.gimmegonghakauth.domain.CoursesDomain;
import com.example.gimmegonghakauth.domain.GonghakCoursesDomain;
import com.example.gimmegonghakauth.domain.MajorsDomain;
import com.example.gimmegonghakauth.dto.GonghakCompletedCoursesDto;
import com.example.gimmegonghakauth.dto.GonghakCoursesByMajorDto;
import com.example.gimmegonghakauth.dto.GonghakStandardDto;
import com.example.gimmegonghakauth.dto.IncompletedCoursesDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class GonghakDao implements GonghakRepository{

    private final AbeekDao abeekDao;
    private final CompletedCoursesDao completedCoursesDao;
    private final GonghakCorusesDao gonghakCorusesDao;
    private final MajorsDao majorsDao;

    @Override
    public AbeekDomain save(AbeekDomain abeekDomain) {
        abeekDao.save(abeekDomain);
        return abeekDomain;
    }

    @Override
    public Optional<GonghakStandardDto> findStandard(Long studentId, MajorsDomain majorsDomain) {
        int year = (int) (studentId/1000000);
//        log.info("year = {}",year);
        return changeToGonghakStandardDto(majorsDomain, year);
    }

    @Override
    public Optional<GonghakCompletedCoursesDto> findUserCompletedCourses(Long studentId) {
        List<CompletedCoursesDomain> completedCoursesDomains = completedCoursesDao.findAll();

        List<CoursesDomain> userCourses = new ArrayList<>();
        completedCoursesDomains.forEach(completedCoursesDomain -> {
            userCourses.add(completedCoursesDomain.getCoursesDomain());
        });

        return Optional.of(new GonghakCompletedCoursesDto(userCourses));
    }

    @Override
    public Optional<GonghakCoursesByMajorDto> findGonghakCoursesByMajor(MajorsDomain majorsDomain) {
        List<GonghakCoursesDomain> allByMajorsDomain = gonghakCorusesDao.findAllByMajorsDomain(majorsDomain);

        return null;
    }

    @Override
    public List<GonghakCoursesByMajorDto> findUserCoursesByMajorByGonghakCoursesWithCompletedCourses(
        Long studentId, MajorsDomain majorsDomain) {
        return gonghakCorusesDao.findUserCoursesByMajorAndGonghakCoursesWithCompletedCourses(studentId,majorsDomain);
    }

    @Override
    public List<IncompletedCoursesDto> findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
        CourseCategoryConst courseCategory, Long studentId, MajorsDomain majorsDomain) {
        return gonghakCorusesDao.findUserCoursesByMajorAndCourseCategoryAndGonghakCoursesWithoutCompleteCourses(
            courseCategory, studentId, majorsDomain);
    }

    private Optional<GonghakStandardDto> changeToGonghakStandardDto(MajorsDomain majorsDomain, int year) {

        Map<AbeekTypeConst, Integer> standards = new ConcurrentHashMap<>();
        List<AbeekDomain> allByYearAndMajorsDomain = abeekDao.findAllByYearAndMajorsDomain(year,
            majorsDomain);

        allByYearAndMajorsDomain.stream().forEach(
            abeekDomain -> {
                standards.put(abeekDomain.getAbeekType(),abeekDomain.getMinCredit());
            }
        );
        log.info("standards={}",standards);

        return Optional.of(new GonghakStandardDto(standards));
    }

}