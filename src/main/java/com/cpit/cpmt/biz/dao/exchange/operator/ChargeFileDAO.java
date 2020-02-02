package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ChargeFileDAO {

    int deleteByPrimaryKey(Integer fileId);

    int insertSelective(ChargeFile record);

    ChargeFile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(ChargeFile record);

    int updateByPrimaryKey(ChargeFile record);

    Page<ChargeFile> getChargeFileList(ChargeFile chargeFile);

    List<ChargeFile> getStationPictureList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);
}