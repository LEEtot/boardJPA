package com.crowdsourcing.test.repository;

import com.crowdsourcing.test.dto.SearchDto;
import com.crowdsourcing.test.dto.board.BoardListDto;
import com.querydsl.core.types.Projections;
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

import static com.crowdsourcing.test.domain.QBoard.board;
import static com.crowdsourcing.test.domain.QCommonCode.commonCode;

@Repository
@Transactional
@RequiredArgsConstructor
/** final이나 @NonNull인 필드 값만 파라미터로 받는 생성자를 추가 */
public class CustomBoardRepositoryImpl implements CustomBoardRepository {

    private final JPAQueryFactory query;

    /**
     * 검색 조건에 만족하는 모든 게시글 조회
     * querydsl 사용 -> 쿼리를 자바 코드로 작성할 수 있게 도와주는 기술
     */
    @Override
    public Page<BoardListDto> findDslAll(SearchDto searchDto, Pageable pageable) {
        String search = searchDto.getSearch();
        String types = searchDto.getTypes();
        List<BoardListDto> list = query.select(Projections.fields(
                    BoardListDto.class, board.id, commonCode.codeNameKor, board.title, board.username, board.time, board.fileMasterId)
                )
                .from(board)
                .innerJoin(commonCode)
                .on(board.commonCode.eq(commonCode.code))
                .where(eqCode(types), eqSearch(search))
                .orderBy(board.id.desc())
                .fetch();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        Page<BoardListDto> page = new PageImpl<BoardListDto>(list.subList(start, end), pageable, list.size());
        return page;
    }

    private BooleanExpression eqSearch(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        return board.title.contains(search);
    }

    private BooleanExpression eqCode(String code) {
        if (!StringUtils.hasText(code) || code.equals("none")) {
            return null;
        }
        return board.commonCode.eq(code);
    }
}