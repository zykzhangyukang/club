package com.coderman.club.service.section.impl;

import com.coderman.club.constant.redis.RedisDbConstant;
import com.coderman.club.constant.redis.RedisKeyConstant;
import com.coderman.club.dao.section.SectionDAO;
import com.coderman.club.model.section.SectionExample;
import com.coderman.club.model.section.SectionModel;
import com.coderman.club.service.redis.RedisService;
import com.coderman.club.service.section.SectionService;
import com.coderman.club.utils.ResultUtil;
import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ：zhangyukang
 * @date ：2023/11/24 14:31
 */
@Service
public class SectionServiceImpl implements SectionService {

    @Resource
    private SectionDAO sectionDAO;

    @Resource
    private RedisService redisService;

    @Override
    public ResultVO<List<SectionVO>> list() {

        // 先查缓存
        List<SectionVO> cacheList = this.getSectionCache();
        if (CollectionUtils.isNotEmpty(cacheList)) {

            return ResultUtil.getSuccessList(SectionVO.class, cacheList);
        }

        SectionExample example = new SectionExample();
        example.createCriteria().andIsActiveEqualTo(Boolean.TRUE);
        List<SectionModel> sectionModels = this.sectionDAO.selectByExample(example);

        // 组装成二级结构
        List<SectionVO> firstLevelSection = sectionModels.stream()
                .filter(e -> e.getParentSection() == 0)
                .map(e -> {
                    SectionVO sectionVO = new SectionVO();
                    BeanUtils.copyProperties(e, sectionVO);
                    return sectionVO;
                })
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
        // 写缓存
        setSectionCache(firstLevelSection);
        return ResultUtil.getSuccessList(SectionVO.class, firstLevelSection);
    }

    private void setSectionCache(List<SectionVO> sectionVos) {

        if(CollectionUtils.isEmpty(sectionVos)){
            return;
        }
        this.redisService.setList(RedisKeyConstant.REDIS_SECTION_CACHE, sectionVos, RedisDbConstant.REDIS_BIZ_CACHE);
        // 设置有效期1分钟
        this.redisService.expire(RedisKeyConstant.REDIS_SECTION_CACHE, 60,RedisDbConstant.REDIS_BIZ_CACHE);
    }

    private List<SectionVO> getSectionCache() {
        return this.redisService.getList(RedisKeyConstant.REDIS_SECTION_CACHE, SectionVO.class, RedisDbConstant.REDIS_BIZ_CACHE);
    }
}
