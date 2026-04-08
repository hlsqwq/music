package com.hls.search.service;

import com.hls.search.doc.MusicDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MusicRepository extends ElasticsearchRepository<MusicDoc, String> {



}
