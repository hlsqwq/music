package com.hls.auth.service.impl;

import media.po.UserFavorite;
import media.mapper.UserFavoriteMapper;
import media.service.IUserFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
