package com.coderman.club.service.serial.impl;
import com.coderman.club.dao.serial.SerialNumberDAO;
import com.coderman.club.enums.SerialTypeEnum;
import com.coderman.club.model.serial.SerialNumberModel;
import com.coderman.club.service.serial.SerialNumberService;
import com.coderman.club.vo.serial.SerialNumberVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author coderman
 * @date 2022/6/2119:51
 */
@Slf4j
@Service
public class SerialNumberServiceImpl implements SerialNumberService {


    /**
     * {
     *   call usp_get_serial_number
     *   (
     *      #{serialType,mode=IN,jdbcType=VARCHAR},
     *      #{serialCount,mode=IN,jdbcType=INTEGER},
     *      #{nextSeq,mode=OUT,jdbcType=INTEGER},
     *      #{updateTime,mode=OUT,jdbcType=TIMESTAMP},
     *   )
     * }
     *
     *
     */
    @Resource
    private SerialNumberDAO serialNumberDAO;


    /**
     * 编号填充
     */
    public static final String SERIAL_NUMBER_PAD = "0";


    /**
     * 编号日期格式
     */
    public static final String SERIAL_NUMBER_YMD = "yyyyMMdd";


    // 编号缓冲区
    private Map<SerialTypeEnum, List<String>> serialBufferMap = new HashMap<>();


    // 序列表编号map
    private final Map<SerialTypeEnum, SerialNumberModel> dbSerialMap = new HashMap<>();


    @Override
    public List<String> noTranGetSerialNumberList(SerialTypeEnum serialType, Integer serialCount) {


        synchronized (serialType.getKey()) {

            // 获取缓存区里面的编号
            List<String> serialBufferList = this.serialBufferMap.getOrDefault(serialType, new ArrayList<>());

            // 初始化序列编号map
            this.initSerialNumberMap(serialType);


            // 缓冲编号不足,从数据库获取

            if (serialBufferList.size() < serialCount) {

                List<String> codeList = this.getDBSerial(serialType, serialCount);
                serialBufferList.addAll(codeList);
            }

            // 返回序列编号列表
            List<String> serialNumberList = serialBufferList.subList(0, serialCount);

            // 此处不用subList 防止溢出
            List<String> newBufferList =  new ArrayList<>();

            for (int i = serialCount; i < serialBufferList.size(); i++) {

                newBufferList.add(serialBufferList.get(i));
            }

            this.serialBufferMap.put(serialType,newBufferList);

            return serialNumberList;
        }

    }

    @Override
    public void clearSerialBufferMap() {
        log.info("-----------------清除编号缓冲区开始-----------------");

        log.info("serialBufferMap:{}",serialBufferMap);

        this.serialBufferMap = new HashMap<>();
        log.info("-----------------清除编号缓冲区结束-----------------");
    }

    private List<String> getDBSerial(SerialTypeEnum serialType, Integer serialCount) {

        SerialNumberModel bufferSerial = this.dbSerialMap.get(serialType);

        // 是否使用缓存

        boolean useBuffer = true;


        try {


            Date currentTime = new Date();

            // 年月日
            String ymd = DateFormatUtils.format(currentTime,"yyyy-MM-dd");


            Date twentyThreeHour = DateUtils.parseDate(ymd + " 23:45:00","yyyy-MM-dd HH:mm:ss");
            Date zeroHour = DateUtils.parseDate(ymd + " 00:15:00","yyyy-MM-dd HH:mm:ss");

            if (currentTime.compareTo(twentyThreeHour) > 0 || currentTime.compareTo(zeroHour) < 0) {

                useBuffer = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (useBuffer) {

            if (serialCount > bufferSerial.getBufferStep()) {

                serialCount += bufferSerial.getBufferStep();
            } else {
                serialCount = bufferSerial.getBufferStep();
            }
        }

        // 生成序列号
        SerialNumberVO dbSerial = this.updateDBSerial(serialType, serialCount);

        // 序列表编号列表
        List<String> serialNumberList = new ArrayList<>();

        // 第一个序列号
        Integer serialSeq = dbSerial.getNextSeq();


        if (bufferSerial.getIsYmd()) {

            String serialYmd = StringUtils.right(DateFormatUtils.format(dbSerial.getUpdateTime(), SERIAL_NUMBER_YMD), 6);
            for (int i = 0; i < serialCount; i++) {

                String sequenceNumberStr = StringUtils.leftPad(serialSeq.toString(), bufferSerial.getDigitWith(), SERIAL_NUMBER_PAD);
                serialNumberList.add(bufferSerial.getSerialPrefix() +  serialYmd + sequenceNumberStr);

                serialSeq ++;
            }

        } else {

            for (int i = 0; i < serialCount; i++) {

                String sequenceNumberStr = StringUtils.leftPad(serialSeq.toString(), bufferSerial.getDigitWith(), SERIAL_NUMBER_PAD);
                serialNumberList.add(bufferSerial.getSerialPrefix()  + sequenceNumberStr);

                serialSeq ++;
            }

        }


        return serialNumberList;
    }

    private SerialNumberVO updateDBSerial(SerialTypeEnum serialType, Integer serialCount) {

        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("serialType", serialType.getKey());
        paramMap.put("serialCount", serialCount);

        this.serialNumberDAO.getSerialNumber(paramMap);

        SerialNumberVO dbSerial = new SerialNumberVO();

        dbSerial.setNextSeq((Integer) paramMap.get("nextSeq"));
        dbSerial.setUpdateTime((Date) paramMap.get("updateTime"));

        return dbSerial;
    }


    private void initSerialNumberMap(SerialTypeEnum serialType) {

        if (!this.dbSerialMap.containsKey(serialType)) {

            SerialNumberModel serialNumModel = this.serialNumberDAO.selectByPrimaryKey(serialType.getKey());

            this.dbSerialMap.put(serialType, serialNumModel);
        }

    }
}
