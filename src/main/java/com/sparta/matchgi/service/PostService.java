package com.sparta.matchgi.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.matchgi.auth.auth.UserDetailsImpl;
import com.sparta.matchgi.dto.*;
import com.sparta.matchgi.model.ImgUrl;
import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.SubjectEnum;

import com.sparta.matchgi.repository.ImageRepository;
import com.sparta.matchgi.repository.ImgUrlRepository;
import com.sparta.matchgi.repository.PostRepository;
import com.sparta.matchgi.util.converter.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ImageService imageService;
    private final ImgUrlRepository imgUrlRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;




    @Value("${S3.bucket.name}")
    private String bucket;

    //포스트 생성하기(완료)
    public ResponseEntity<?> createPost(
            CreatePostRequestDto createPostRequestDto, UserDetailsImpl userDetails)
            throws IOException
    {

        Post post = new Post(createPostRequestDto, userDetails);
        postRepository.save(post);

        CreatePostResponseDto createPostResponseDto = DtoConverter.PostToCreateResponseDto(post);

        return new ResponseEntity<>(createPostResponseDto, HttpStatus.valueOf(201));
    }


    //상세 게시글 보기(완료)
    public ResponseEntity<?> getPost(Long postId,UserDetailsImpl userDetails){
        Post post=postRepository.findById(postId)
                .orElseThrow( () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(userDetails.getUser().getEmail().equals(post.getUser().getEmail())){
            post.setOwner(1);
        }
        else
            post.setOwner(-1);

        CreatePostResponseDto createPostResponseDto = DtoConverter.PostToCreateResponseDto(post);

        return new ResponseEntity<>(createPostResponseDto, HttpStatus.valueOf(201));

    }

    //이미지 db에서 지우기기
   public void imageDelete(Long imgId,UserDetailsImpl userDetails){
//        Post post=postRepository.findById(postId)
//                .orElseThrow( () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
//        System.out.println("지우기전 포스트이미지리스트: "+post.getImageList());
        //ImgUrl imgurl=imgUrlRepository.findImgUrlByPath(objectKey);

        ImgUrl imageUrl=imageRepository.findImgUrlById(imgId); //post 마다 ImgUrl(path,url)
        imageRepository.delete(imageUrl);

//        for (Iterator<ImgUrl> iterator = imageList.iterator(); iterator.hasNext();) {
//           ImgUrl img=iterator.next();
//           if (img.getImagePathDto().getPath().equals(objectKey)) {
//               iterator.remove();
//           }
//       }
//
//       System.out.println("현재 포스트이미지리스트: "+post.getImageList().get(0).getUrl());
//       System.out.println("현재 삭제하고남은이미지리스트: "+imageList);
//
//
//       CreatePostResponseDto createPostResponseDto = DtoConverter.PostToCreateResponseDto(post);
//
//       return new ResponseEntity<>(createPostResponseDto, HttpStatus.valueOf(201));
    }

    //이미지 url 받아오기(완료)
    public ImageUrlDto getImgUrl(String path){
        ImageUrlDto imageUrlDto=new ImageUrlDto(amazonS3.getUrl(bucket,path).toString());

        return imageUrlDto;
    }


    //이미지 업로드하기(완료)
    public void imageUpload(Long postId, List<MultipartFile> files,UserDetailsImpl userDetails) throws IOException{
        List<ImagePathDto> imagePathDtoList=new ArrayList<>();
        List<ImageUrlDto> imageUrlDtoList=new ArrayList<>();

        for(MultipartFile file:files){
            ImagePathDto imagePathDto=update(file); //파일을 하나씩 s3에 업데이트
            imagePathDtoList.add(imagePathDto); //파일 path를 하나씩 db에 업데이트
        }

        Post post=postRepository.findPostById(postId); //어떤 포스트에 추가할지, 포스트 불러오기

        for (ImagePathDto imagePathdto : imagePathDtoList) {
            ImageUrlDto imgurl=getImgUrl(imagePathdto.getPath());//path에 해당하는 url 받기
            ImgUrl imgUrl = new ImgUrl(post, imagePathdto.getPath(),imgurl.getUrl());
            post.addImgUrl(imgUrl); //imgurl 따로 post에 저장
        }
    }

    //이미지 버킷에 업로드하기(완료)
    public ImagePathDto update(MultipartFile file) throws IOException {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest por = new PutObjectRequest(bucket, filename, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(por);

        ImagePathDto imagePathDto = new ImagePathDto(filename);

        return imagePathDto;
    }

    //포스트 수정하기(완료)
   public ResponseEntity<?> editPost(
            Long postId,CreatePostRequestDto createPostRequestDto,UserDetailsImpl userDetails)
            throws IOException {

        Post post=postRepository.findPostById(postId);
        if(!userDetails.getUser().getEmail().equals(post.getUser().getEmail()))
            throw new IllegalArgumentException("수정 권한이 없습니다.");

        post.updatePost(createPostRequestDto,userDetails);
        postRepository.save(post);

        CreatePostResponseDto createPostResponseDto = DtoConverter.PostToCreateResponseDto(post);

        return new ResponseEntity<>(createPostResponseDto, HttpStatus.valueOf(201));
    }


    //포스트 지우기(완료)
    public ResponseEntity<?> deletePost(Long postId, UserDetailsImpl userDetails){
        Post post=postRepository.findById(postId)
                .orElseThrow( () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(!userDetails.getUser().getEmail().equals(post.getUser().getEmail())){
            throw new IllegalArgumentException("접근 권한이 없는 사용자입니다.");
        }

        postRepository.deleteById(postId);

        return new ResponseEntity<>(HttpStatus.valueOf(201));

    }


    //이미지 버킷에서 지우기(완료)-사용 X
    public void deleteImages(ImagePathDto filePaths) {
        amazonS3.deleteObject(bucket,filePaths.getPath());
    }

    public Slice<PostFilterDto> filterDtoSlice(SubjectEnum subject, Pageable pageable){
        return postRepository.findAllBySubjectOrderByCreatedAt(subject,pageable);
    }


}