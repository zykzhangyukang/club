package com.coderman.club.service.section;

import com.coderman.club.vo.common.ResultVO;
import com.coderman.club.vo.section.SectionVO;

import java.util.List;

/**
 * @author Administrator
 */
public interface SectionService {

    /**
     * 板块列表获取
     *
     * @return
     */
    ResultVO<List<SectionVO>> getSectionVoCacheList();


    /**
     * 板块列表获取
     * @return
     */
    List<SectionVO> getSectionVoList();


    /**
     * 根据id板块列表获取
     * @param sectionId
     * @return
     */
    SectionVO getSectionVoById(Long sectionId);

    /**
     * 根据一级板块id查询所有二级板块
     *
     * @param firstSectionId
     * @return
     */
    List<SectionVO> getSectionVoByPid(Long firstSectionId);
}
