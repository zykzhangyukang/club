package com.coderman.club.model.post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PostExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andPostIdIsNull() {
            addCriterion("post_id is null");
            return (Criteria) this;
        }

        public Criteria andPostIdIsNotNull() {
            addCriterion("post_id is not null");
            return (Criteria) this;
        }

        public Criteria andPostIdEqualTo(Long value) {
            addCriterion("post_id =", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdNotEqualTo(Long value) {
            addCriterion("post_id <>", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdGreaterThan(Long value) {
            addCriterion("post_id >", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdGreaterThanOrEqualTo(Long value) {
            addCriterion("post_id >=", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdLessThan(Long value) {
            addCriterion("post_id <", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdLessThanOrEqualTo(Long value) {
            addCriterion("post_id <=", value, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdIn(List<Long> values) {
            addCriterion("post_id in", values, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdNotIn(List<Long> values) {
            addCriterion("post_id not in", values, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdBetween(Long value1, Long value2) {
            addCriterion("post_id between", value1, value2, "postId");
            return (Criteria) this;
        }

        public Criteria andPostIdNotBetween(Long value1, Long value2) {
            addCriterion("post_id not between", value1, value2, "postId");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andSectionIdIsNull() {
            addCriterion("section_id is null");
            return (Criteria) this;
        }

        public Criteria andSectionIdIsNotNull() {
            addCriterion("section_id is not null");
            return (Criteria) this;
        }

        public Criteria andSectionIdEqualTo(Long value) {
            addCriterion("section_id =", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdNotEqualTo(Long value) {
            addCriterion("section_id <>", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdGreaterThan(Long value) {
            addCriterion("section_id >", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("section_id >=", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdLessThan(Long value) {
            addCriterion("section_id <", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdLessThanOrEqualTo(Long value) {
            addCriterion("section_id <=", value, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdIn(List<Long> values) {
            addCriterion("section_id in", values, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdNotIn(List<Long> values) {
            addCriterion("section_id not in", values, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdBetween(Long value1, Long value2) {
            addCriterion("section_id between", value1, value2, "sectionId");
            return (Criteria) this;
        }

        public Criteria andSectionIdNotBetween(Long value1, Long value2) {
            addCriterion("section_id not between", value1, value2, "sectionId");
            return (Criteria) this;
        }

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andViewsCountIsNull() {
            addCriterion("views_count is null");
            return (Criteria) this;
        }

        public Criteria andViewsCountIsNotNull() {
            addCriterion("views_count is not null");
            return (Criteria) this;
        }

        public Criteria andViewsCountEqualTo(Integer value) {
            addCriterion("views_count =", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountNotEqualTo(Integer value) {
            addCriterion("views_count <>", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountGreaterThan(Integer value) {
            addCriterion("views_count >", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("views_count >=", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountLessThan(Integer value) {
            addCriterion("views_count <", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountLessThanOrEqualTo(Integer value) {
            addCriterion("views_count <=", value, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountIn(List<Integer> values) {
            addCriterion("views_count in", values, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountNotIn(List<Integer> values) {
            addCriterion("views_count not in", values, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountBetween(Integer value1, Integer value2) {
            addCriterion("views_count between", value1, value2, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andViewsCountNotBetween(Integer value1, Integer value2) {
            addCriterion("views_count not between", value1, value2, "viewsCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountIsNull() {
            addCriterion("likes_count is null");
            return (Criteria) this;
        }

        public Criteria andLikesCountIsNotNull() {
            addCriterion("likes_count is not null");
            return (Criteria) this;
        }

        public Criteria andLikesCountEqualTo(Integer value) {
            addCriterion("likes_count =", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountNotEqualTo(Integer value) {
            addCriterion("likes_count <>", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountGreaterThan(Integer value) {
            addCriterion("likes_count >", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("likes_count >=", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountLessThan(Integer value) {
            addCriterion("likes_count <", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountLessThanOrEqualTo(Integer value) {
            addCriterion("likes_count <=", value, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountIn(List<Integer> values) {
            addCriterion("likes_count in", values, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountNotIn(List<Integer> values) {
            addCriterion("likes_count not in", values, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountBetween(Integer value1, Integer value2) {
            addCriterion("likes_count between", value1, value2, "likesCount");
            return (Criteria) this;
        }

        public Criteria andLikesCountNotBetween(Integer value1, Integer value2) {
            addCriterion("likes_count not between", value1, value2, "likesCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountIsNull() {
            addCriterion("comments_count is null");
            return (Criteria) this;
        }

        public Criteria andCommentsCountIsNotNull() {
            addCriterion("comments_count is not null");
            return (Criteria) this;
        }

        public Criteria andCommentsCountEqualTo(Integer value) {
            addCriterion("comments_count =", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountNotEqualTo(Integer value) {
            addCriterion("comments_count <>", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountGreaterThan(Integer value) {
            addCriterion("comments_count >", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("comments_count >=", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountLessThan(Integer value) {
            addCriterion("comments_count <", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountLessThanOrEqualTo(Integer value) {
            addCriterion("comments_count <=", value, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountIn(List<Integer> values) {
            addCriterion("comments_count in", values, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountNotIn(List<Integer> values) {
            addCriterion("comments_count not in", values, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountBetween(Integer value1, Integer value2) {
            addCriterion("comments_count between", value1, value2, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andCommentsCountNotBetween(Integer value1, Integer value2) {
            addCriterion("comments_count not between", value1, value2, "commentsCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountIsNull() {
            addCriterion("favorites_count is null");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountIsNotNull() {
            addCriterion("favorites_count is not null");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountEqualTo(Integer value) {
            addCriterion("favorites_count =", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountNotEqualTo(Integer value) {
            addCriterion("favorites_count <>", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountGreaterThan(Integer value) {
            addCriterion("favorites_count >", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("favorites_count >=", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountLessThan(Integer value) {
            addCriterion("favorites_count <", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountLessThanOrEqualTo(Integer value) {
            addCriterion("favorites_count <=", value, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountIn(List<Integer> values) {
            addCriterion("favorites_count in", values, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountNotIn(List<Integer> values) {
            addCriterion("favorites_count not in", values, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountBetween(Integer value1, Integer value2) {
            addCriterion("favorites_count between", value1, value2, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andFavoritesCountNotBetween(Integer value1, Integer value2) {
            addCriterion("favorites_count not between", value1, value2, "favoritesCount");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtIsNull() {
            addCriterion("last_updated_at is null");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtIsNotNull() {
            addCriterion("last_updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtEqualTo(Date value) {
            addCriterion("last_updated_at =", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtNotEqualTo(Date value) {
            addCriterion("last_updated_at <>", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtGreaterThan(Date value) {
            addCriterion("last_updated_at >", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("last_updated_at >=", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtLessThan(Date value) {
            addCriterion("last_updated_at <", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("last_updated_at <=", value, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtIn(List<Date> values) {
            addCriterion("last_updated_at in", values, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtNotIn(List<Date> values) {
            addCriterion("last_updated_at not in", values, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("last_updated_at between", value1, value2, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("last_updated_at not between", value1, value2, "lastUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNull() {
            addCriterion("is_active is null");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNotNull() {
            addCriterion("is_active is not null");
            return (Criteria) this;
        }

        public Criteria andIsActiveEqualTo(Boolean value) {
            addCriterion("is_active =", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotEqualTo(Boolean value) {
            addCriterion("is_active <>", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThan(Boolean value) {
            addCriterion("is_active >", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_active >=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThan(Boolean value) {
            addCriterion("is_active <", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThanOrEqualTo(Boolean value) {
            addCriterion("is_active <=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveIn(List<Boolean> values) {
            addCriterion("is_active in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotIn(List<Boolean> values) {
            addCriterion("is_active not in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active not between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsDraftIsNull() {
            addCriterion("is_draft is null");
            return (Criteria) this;
        }

        public Criteria andIsDraftIsNotNull() {
            addCriterion("is_draft is not null");
            return (Criteria) this;
        }

        public Criteria andIsDraftEqualTo(Boolean value) {
            addCriterion("is_draft =", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftNotEqualTo(Boolean value) {
            addCriterion("is_draft <>", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftGreaterThan(Boolean value) {
            addCriterion("is_draft >", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_draft >=", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftLessThan(Boolean value) {
            addCriterion("is_draft <", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftLessThanOrEqualTo(Boolean value) {
            addCriterion("is_draft <=", value, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftIn(List<Boolean> values) {
            addCriterion("is_draft in", values, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftNotIn(List<Boolean> values) {
            addCriterion("is_draft not in", values, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftBetween(Boolean value1, Boolean value2) {
            addCriterion("is_draft between", value1, value2, "isDraft");
            return (Criteria) this;
        }

        public Criteria andIsDraftNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_draft not between", value1, value2, "isDraft");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}