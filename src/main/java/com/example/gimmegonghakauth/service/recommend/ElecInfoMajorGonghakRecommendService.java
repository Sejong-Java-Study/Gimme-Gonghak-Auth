package com.example.gimmegonghakauth.service.recommend;

import com.example.gimmegonghakauth.constant.AbeekTypeConst;
import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.dao.GonghakRepository;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.dto.GonghakRecommendCoursesDto;
import com.example.gimmegonghakauth.dto.GonghakStandardDto;
import com.example.gimmegonghakauth.dto.IncompletedCoursesDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElecInfoMajorGonghakRecommendService implements GonghakRecommendService {

    private final GonghakRepository gonghakRepository;

    //리팩토링 필요
    @Transactional(readOnly = true)
    @Override
    public GonghakRecommendCoursesDto createRecommendCourses(UserDomain userDomain){
        GonghakRecommendCoursesDto gonghakRecommendCoursesDto = new GonghakRecommendCoursesDto();

        // findStandard -> 학번 입학년도를 기준으로 해당 년도의 abeekType(영역별 구분),minCredit(영역별 인증학점) 불러온다.
        Optional<GonghakStandardDto> standard = gonghakRepository.findStandard(
            userDomain.getStudentId(), userDomain.getMajorsDomain());


        // 수강하지 않은 과목 중 "전선" 과목을 반환한다.
        List<IncompletedCoursesDto> majorSelective = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전선, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(majorSelective);

        // 수강하지 않은 과목 중 "전필" 과목을 반환한다.
        List<IncompletedCoursesDto> majorRequired = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전필, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );

        // 수강하지 않은 과목 중 "전공" 과목을 반환한다.
        List<IncompletedCoursesDto> major = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전공, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(majorRequired);

        // 수강하지 않은 과목 중 "전문교양" 과목을 반환한다.
        List<IncompletedCoursesDto> nonMajor = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.전문교양, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(nonMajor);

        // // 수강하지 않은 과목 중 "MSC" 과목을 반환한다.
        List<IncompletedCoursesDto> msc = gonghakRepository.findUserCoursesByMajorByGonghakCoursesWithoutCompleteCourses(
            CourseCategoryConst.MSC, userDomain.getStudentId(), userDomain.getMajorsDomain()
        );
        printLog(msc);

        // abeekType 별 추천 과목 List를 반환한다.
        Map<AbeekTypeConst, List<IncompletedCoursesDto>> coursesByAbeekTypeWithoutCompleteCourses = gonghakRecommendCoursesDto.getRecommendCoursesByAbeekType();
        Arrays.stream(AbeekTypeConst.values()).forEach(
            abeekType -> {
                List<IncompletedCoursesDto> abeekRecommend = new ArrayList<>();
                if(standard.get().getStandards().containsKey(abeekType)){
                    switch (abeekType){
                        case MSC :
                            abeekRecommend.addAll(msc); break;
                        case MAJOR :
                            abeekRecommend.addAll(major);
                            abeekRecommend.addAll(majorRequired);
                            abeekRecommend.addAll(majorSelective); break;
                        case DESIGN:
                            addOnlyDesignCreditOverZero(major, abeekRecommend);
                            addOnlyDesignCreditOverZero(majorRequired, abeekRecommend);
                            addOnlyDesignCreditOverZero(majorSelective, abeekRecommend);
                            break;
                        case PROFESSIONAL_NON_MAJOR:
                            abeekRecommend.addAll(nonMajor); break;
                        case MINIMUM_CERTI:
                            abeekRecommend.addAll(msc);
                            abeekRecommend.addAll(majorRequired);
                            abeekRecommend.addAll(majorSelective);
                            abeekRecommend.addAll(nonMajor); break;
                    }
                    coursesByAbeekTypeWithoutCompleteCourses.put(abeekType,abeekRecommend);
                }

            }
        );

        return gonghakRecommendCoursesDto;
    }

    // 설계 과목(designCredit > 0)인 경우만 추가한다.
    private static void addOnlyDesignCreditOverZero(List<IncompletedCoursesDto> majorBasic,
        List<IncompletedCoursesDto> abeekRecommend) {
        majorBasic.forEach(
            incompletedCoursesDto -> {
                if(incompletedCoursesDto.getDesignCredit()>0){
                    abeekRecommend.add(incompletedCoursesDto);
                }
            }
        );
    }

    private void printLog(List<IncompletedCoursesDto> incompletedCoursesDtoList){
        log.info("-----------");
        log.info("dto list size = {}",incompletedCoursesDtoList.size());
        for (IncompletedCoursesDto incompletedCoursesDto : incompletedCoursesDtoList) {
            log.info("courseName = {}",incompletedCoursesDto.getCourseName());
        }
    }
}
