package com.coderman.club.dao.post;

import com.coderman.club.dao.common.BaseDAO;
import com.coderman.club.model.post.PostExample;
import com.coderman.club.model.post.PostModel;
import com.coderman.club.vo.post.PostListItemVO;

import java.util.List;
import java.util.Map;

public interface PostDAO extends BaseDAO<PostModel, PostExample> {

    /**
     * 分页总条数
     * @param conditionMap
     * @return
     */
    Long countPage(Map<String, Object> conditionMap);

    /**
     * 分页查询
     * @param conditionMap
     * @return
     */
    List<PostListItemVO> pageList(Map<String, Object> conditionMap);
}