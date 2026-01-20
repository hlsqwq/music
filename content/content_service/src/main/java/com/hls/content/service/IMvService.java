package com.hls.content.service;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.po.Mv;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 歌手mv 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
public interface IMvService extends IService<Mv> {

    PageResult<Mv> pageBySinger(Long id,PageParam pageParam);
}
