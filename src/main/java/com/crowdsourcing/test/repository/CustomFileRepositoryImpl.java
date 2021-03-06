package com.crowdsourcing.test.repository;

import com.crowdsourcing.test.dto.SearchDto;
import com.crowdsourcing.test.domain.File;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.crowdsourcing.test.domain.QFile.file;

@Repository
@Transactional
@RequiredArgsConstructor /** final이나 @NonNull인 필드 값만 파라미터로 받는 생성자를 추가 */
public class CustomFileRepositoryImpl implements CustomFileRepository {

    private final JPAQueryFactory query;

    /**
     * 조건에 맞는 파일 전체 조회
     * querydsl 사용 -> 쿼리를 자바 코드로 작성할 수 있게 도와주는 기술
     */
    @Override
    public Page<File> findFile(SearchDto fileSearch, Pageable pageable) {
        String types = fileSearch.getTypes();
        String search = fileSearch.getSearch();
        List<File> list = query.selectFrom(file)
                .where(eqTypes(types),
                        eqSearch(search))
                .orderBy(file.id.desc())
                .fetch();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        Page<File> page = new PageImpl<File>(list.subList(start, end), pageable, list.size());
        return page;
    }

    private BooleanExpression eqTypes(String types) {
        if(!StringUtils.hasText(types)) {
            return null;
        }
        if(types.equals("none")) {
            return null;
        }
        return file.useFile.eq(types);
    }

    private BooleanExpression eqSearch(String search) {
        if(!StringUtils.hasText(search)) {
            return null;
        }
//        where originFileName like %:search%
        return file.originFileName.contains(search);
    }
}
