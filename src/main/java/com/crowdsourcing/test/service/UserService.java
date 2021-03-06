package com.crowdsourcing.test.service;

import com.crowdsourcing.test.domain.BoardUser;
import com.crowdsourcing.test.dto.SearchDto;
import com.crowdsourcing.test.dto.user.UserDto;
import com.crowdsourcing.test.domain.CommonCode;
import com.crowdsourcing.test.domain.CommonCodeId;
import com.crowdsourcing.test.domain.UserId;
import com.crowdsourcing.test.dto.user.UserListDto;
import com.crowdsourcing.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor /** final이나 @NonNull인 필드 값만 파라미터로 받는 생성자를 추가 */
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CommonCodeService commonCodeService;

    /**
     * 사용자 저장, 삭제
     */
    @Transactional
    public void save(BoardUser boardUser) {
        userRepository.save(boardUser);
    }
    @Transactional
    public void remove(BoardUser boardUser) {
        userRepository.delete(boardUser);
    }

    /**
     * 사용자 한 명 검색(username)
     */
    @Override
    public BoardUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameEquals(username);
    }

    /**
     * 사용자 한 명 검색(id)
     * @return
     */
    public BoardUser findOne(UserId id) {
        return userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 조건에 맞는 사용자 전체 검색
     */
    public Page<UserListDto> findUserList(Map<String, Object> param, Pageable pageable) {
        SearchDto searchDto = new SearchDto();
        if(param.get("search") != null) {
            searchDto.setSearch(param.get("search").toString());
        }
        if(param.get("types") != null) {
            searchDto.setTypes(param.get("types").toString());
        }
        return userRepository.findUserList(searchDto, pageable);
    }

    /**
     * 회원가입
     * @param userDto
     */
    @Transactional
    public void signup(UserDto userDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        BoardUser boardUser = BoardUser.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .commonCode(userDto.getCommonCode())
                .role(commonCodeService.findById(new CommonCodeId("G002", userDto.getCommonCode())).getCodeName())
                .build();
        userRepository.save(boardUser);
    }

    /**
     * 사용자 삭제
     * @param selectedValues
     */
    @Transactional
    public void deleteUsers(List<String> selectedValues) {
        for (String user : selectedValues) {
            String[] userOne = user.split("_");
            BoardUser one = findOne(new UserId(Long.parseLong(userOne[0]), userOne[1]));
            userRepository.delete(one);
        }
    }
}
