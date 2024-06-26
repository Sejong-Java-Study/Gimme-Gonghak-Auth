package com.example.gimmegonghakauth;

import com.example.gimmegonghakauth.constant.CourseCategoryConst;
import com.example.gimmegonghakauth.dao.CoursesDao;
import com.example.gimmegonghakauth.dao.GonghakCorusesDao;
import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.domain.CoursesDomain;
import com.example.gimmegonghakauth.domain.GonghakCoursesDomain;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
@Profile("!(prod || release)")
public class InitFileData {
    private final MajorsDao majorsDao;
    private final CoursesDao coursesDao;
    private final GonghakCorusesDao gonghakCorusesDao;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadCoursesDataFromCSV()  throws IOException {
        log.info("----set data----");
        String csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_컴공.csv";
        inputCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_전정통.csv";
        inputCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/19학년2학기_20학년1학기_대양.csv";
        inputCoursesCsv(csvFilePath);

        csvFilePath = "src/main/java/com/example/gimmegonghakauth/computerMajorGonghakCourses2019Test.csv";
        inputGonghakCoursesCsv(csvFilePath);
        csvFilePath = "src/main/java/com/example/gimmegonghakauth/elecInfoMajorGonghakCourses2019Test.csv";
        inputGonghakCoursesCsv(csvFilePath);
    }

    private void inputCoursesCsv(String csvFilePath) throws IOException {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            String s = br.readLine();
            log.info("first br.readLine() ={}",s);

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                // Create and map GonghakCoursesDomain object
                log.info("data= {}",data.toString());
                try {
                    CoursesDomain course = mapToCoursesDomain(data);
                    log.info("data= {}",course.toString());
                    // Save to repository
                    coursesDao.save(course);
                }catch (Exception e){
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private CoursesDomain mapToCoursesDomain(String[] data){
        return CoursesDomain.builder()
            .courseId(Long.valueOf(data[0]))
            .name(data[2])
            .credit(Integer.parseInt(data[3]))
            .build();
    }

    private void inputGonghakCoursesCsv(String csvFilePath) {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                // Create and map GonghakCoursesDomain object
                try {
                    GonghakCoursesDomain course = mapToGonghakCoursesDomain(data);
                    // Save to repository
                    gonghakCorusesDao.save(course);
                }catch (Exception e){
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private GonghakCoursesDomain mapToGonghakCoursesDomain(String[] data) {
        // Assuming data format is consistent and matches the domain structure
        // Adjust indices according to your CSV file's column order
        return GonghakCoursesDomain.builder()
            .year(Integer.parseInt(data[0]))
            .majorsDomain(majorsDao.findByMajor(data[2]))
            .coursesDomain(coursesDao.findByName(data[6]))
            .courseCategory(CourseCategoryConst.valueOf(data[4]))
            .passCategory(data[5])
            .designCredit(Double.parseDouble(data[8]))
            .build();
    }
}
