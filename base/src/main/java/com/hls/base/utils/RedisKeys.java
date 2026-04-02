package com.hls.base.utils;


import com.hls.base.PageParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeys {

    private final RedisBase redisBase;


    @Getter
    @AllArgsConstructor
    public enum DoType {
        play("play"),
        like("like"),
        fans("fans"),
        comment("comment"),
        commentLike("commentLike"),
        getCommentList("commentList"),
        getCommentFlag("getCommentFlag"),
        checkFile("checkFile"),
        singerTop("singerTop"),
        favorite("favorite");
        private final String doName;
    }

    @Getter
    @AllArgsConstructor
    public enum TableType {
        song("song"),
        mv("mv"),
        singer("singer"),
        comment("comment"),
        songList("songList"),
        media("media"),
        album("album");
        private final String tableName;
    }


    /**
     * 获取歌曲播放量
     * @param songId 歌曲id
     * @return
     */
    public String getSongPlay(Integer songId){
        return redisBase.getKey(DoType.play.getDoName(), TableType.song.getTableName(), songId);
    }

    /**
     * 获取歌手粉丝id
     * @param singerId 歌手id
     * @return
     */
    public String getSingerFans(Integer singerId){
        return redisBase.getKey(DoType.fans.getDoName(), TableType.singer.getTableName(), singerId);
    }

    /**
     * 获取歌手的排行榜信息
     * @param id 分类id
     * @return
     */
    public String getSingerTop(Integer id){
        if(id == null){
            //代表总榜单
            id=0;
        }
        return redisBase.getKey(DoType.singerTop.getDoName(), TableType.singer.getTableName(),id);
    }

    public String getSongTop(){
        return redisBase.getKey(DoType.singerTop.getDoName(), TableType.song.getTableName());
    }

    /**
     * 获取歌曲热度排行榜
     * @return 歌曲热度排行榜键
     */
    public String getSongHot(){
        return redisBase.getKey(DoType.singerTop.getDoName(), TableType.song.getTableName());
    }

    /**
     * 检查文件或分块是否存在
     *
     * @param md5 文件MD5
     * @return 文件或分块 key
     */
    public String checkFileExist(String md5) {
        return redisBase.getKey(DoType.checkFile.getDoName(),
                TableType.media.getTableName(), md5);
    }

    /**
     * 检查分块是否存在
     *
     * @param md5 完整文件MD5
     * @param id  分块索引
     * @return 文件或分块 key
     */
    public String checkChunkExist(String md5, Integer id) {
        return redisBase.getKey(DoType.checkFile.getDoName(),
                TableType.media.getTableName(), md5 + id);
    }


    /**
     * 评论列表
     *
     * @param type 是mv还是song 的评论
     * @param id   mv 或者 song 的id
     * @return
     */
    public String commentList(TableType type, Integer id) {
        return redisBase.getKey(DoType.getCommentList.getDoName(),
                type.getTableName(), id.toString());
    }


    /**
     * 评论列表 flag
     *
     * @param type 是mv还是song 的评论
     * @param id   mv 或者 song 的id
     * @return
     */
    public String commentFlag(TableType type, Integer id, PageParam pageParam) {
        return redisBase.getKey(DoType.getCommentFlag.getDoName(),
                type.getTableName(), id.toString() + pageParam.getNum() + pageParam.getSize());
    }

    /**
     * 评论数
     *
     * @param type 是mv还是song 的评论数
     * @param id   mv 或者 song 的id
     * @return
     */
    public String commentNumber(TableType type, Integer id) {
        return redisBase.getKey(DoType.comment.getDoName(),
                type.getTableName(), id.toString());
    }

    /**
     * 评论数点赞数
     *
     * @param type 是mv还是song 的评论数
     * @param id   mv 或者 song 的id
     * @return
     */
    public String commentLike(TableType type, Integer id) {
        return redisBase.getKey(DoType.commentLike.getDoName(),
                type.getTableName(), id.toString());
    }


}
