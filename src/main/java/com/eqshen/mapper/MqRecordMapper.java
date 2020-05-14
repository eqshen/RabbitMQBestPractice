package com.eqshen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eqshen.entity.MqRecord;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author eqshen
 * @since 2020-03-11
 */
public interface MqRecordMapper extends BaseMapper<MqRecord> {
    int updateStauts(@Param("msgId") String msgId, @Param("status") int status);

    int updateStautsWithCondition(@Param("msgId") String msgId, @Param("oldStatus") int oldStatus, @Param("status") int status);
}
