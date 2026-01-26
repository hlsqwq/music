package com.hls.content.service;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.po.Mv;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * <p>
 * 歌手mv 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
public interface IMvService extends IService<Mv> {

    PageResult<Mv> pageBySinger(Long id, PageParam pageParam);

    /**
     * 新增MV
     *
     * @param mv     MV信息
     */
    void addMv(Mv mv);

    /**
     * 删除MV
     *
     * @param mvId   MV ID
     */
    void deleteMv(Integer mvId);

    /**
     * 修改MV
     *
     * @param mv     MV信息
     */
    void updateMv(Mv mv);

    /**
     * 获取MV详情
     * 
     * @param mvId MV ID
     * @return MV详情
     */
    Mv getMvDetail(Integer mvId);

    /**
     * 获取MV列表
     * 
     * @return MV列表
     */
    List<Mv> getMvList();
}
