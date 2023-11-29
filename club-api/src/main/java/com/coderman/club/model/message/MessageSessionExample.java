package com.coderman.club.model.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageSessionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MessageSessionExample() {
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

        public Criteria andSessionIdIsNull() {
            addCriterion("session_id is null");
            return (Criteria) this;
        }

        public Criteria andSessionIdIsNotNull() {
            addCriterion("session_id is not null");
            return (Criteria) this;
        }

        public Criteria andSessionIdEqualTo(Long value) {
            addCriterion("session_id =", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotEqualTo(Long value) {
            addCriterion("session_id <>", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdGreaterThan(Long value) {
            addCriterion("session_id >", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("session_id >=", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdLessThan(Long value) {
            addCriterion("session_id <", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdLessThanOrEqualTo(Long value) {
            addCriterion("session_id <=", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdIn(List<Long> values) {
            addCriterion("session_id in", values, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotIn(List<Long> values) {
            addCriterion("session_id not in", values, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdBetween(Long value1, Long value2) {
            addCriterion("session_id between", value1, value2, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotBetween(Long value1, Long value2) {
            addCriterion("session_id not between", value1, value2, "sessionId");
            return (Criteria) this;
        }

        public Criteria andUserOneIsNull() {
            addCriterion("user_one is null");
            return (Criteria) this;
        }

        public Criteria andUserOneIsNotNull() {
            addCriterion("user_one is not null");
            return (Criteria) this;
        }

        public Criteria andUserOneEqualTo(Long value) {
            addCriterion("user_one =", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneNotEqualTo(Long value) {
            addCriterion("user_one <>", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneGreaterThan(Long value) {
            addCriterion("user_one >", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneGreaterThanOrEqualTo(Long value) {
            addCriterion("user_one >=", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneLessThan(Long value) {
            addCriterion("user_one <", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneLessThanOrEqualTo(Long value) {
            addCriterion("user_one <=", value, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneIn(List<Long> values) {
            addCriterion("user_one in", values, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneNotIn(List<Long> values) {
            addCriterion("user_one not in", values, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneBetween(Long value1, Long value2) {
            addCriterion("user_one between", value1, value2, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserOneNotBetween(Long value1, Long value2) {
            addCriterion("user_one not between", value1, value2, "userOne");
            return (Criteria) this;
        }

        public Criteria andUserTwoIsNull() {
            addCriterion("user_two is null");
            return (Criteria) this;
        }

        public Criteria andUserTwoIsNotNull() {
            addCriterion("user_two is not null");
            return (Criteria) this;
        }

        public Criteria andUserTwoEqualTo(Long value) {
            addCriterion("user_two =", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoNotEqualTo(Long value) {
            addCriterion("user_two <>", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoGreaterThan(Long value) {
            addCriterion("user_two >", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoGreaterThanOrEqualTo(Long value) {
            addCriterion("user_two >=", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoLessThan(Long value) {
            addCriterion("user_two <", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoLessThanOrEqualTo(Long value) {
            addCriterion("user_two <=", value, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoIn(List<Long> values) {
            addCriterion("user_two in", values, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoNotIn(List<Long> values) {
            addCriterion("user_two not in", values, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoBetween(Long value1, Long value2) {
            addCriterion("user_two between", value1, value2, "userTwo");
            return (Criteria) this;
        }

        public Criteria andUserTwoNotBetween(Long value1, Long value2) {
            addCriterion("user_two not between", value1, value2, "userTwo");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdIsNull() {
            addCriterion("last_message_id is null");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdIsNotNull() {
            addCriterion("last_message_id is not null");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdEqualTo(Long value) {
            addCriterion("last_message_id =", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdNotEqualTo(Long value) {
            addCriterion("last_message_id <>", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdGreaterThan(Long value) {
            addCriterion("last_message_id >", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdGreaterThanOrEqualTo(Long value) {
            addCriterion("last_message_id >=", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdLessThan(Long value) {
            addCriterion("last_message_id <", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdLessThanOrEqualTo(Long value) {
            addCriterion("last_message_id <=", value, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdIn(List<Long> values) {
            addCriterion("last_message_id in", values, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdNotIn(List<Long> values) {
            addCriterion("last_message_id not in", values, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdBetween(Long value1, Long value2) {
            addCriterion("last_message_id between", value1, value2, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageIdNotBetween(Long value1, Long value2) {
            addCriterion("last_message_id not between", value1, value2, "lastMessageId");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeIsNull() {
            addCriterion("last_message_time is null");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeIsNotNull() {
            addCriterion("last_message_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeEqualTo(Date value) {
            addCriterion("last_message_time =", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeNotEqualTo(Date value) {
            addCriterion("last_message_time <>", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeGreaterThan(Date value) {
            addCriterion("last_message_time >", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_message_time >=", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeLessThan(Date value) {
            addCriterion("last_message_time <", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_message_time <=", value, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeIn(List<Date> values) {
            addCriterion("last_message_time in", values, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeNotIn(List<Date> values) {
            addCriterion("last_message_time not in", values, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeBetween(Date value1, Date value2) {
            addCriterion("last_message_time between", value1, value2, "lastMessageTime");
            return (Criteria) this;
        }

        public Criteria andLastMessageTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_message_time not between", value1, value2, "lastMessageTime");
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