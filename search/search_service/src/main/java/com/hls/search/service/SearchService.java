package com.hls.search.service;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.search.doc.MusicDoc;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * @author hls
 * @since 2026-04-07
 */
public interface SearchService {

    /**
     * 综合搜索
     *
     * @param key 关键词
     * @return 各类型搜索结果
     */
    R<Map<String, List<Object>>> findAll(String key);

    /**
     * 单类型搜索
     *
     * @param type      搜索类型
     * @param keyword   关键词
     * @param order     排序字段（hot/createTime）
     * @param pageParam 分页参数
     * @return 分页结果
     */
    R<PageResult<Object>> findByType(String type, String keyword, String order, PageParam pageParam);

    /**
     * 保存或更新文档
     * @param doc 文档
     */
    void saveOrUpdate(MusicDoc doc);

    /**
     * 删除文档
     * @param id 文档 ID
     */
    void delete(String id);
}
