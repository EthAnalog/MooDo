package bitcfull.moodo_spring.service;

import bitcfull.moodo_spring.model.MooDoUser;
import bitcfull.moodo_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class MoodoUserService {

    @Autowired
    private UserRepository userRepository;

    //    회원가입
    public MooDoUser insert(MooDoUser user) {
        return userRepository.save(user);
    }

    //    로그인
    public Optional<MooDoUser> findById(String id) {
        return userRepository.findById(id);
    }

    // 회원가입 시 아이디 중복 여부 확인
    public int userIdCheck(String id) {
        int result = userRepository.countById(id);

        return result;
    }

    // 사용자 목록 조회
    public List<MooDoUser> getAllUsers() {
        return userRepository.findAll();
    }

    // 파일 저장할 경로 설정 + 프로필 사진 업로드 및 변경(덮어쓰기)
    private final String UPLOAD_DIR = "uploads/";

    public String saveProfilePicture(String userId, MultipartFile file) throws Exception {
        MooDoUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 덮어쓰기(기존 사진 삭제)
        if (user.getProfilePicturePath() != null) {
            Path oldFilePath = Paths.get(user.getProfilePicturePath());
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }

        // 업로드 디렉터리가 없으면 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 이름 설정
        String fileName = userId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 파일 경로를 사용자 프로필에 저장
        user.setProfilePicturePath(filePath.toString());
        userRepository.save(user);

        return filePath.toString(); // 저장된 파일 경로 반환
    }

    // 프로필 사진 파일 삭제
    public String deleteProfilePicture(String userId) {
        MooDoUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String profilePicturePath = user.getProfilePicturePath();
        if (profilePicturePath != null) {
            Path filePath = Paths.get(profilePicturePath);
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
                user.setProfilePicturePath(null);
                userRepository.save(user);
                return "프로필 사진 삭제 성공";
            } catch (Exception e) {
                throw new RuntimeException("프로필 사진 삭제 실패: " + e.getMessage());
            }
        }
        return "프로필 사진이 존재하지 않습니다.";
    }

    // 사용자 정보 조회
    public MooDoUser getUserInfo(String id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.")
        );
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String id) {
        MooDoUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 프로필 사진 있을 경우 함께 삭제
        if (user.getProfilePicturePath() != null) {
            Path filePath = Paths.get(user.getProfilePicturePath());
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (Exception e) {
                throw new RuntimeException("프로필 사진 삭제 실패 : " + e.getMessage());
            }
        }

        userRepository.delete(user);
    }


}
