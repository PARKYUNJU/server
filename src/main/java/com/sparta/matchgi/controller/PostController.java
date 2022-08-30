package com.sparta.matchgi.controller;


import com.sparta.matchgi.auth.auth.UserDetailsImpl;
import com.sparta.matchgi.dto.CreatePostRequestDto;
import com.sparta.matchgi.dto.CreatePostResponseDto;
import com.sparta.matchgi.dto.RevisePostRequetDto;

import com.sparta.matchgi.dto.PostFilterDto;
import com.sparta.matchgi.model.SubjectEnum;

import com.sparta.matchgi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/test")
    public String test(@RequestBody String teststring){
        return teststring;
    }
    @PostMapping("/api/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto createPostRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.createPost(createPostRequestDto,userDetails);
    }

    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<?> editPost(@PathVariable Long postId,@RequestBody CreatePostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.editPost(postId,requestDto,userDetails);
    }

    @DeleteMapping("/api/posts/{postId}")
    public void deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
    }

    @GetMapping("/api/posts/{postId}")
    public  ResponseEntity<?> getPost(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId,userDetails);

    }


    @PostMapping("/api/images/posts/{postId}")
    public void imageUpload(@PathVariable Long postId,@RequestPart List<MultipartFile> files,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        postService.imageUpload(postId,files,userDetails);
    }

    @DeleteMapping("/api/images/posts/{objectKey}")
    public void imageDelete(@PathVariable String objectKey,UserDetailsImpl userDetails){
        postService.imageDelete(objectKey,userDetails);
    }



    //필터
    @GetMapping("/api/posts")
    public Slice<PostFilterDto> postList(@RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @RequestParam(value="subject") String subject,
                                         @RequestParam(value = "sort")String sort
    )
    {
        System.out.println("정렬 컨트롤러 진입");

        return postService.filterDtoSlice(subject,sort,size,page);
    }


    //검색
    @GetMapping("/api/posts/search")
    public Slice<PostFilterDto> searchList(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam(value="search",required = false)String search){
        return postService.searchDtoSlice(search,page,size);
    }

    @PutMapping("/api/posts/matchstatus/{postId}")
    public ResponseEntity<?> changeStatus(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails){
       return  postService.changeStatus(postId,userDetails);
    }

    @GetMapping("/api/posts/gps")
    public List<PostFilterDto> findLocation(@RequestParam("lat")double lat,
                                             @RequestParam("lng")double lng) throws ParseException {
        System.out.println("정렬 컨트롤러 진입");

        return postService.findLocation(lat,lng);
    }







}