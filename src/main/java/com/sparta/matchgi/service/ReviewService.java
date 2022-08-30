package com.sparta.matchgi.service;

import com.sparta.matchgi.auth.auth.UserDetailsImpl;
import com.sparta.matchgi.dto.RegisterReviewRequestDto;
import com.sparta.matchgi.dto.RegisterReviewResponseDto;
import com.sparta.matchgi.dto.ReviewListResponseDto;
import com.sparta.matchgi.dto.ShowReviewListResponseDto;
import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.Review;
import com.sparta.matchgi.model.ReviewImgUrl;
import com.sparta.matchgi.model.User;
import com.sparta.matchgi.repository.PostRepository;
import com.sparta.matchgi.repository.ReviewImgUrlRepository;
import com.sparta.matchgi.repository.ReviewRepository;
import com.sparta.matchgi.util.Image.S3Image;
import com.sparta.matchgi.util.converter.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final PostRepository postRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewImgUrlRepository reviewImgUrlRepository;

    private final S3Image s3Image;

    @Value("${S3.Url}")
    private String S3Url;

    public ResponseEntity<?> registerReview(Long postId, RegisterReviewRequestDto registerReviewRequestDto, UserDetailsImpl userDetails) {

        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 post입니다")
        );

        User user = userDetails.getUser();

        Optional<Review> reviewFound = reviewRepository.findByPostAndUser(post,user);

        if(reviewFound.isPresent()){
            throw new IllegalArgumentException("한 게시글에 리뷰는 한번만 달 수 있습니다.");
        }

        Review review = new Review(registerReviewRequestDto.getTitle(),registerReviewRequestDto.getContent(),post,user);

        reviewRepository.save(review);

        return new ResponseEntity<>(new RegisterReviewResponseDto(review.getId(),review.getTitle(),review.getContent(),review.getUser().getNickname()), HttpStatus.valueOf(201));

    }

    public ResponseEntity<?> uploadReviewImage(Long reviewId, List<MultipartFile> files) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 review 입니다")
        );

        try{
            for(MultipartFile file:files){
                String filePath = s3Image.upload(file);
                ReviewImgUrl reviewImgUrl = new ReviewImgUrl(S3Url+filePath,review);
                reviewImgUrlRepository.save(reviewImgUrl);
            }
        }catch (IOException e){
            reviewRepository.delete(review);
            return new ResponseEntity<>("파일업로드에 실패하였습니다.",HttpStatus.valueOf(500));
        }
        return new ResponseEntity<>("성공적으로 파일이 업로드되었습니다",HttpStatus.valueOf(201));

    }

    public ResponseEntity<?> showReviewList(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 post입니다")
        );

        List<Review> reviewList = reviewRepository.findByPost(post);

        List<ReviewListResponseDto> reviewListResponseDtoList = DtoConverter.reviewListToReviewListResponseDto(reviewList);

        return new ResponseEntity<>(new ShowReviewListResponseDto(reviewListResponseDtoList),HttpStatus.valueOf(200));

    }
}
