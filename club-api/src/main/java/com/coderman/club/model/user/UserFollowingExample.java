package com.coderman.club.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserFollowingExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UserFollowingExample() {
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

        public Criteria andFollowIdIsNull() {
            addCriterion("follow_id is null");
            return (Criteria) this;
        }

        public Criteria andFollowIdIsNotNull() {
            addCriterion("follow_id is not null");
            return (Criteria) this;
        }

        public Criteria andFollowIdEqualTo(Long value) {
            addCriterion("follow_id =", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdNotEqualTo(Long value) {
            addCriterion("follow_id <>", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdGreaterThan(Long value) {
            addCriterion("follow_id >", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdGreaterThanOrEqualTo(Long value) {
            addCriterion("follow_id >=", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdLessThan(Long value) {
            addCriterion("follow_id <", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdLessThanOrEqualTo(Long value) {
            addCriterion("follow_id <=", value, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdIn(List<Long> values) {
            addCriterion("follow_id in", values, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdNotIn(List<Long> values) {
            addCriterion("follow_id not in", values, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdBetween(Long value1, Long value2) {
            addCriterion("follow_id between", value1, value2, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowIdNotBetween(Long value1, Long value2) {
            addCriterion("follow_id not between", value1, value2, "followId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdIsNull() {
            addCriterion("follower_id is null");
            return (Criteria) this;
        }

        public Criteria andFollowerIdIsNotNull() {
            addCriterion("follower_id is not null");
            return (Criteria) this;
        }

        public Criteria andFollowerIdEqualTo(Long value) {
            addCriterion("follower_id =", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdNotEqualTo(Long value) {
            addCriterion("follower_id <>", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdGreaterThan(Long value) {
            addCriterion("follower_id >", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdGreaterThanOrEqualTo(Long value) {
            addCriterion("follower_id >=", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdLessThan(Long value) {
            addCriterion("follower_id <", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdLessThanOrEqualTo(Long value) {
            addCriterion("follower_id <=", value, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdIn(List<Long> values) {
            addCriterion("follower_id in", values, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdNotIn(List<Long> values) {
            addCriterion("follower_id not in", values, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdBetween(Long value1, Long value2) {
            addCriterion("follower_id between", value1, value2, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowerIdNotBetween(Long value1, Long value2) {
            addCriterion("follower_id not between", value1, value2, "followerId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdIsNull() {
            addCriterion("followed_id is null");
            return (Criteria) this;
        }

        public Criteria andFollowedIdIsNotNull() {
            addCriterion("followed_id is not null");
            return (Criteria) this;
        }

        public Criteria andFollowedIdEqualTo(Long value) {
            addCriterion("followed_id =", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdNotEqualTo(Long value) {
            addCriterion("followed_id <>", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdGreaterThan(Long value) {
            addCriterion("followed_id >", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdGreaterThanOrEqualTo(Long value) {
            addCriterion("followed_id >=", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdLessThan(Long value) {
            addCriterion("followed_id <", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdLessThanOrEqualTo(Long value) {
            addCriterion("followed_id <=", value, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdIn(List<Long> values) {
            addCriterion("followed_id in", values, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdNotIn(List<Long> values) {
            addCriterion("followed_id not in", values, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdBetween(Long value1, Long value2) {
            addCriterion("followed_id between", value1, value2, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowedIdNotBetween(Long value1, Long value2) {
            addCriterion("followed_id not between", value1, value2, "followedId");
            return (Criteria) this;
        }

        public Criteria andFollowDateIsNull() {
            addCriterion("follow_date is null");
            return (Criteria) this;
        }

        public Criteria andFollowDateIsNotNull() {
            addCriterion("follow_date is not null");
            return (Criteria) this;
        }

        public Criteria andFollowDateEqualTo(Date value) {
            addCriterion("follow_date =", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateNotEqualTo(Date value) {
            addCriterion("follow_date <>", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateGreaterThan(Date value) {
            addCriterion("follow_date >", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateGreaterThanOrEqualTo(Date value) {
            addCriterion("follow_date >=", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateLessThan(Date value) {
            addCriterion("follow_date <", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateLessThanOrEqualTo(Date value) {
            addCriterion("follow_date <=", value, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateIn(List<Date> values) {
            addCriterion("follow_date in", values, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateNotIn(List<Date> values) {
            addCriterion("follow_date not in", values, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateBetween(Date value1, Date value2) {
            addCriterion("follow_date between", value1, value2, "followDate");
            return (Criteria) this;
        }

        public Criteria andFollowDateNotBetween(Date value1, Date value2) {
            addCriterion("follow_date not between", value1, value2, "followDate");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("status like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("status not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("status not between", value1, value2, "status");
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