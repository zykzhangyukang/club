package com.coderman.club.service.section.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.mapper.section.SectionMapper;
import com.coderman.club.model.section.SectionModel;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 14:31
 */
@Service
@Slf4j
public class SectionServiceImpl implements SectionService {

    @Resource
    private SectionMapper sectionMapper;

    @Resource
    private RedisService redisService;

    /**
     * 保存栏目缓存 (缓存1分钟防止频繁请求redis)
     */
    public static final Cache<String, List<SectionVO>> SECTION_CACHE_MAP = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .concurrencyLevel(5)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Override
    public ResultVO<List<SectionVO>> getSectionVoCacheList() {

        List<SectionVO> sectionVos = new ArrayList<>();
        try {
            sectionVos = SECTION_CACHE_MAP.get(RedisKeyConstant.REDIS_SECTION_CACHE, () -> redisService.getListData(RedisKeyConstant.REDIS_SECTION_CACHE, SectionVO.class
                    , RedisDbConstant.REDIS_BIZ_CACHE));
        } catch (Exception e) {
            log.error("获取栏目数据失败:{}", e.getMessage(), e);
        }
        return ResultUtil.getSuccessList(SectionVO.class, sectionVos);
    }

    @Override
    public List<SectionVO> getSectionVoList() {
        List<SectionModel> sectionModels = this.sectionMapper.selectList(Wrappers.<SectionModel>lambdaQuery()
                .eq(SectionModel::getIsActive, Boolean.TRUE));

        if(CollectionUtils.isEmpty(sectionModels)){
            return Lists.newArrayList();
        }

        // 组装成二级结构
        List<SectionVO> firstLevelSection = sectionModels.stream()
                .filter(e -> e.getParentSection() == 0)
                .map(e -> {
                    SectionVO sectionVO = new SectionVO();
                    BeanUtils.copyProperties(e, sectionVO);
                    return sectionVO;
                })
                .sorted(Comparator.comparingInt(SectionVO::getSort))
                .distinct().collect(Collectors.toList());

        for (SectionVO firstLevel : firstLevelSection) {
            List<SectionVO> secondLevel = new ArrayList<>();
            for (SectionModel sectionModel : sectionModels) {
                if (Objects.equals(sectionModel.getParentSection(), firstLevel.getSectionId())) {

                    SectionVO sectionVO = new SectionVO();
                    BeanUtils.copyProperties(sectionModel, sectionVO);
                    secondLevel.add(sectionVO);
                }
            }
            firstLevel.setChildren(secondLevel);
        }

        return firstLevelSection;
    }

    @Override
    public SectionVO getSectionVoById(Long sectionId) {
        if (sectionId == null) {
            return null;
        }

        SectionModel sectionModel = this.sectionMapper.selectOne(Wrappers.<SectionModel>lambdaQuery()
                .eq(SectionModel::getSectionId, sectionId)
                .eq(SectionModel::getIsActive, Boolean.TRUE)
                .last("limit 1"));

        if (sectionModel == null) {
            return null;
        }

        SectionVO sectionVO = new SectionVO();
        BeanUtils.copyProperties(sectionModel, sectionVO);
        return sectionVO;
    }

    @Override
    public List<SectionVO> getSectionVoByPid(Long firstSectionId) {

        if (firstSectionId == null) {
            return Lists.newArrayList();
        }

        List<SectionModel> sectionModels = this.sectionMapper.selectList(Wrappers.<SectionModel>lambdaQuery()
                .eq(SectionModel::getParentSection, firstSectionId)
                .eq(SectionModel::getIsActive, Boolean.TRUE));

        if (CollectionUtils.isEmpty(sectionModels)) {

            return Lists.newArrayList();
        }

        return sectionModels.stream().map(e -> {
            SectionVO sectionVO = new SectionVO();
            BeanUtils.copyProperties(e, sectionVO);
            return sectionVO;
        }).collect(Collectors.toList());
    }
}
