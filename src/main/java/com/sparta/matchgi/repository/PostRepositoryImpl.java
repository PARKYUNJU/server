package com.sparta.matchgi.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.matchgi.dto.PostFilterDto;
import com.sparta.matchgi.model.MatchStatus;
import com.sparta.matchgi.model.QImgUrl;
import com.sparta.matchgi.model.QPost;
import com.sparta.matchgi.model.SubjectEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.querydsl.core.types.dsl.MathExpressions.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{


    private final JPAQueryFactory queryFactory;
    private QPost post=QPost.post;

    private QImgUrl imgUrl = QImgUrl.imgUrl;



    //종목+조회수 필터
    @Override
    public Slice<PostFilterDto> findAllBySubjectOrderByCreatedAt(String subject, String sort, Pageable pageable) {
        List<com.sparta.matchgi.dto.PostFilterDto> returnPost = queryFactory.select(Projections.fields(
                        com.sparta.matchgi.dto.PostFilterDto.class,
                        post.id.as("postId"),//as를 꼭 해줘야 id가 들어감
                        post.createdAt,
                        post.title,
                        subjectCaseBuilder().as("subject"),
                        post.viewCount,
                        post.matchDeadline,
                        post.requestCount,
                        post.matchStatus,
                        post.address,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(imgUrl.url)
                                        .from(imgUrl)
                                        .where(imgUrl.id.eq(
                                                JPAExpressions
                                                        .select(imgUrl.id.min())
                                                        .from(imgUrl)
                                                        .where(imgUrl.post.eq(post))
                                        )),"imgUrl"
                        )))
                .from(post)
                .where(getSubject(subject))
                .orderBy(orderByOngoing(), OrderBySort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = false;
        if (returnPost.size() > pageable.getPageSize()) {
            returnPost.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(returnPost, pageable, hasNext);
    }


    //검색 기능
    @Override
    public Slice<PostFilterDto> findAllBySearchOrderByCreatedAt(String search,Pageable pageable){
        List<PostFilterDto> returnPost=queryFactory.select(Projections.fields(
                        PostFilterDto.class,
                        post.id.as("postId"),//as를 꼭 해줘야 id가 들어감
                        post.createdAt,
                        post.title,
                        subjectCaseBuilder().as("subject"),
                        post.viewCount,
                        post.matchDeadline,
                        post.requestCount,
                        post.matchStatus,
                        post.address,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(imgUrl.url)
                                        .from(imgUrl)
                                        .where(imgUrl.id.eq(
                                                JPAExpressions
                                                        .select(imgUrl.id.min())
                                                        .from(imgUrl)
                                                        .where(imgUrl.post.eq(post))
                                        )),"imgUrl"
                        )))
                .from(post)
                .where(post.title.contains(search).or(post.content.contains(search)))
                .orderBy(orderByOngoing(),post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        return new SliceImpl<>(returnPost,pageable,returnPost.iterator().hasNext());
    }


    @Override
    public List<PostFilterDto> findAllByLocation(double lat, double lng){

        NumberPath<Double> distancePath = Expressions.numberPath(Double.class, "distance");

        List<PostFilterDto> returnPost= queryFactory.select(Projections.fields(
                PostFilterDto.class,
                post.id.as("postId"),//as를 꼭 해줘야 id가 들어감
                post.createdAt,
                post.title,
                subjectCaseBuilder().as("subject"),
                post.viewCount,
                post.matchDeadline,
                post.requestCount,
                post.matchStatus,
                post.lat,
                post.lng,
                post.address,
                ExpressionUtils.as(
                        JPAExpressions
                                .select(imgUrl.url)
                                .from(imgUrl)
                                .where(imgUrl.id.eq(
                                        JPAExpressions
                                                .select(imgUrl.id.min())
                                                .from(imgUrl)
                                                .where(imgUrl.post.eq(post))
                                )),"imgUrl"
                )
                        ))
                .from(post)
                .where(acos(cos(radians(Expressions.constant(lat)))
                        .multiply(cos(radians(post.lat)))
                        .multiply(cos(radians(post.lng).subtract(radians(Expressions.constant(lng)))))
                        .add((sin(radians(Expressions.constant(lat))).multiply(sin(radians(post.lat)))))
                ).multiply(Expressions.constant(6371)).loe(5),post.matchStatus.eq(MatchStatus.ONGOING))//getLocation(lat,lng)로하면 안뜸
                .orderBy(post.id.asc())
                .fetch();
        return returnPost;

    }
    //querydsl이나 nativeQuery에서 st_distance_sphere을 쓰려면 db를 mysql로 연결해야함


    private BooleanExpression getSubject(String subject){
        return subject.equals("ALL") ? null : post.subject.eq(SubjectEnum.valueOf(subject));
    }



    private OrderSpecifier<Integer> orderByOngoing() {
        return new CaseBuilder().when(post.matchStatus.eq(MatchStatus.ONGOING)).then(1).otherwise(2).asc();
    }

    private BooleanExpression orderOnlyOngoing(){
        return orderOnlyOngoing().equals(1)?post.matchStatus.eq(MatchStatus.ONGOING):null;
    }

    private StringExpression subjectCaseBuilder(){
        return new CaseBuilder().when(post.subject.eq(SubjectEnum.SOCCER)).then("축구")
                .when(post.subject.eq(SubjectEnum.BADMINTON)).then("배드민턴")
                .when(post.subject.eq(SubjectEnum.BASKETBALL)).then("농구")
                .when(post.subject.eq(SubjectEnum.BILLIARDS)).then("당구")
                .when(post.subject.eq(SubjectEnum.BOWLING)).then("볼링")
                .when(post.subject.eq(SubjectEnum.TENNIS)).then("테니스")
                .otherwise("기타");

    }


    private OrderSpecifier<?> OrderBySort(String sort) {
        if ("viewcount".equals(sort)) {
            return post.viewCount.desc();
        }
        return post.createdAt.desc();
    }

    //경기목록(필터)api에서 몇월/며칠(요일)로 주기
    //지도 경기줄 때, 경기별로 경기 아이콘 이미지url도 추가해서 주기기
    //경기목록에 내용 굳이 안줘도 될듯? 제목도 길면 한 카드가 너무 복잡함



}