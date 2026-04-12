package com.hls.media.controller;


import com.hls.media.service.IUserMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-03-30
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-media")
public class UserMediaController {

    private final IUserMediaService  userMediaService;



}
