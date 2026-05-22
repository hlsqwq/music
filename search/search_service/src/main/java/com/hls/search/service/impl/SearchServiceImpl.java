package com.hls.search.service.impl;


import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.po.*;
import com.hls.search.doc.MusicDoc;
import com.hls.search.enums.SearchType;
import com.hls.search.service.MusicRepository;
import com.hls.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 *
 * @author hls
 * @since 2026-04-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final MusicRepository musicRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    private static final Map<String, Integer> typeLimits = new HashMap<>();
    private static final List<String> matchFields = new ArrayList<>();

    static {
        typeLimits.put(SearchType.singer.getValue(), 3);
        typeLimits.put(SearchType.Song.getValue(), 20);
        typeLimits.put(SearchType.mv.getValue(), 5);
        typeLimits.put(SearchType.songList.getValue(), 5);
        typeLimits.put(SearchType.album.getValue(), 3);
        matchFields.add("name");
        matchFields.add("singerName");
        matchFields.add("albumName");
        matchFields.add("userName");
    }


    /**
     * 获取查询参数
     *
     * @param keyword 关键词
     * @param type    查询的类型
     * @return 查询参数
     */
    private NativeQuery getNativeQuery(String keyword, String type, String order, PageParam pageParam) {
        HighlightQuery highlightQuery = new HighlightQuery(
                new Highlight(HighlightParameters.builder()
                        .withPreTags("<es>")
                        .withPostTags("</es>")
                        .build(),
                        matchFields.stream()
                                .map(HighlightField::new)
                                .toList()),
                MusicDoc.class);
        return NativeQuery.builder()
                .withQuery(q ->
                        q.bool(b ->
                                b.must(m -> m.term(t -> t.field("docType")
                                                .value(type)))
                                        .must(m -> m.multiMatch(mm ->
                                                mm.query(keyword).fields(matchFields)))))
                .withHighlightQuery(highlightQuery)
                .withPageable(PageRequest.of(pageParam.getNum(), pageParam.getSize()))
                .withSort(s -> {
                    if (order == null) {
                        return s.score(sc -> sc.order(SortOrder.Desc));
                    } else {
                        return s.field(f -> f.field(order).order(SortOrder.Desc));
                    }
                })
                .build();
    }


    /**
     * 综合搜索
     *
     * @param keyword 关键词
     * @return 各类型搜索结果
     */
    @Override
    public R<Map<String, List<Object>>> findAll(String keyword) {
        List<String> types = typeLimits.keySet().stream().toList();
        List<NativeQuery> list = types.stream()
                .map(v -> getNativeQuery(keyword, v, null,
                        new PageParam(0, typeLimits.get(v), null)))
                .toList();

        // 执行 multiSearch
        List<SearchHits<MusicDoc>> multiResults = elasticsearchOperations
                .multiSearch(list, MusicDoc.class);

        // 处理结果
        Map<String, List<Object>> result = new HashMap<>();

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            SearchHits<MusicDoc> searchHits = multiResults.get(i);

            List<Object> objects = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        MusicDoc doc = hit.getContent();
                        // 将高亮结果填充到对应字段
                        applyHighlight(doc, hit.getHighlightFields());
                        // 转换为对应的 PO 类
                        return convertToPo(type, doc);
                    })
                    .collect(Collectors.toList());

            result.put(type, objects);
        }

        return R.success(result);
    }

    /**
     * 单类型搜索
     *
     * @param type      搜索类型
     * @param keyword   关键词
     * @param order     排序字段（hot/createTime）
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @Override
    public R<PageResult<Object>> findByType(String type, String keyword, String order, PageParam pageParam) {
        NativeQuery nativeQuery = getNativeQuery(keyword, type, order, pageParam);

        SearchHits<MusicDoc> searchHits = elasticsearchOperations.search(nativeQuery, MusicDoc.class);

        List<Object> objects = searchHits.getSearchHits().stream()
                .map(hit -> {
                    MusicDoc doc = hit.getContent();
                    // 将高亮结果填充到对应字段
                    applyHighlight(doc, hit.getHighlightFields());
                    // 转换为对应的 PO 类
                    return convertToPo(type, doc);
                })
                .collect(Collectors.toList());

        PageResult<Object> result = new PageResult<>();
        result.setTotal(searchHits.getTotalHits());
        result.setNum(pageParam.getNum());
        result.setSize(pageParam.getSize());
        result.setItem(objects);

        return R.success(result);
    }

    /**
     * 保存或更新文档
     *
     * @param doc 文档
     */
    @Override
    public void saveOrUpdate(MusicDoc doc) {
        musicRepository.save(doc);
        log.info("保存/更新文档成功：id={}", doc.getId());
    }

    /**
     * 删除文档
     *
     * @param id 文档 ID
     */
    @Override
    public void delete(String id) {
        musicRepository.deleteById(id);
        log.info("删除文档成功：id={}", id);
    }

    /**
     * 应用高亮到文档字段
     *
     * @param doc             文档
     * @param highlightFields 高亮字段
     */
    private void applyHighlight(MusicDoc doc, Map<String, List<String>> highlightFields) {
        if (highlightFields == null || highlightFields.isEmpty()) {
            return;
        }

        // 将高亮结果直接填充到对应字段
        if (highlightFields.containsKey("name")) {
            doc.setName(String.join(",", highlightFields.get("name")));
        }
        if (highlightFields.containsKey("singerName")) {
            doc.setSingerName(String.join(",", highlightFields.get("singerName")));
        }
        if (highlightFields.containsKey("albumName")) {
            doc.setAlbumName(String.join(",", highlightFields.get("albumName")));
        }
        if (highlightFields.containsKey("userName")) {
            doc.setUserName(String.join(",", highlightFields.get("userName")));
        }
    }

    /**
     * 将 MusicDoc 转换为对应的 PO 类
     *
     * @param type 类型
     * @param doc  文档
     * @return 对应的 PO 对象
     */
    private Object convertToPo(String type, MusicDoc doc) {
        switch (type) {
            case "singer":
                Singer singer = BeanUtil.copyProperties(doc, Singer.class);
                singer.setId(doc.getDocId());
                return singer;
            case "song":
                Song song = BeanUtil.copyProperties(doc, Song.class);
                song.setId(doc.getDocId());
                return song;
            case "mv":
                Mv mv = BeanUtil.copyProperties(doc, Mv.class);
                mv.setId(doc.getDocId());
                return mv;
            case "album":
                Album album = BeanUtil.copyProperties(doc, Album.class);
                album.setId(doc.getDocId());
                return album;
            case "songList":
                SongList songList = BeanUtil.copyProperties(doc, SongList.class);
                songList.setId(doc.getDocId());
                return songList;
            default:
                return doc;
        }
    }
}
