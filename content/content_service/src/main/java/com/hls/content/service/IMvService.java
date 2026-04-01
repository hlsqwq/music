package com.hls.content.service;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.po.Mv;
import com.baomidou.mybatisplus.extension.service.IService;

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

    PageResult<Mv> pageBySinger(Integer id, PageParam pageParam);

    /**
     * 新增MV
     *
     * @param mv MV信息
     */
    void addMv(Mv mv);

    /**
     * 删除MV
     *
     * @param mvId MV ID
     */
    void deleteMv(Integer mvId);

    /**
     * 修改MV
     *
     * @param mv MV信息
     */
    void updateMv(Mv mv);

    /**
     * 获取MV列表
     * 
     * @return MV列表
     */
    List<Mv> getMvList();

    /**
     * 增加MV播放量
     *
     * @param mvId MV ID
     * @return
     */
    Long incrPlayNum(Integer mvId);

    /**
     * 增加MV点赞数
     *
     * @param mvId MV ID
     * @return 增加后的点赞数
     */
    Long incrOrDecrLikeNum(Integer mvId);

    void updatePlayOrLike(List<Mv> map);
}
