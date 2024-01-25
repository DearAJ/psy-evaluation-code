package org.ww.Adapter;

import org.springframework.stereotype.Service;
import org.ww.EntityVO.UserDetailVO;
import org.ww.EntityVO.UserVO;
import org.ww.base.BaseEntity;
import org.ww.domain.Students;
import org.ww.domain.Teachers;

import java.util.ArrayList;
import java.util.List;

@Service
public class DOToUserVO {
    public List<UserVO> studentToUserVO(List<Students> students){
        List<UserVO> userVOS = new ArrayList<>();
        for(Students s: students){
            userVOS.add(studentToUserVO(s));
        }

        return userVOS;
    }


    public UserVO studentToUserVO(Students students){
        if(students == null) return null;




        return UserVO.builder()
                .identity("student")
                .name(students.getName())
                .sex(students.getSex())
                .period(students.getPeriod())
                .grade(students.getGrade())
                .classes(students.getClasses())
                .IDCard(students.getIdNumber())
                .isGraduate(students.getGraduated())
                .studentId(students.getId())
                .schoolId(students.getSchoolId())
                .archived(students.getArchived())
                .build();
    }
    public List<UserVO> teacherToUserVO(List<Teachers> teachers){
        List<UserVO> userVOS = new ArrayList<>();
        for(Teachers t: teachers){
            userVOS.add(teacherToUserVO(t));
        }

        return userVOS;
    }

    public UserVO teacherToUserVO(Teachers teacher){
        if(teacher == null) return  null;

        return UserVO.builder()
                .identity("teacher")
                .name(teacher.getName())
                .sex(teacher.getSex())
                .phoneNumber(teacher.getPhone())
                .IDCard(teacher.getIdNumber())
                .teacherId(teacher.getId())
                .schoolId(teacher.getSchoolId())
                .build();
    }


    public UserDetailVO teacherToUserDetailVO(BaseEntity obj){
        if(obj == null) return  null;
        if(obj instanceof Teachers) {
            Teachers teacher = (Teachers) obj;
            return UserDetailVO.builder()
                    .identity("teacher")
                    .name(teacher.getName())
                    .sex(teacher.getSex())
//                .nation()
//                .origin()
                    .idType(teacher.getIdType())
                    .phoneNumber(teacher.getPhone())
                    .IDCard(teacher.getIdNumber())
                    .teacherId(teacher.getId())
                    .build();
        }
        if(obj instanceof Students){
            Students student = (Students) obj;
            return UserDetailVO.builder()
                    .identity("student")
                    .name(student.getName())
                    .sex(student.getSex())
//                .nation()
//                .origin()
                    .idType(student.getIdType())
                    .phoneNumber("")
                    .IDCard(student.getIdNumber())
                    .studentId(student.getId())
                    .build();
        }
        return null;
    }

//    public UserVO studentToUserDetailVO(Students students){
//        if(teacher == null) return  null;
//
//        return UserDetailVO.builder()
//                .identity("teacher")
//                .name(teacher.getName())
//                .sex(teacher.getSex())
////                .nation()
////                .origin()
//                .idType(teacher.getIdType())
//                .phoneNumber(teacher.getPhone())
//                .IDCard(teacher.getIdNumber())
//                .build();
//    }

}
