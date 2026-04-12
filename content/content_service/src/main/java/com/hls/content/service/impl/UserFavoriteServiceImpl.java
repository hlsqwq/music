package com.hls.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hls.content.mapper.UserFavoriteMapper;
import com.hls.content.po.UserFavorite;
import com.hls.content.service.IUserFavoriteService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收藏 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-04-11
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {

}
