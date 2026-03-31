package com.hls.base.config;

public class MqConfig {

    public final static String EXCHANGE = "direct.music";
    public final static String EXCHANGE_DELAY = "direct.music.delay";

    //删除临时媒资文件
    public final static String MEDIA_TEMP_QUEUE = "queue.del.media";
    public final static String MEDIA_TEMP_KEY = "del.media";

    //更新歌曲播放量
    public final static String HOT_SONG_QUEUE = "queue.hot.song";
    public final static String HOT_SONG_KEY = "songHot";

    //更新mv播放量和点赞数
    public final static String HOT_MV_QUEUE = "queue.hot.mv";
    public final static String HOT_MV_KEY = "mvHot";

    //更新评论
    public final static String COMMENT_QUEUE = "queue.comment";
    public final static String COMMENT_KEY = "comment";

    //审核相关
    public final static String AUDIT_QUEUE = "queue.audit";
    public final static String AUDIT_KEY = "audit";
    public final static String AUDIT_RESULT_KEY = "audit.result";

    public final static String DEAD_EXCHANGE = "direct.dead";
    public final static String DEAD_QUEUE = "dead";
    public final static String DEAD_KEY = "dead";




}
