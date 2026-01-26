package content.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 歌手mv
 * </p>
 *
 * @author hls
 * @since 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mv")
public class Mv implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌曲id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌手id
     */
    private Integer singerId;

    /**
     * mv名称
     */
    private String name;

    /**
     * 视频源
     */
    private String video;

    /**
     * 封面
     */
    private String avatar;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 播放量
     */
    private Long playNum;

    /**
     * 评论数
     */
    private Long commentNum;

    /**
     * 点赞数
     */
    private Long likeNum;

    /**
     * 上传者
     */
    private Integer userId;

    private LocalDateTime createTime;


}
