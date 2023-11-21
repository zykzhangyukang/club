package com.coderman.club.model.serial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SerialNumberExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SerialNumberExample() {
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

        public Criteria andSerialTypeIsNull() {
            addCriterion("serial_type is null");
            return (Criteria) this;
        }

        public Criteria andSerialTypeIsNotNull() {
            addCriterion("serial_type is not null");
            return (Criteria) this;
        }

        public Criteria andSerialTypeEqualTo(String value) {
            addCriterion("serial_type =", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeNotEqualTo(String value) {
            addCriterion("serial_type <>", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeGreaterThan(String value) {
            addCriterion("serial_type >", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeGreaterThanOrEqualTo(String value) {
            addCriterion("serial_type >=", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeLessThan(String value) {
            addCriterion("serial_type <", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeLessThanOrEqualTo(String value) {
            addCriterion("serial_type <=", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeLike(String value) {
            addCriterion("serial_type like", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeNotLike(String value) {
            addCriterion("serial_type not like", value, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeIn(List<String> values) {
            addCriterion("serial_type in", values, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeNotIn(List<String> values) {
            addCriterion("serial_type not in", values, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeBetween(String value1, String value2) {
            addCriterion("serial_type between", value1, value2, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialTypeNotBetween(String value1, String value2) {
            addCriterion("serial_type not between", value1, value2, "serialType");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixIsNull() {
            addCriterion("serial_prefix is null");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixIsNotNull() {
            addCriterion("serial_prefix is not null");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixEqualTo(String value) {
            addCriterion("serial_prefix =", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixNotEqualTo(String value) {
            addCriterion("serial_prefix <>", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixGreaterThan(String value) {
            addCriterion("serial_prefix >", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixGreaterThanOrEqualTo(String value) {
            addCriterion("serial_prefix >=", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixLessThan(String value) {
            addCriterion("serial_prefix <", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixLessThanOrEqualTo(String value) {
            addCriterion("serial_prefix <=", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixLike(String value) {
            addCriterion("serial_prefix like", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixNotLike(String value) {
            addCriterion("serial_prefix not like", value, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixIn(List<String> values) {
            addCriterion("serial_prefix in", values, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixNotIn(List<String> values) {
            addCriterion("serial_prefix not in", values, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixBetween(String value1, String value2) {
            addCriterion("serial_prefix between", value1, value2, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andSerialPrefixNotBetween(String value1, String value2) {
            addCriterion("serial_prefix not between", value1, value2, "serialPrefix");
            return (Criteria) this;
        }

        public Criteria andIsYmdIsNull() {
            addCriterion("is_ymd is null");
            return (Criteria) this;
        }

        public Criteria andIsYmdIsNotNull() {
            addCriterion("is_ymd is not null");
            return (Criteria) this;
        }

        public Criteria andIsYmdEqualTo(Boolean value) {
            addCriterion("is_ymd =", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdNotEqualTo(Boolean value) {
            addCriterion("is_ymd <>", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdGreaterThan(Boolean value) {
            addCriterion("is_ymd >", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_ymd >=", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdLessThan(Boolean value) {
            addCriterion("is_ymd <", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdLessThanOrEqualTo(Boolean value) {
            addCriterion("is_ymd <=", value, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdIn(List<Boolean> values) {
            addCriterion("is_ymd in", values, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdNotIn(List<Boolean> values) {
            addCriterion("is_ymd not in", values, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdBetween(Boolean value1, Boolean value2) {
            addCriterion("is_ymd between", value1, value2, "isYmd");
            return (Criteria) this;
        }

        public Criteria andIsYmdNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_ymd not between", value1, value2, "isYmd");
            return (Criteria) this;
        }

        public Criteria andDigitWithIsNull() {
            addCriterion("digit_with is null");
            return (Criteria) this;
        }

        public Criteria andDigitWithIsNotNull() {
            addCriterion("digit_with is not null");
            return (Criteria) this;
        }

        public Criteria andDigitWithEqualTo(Integer value) {
            addCriterion("digit_with =", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithNotEqualTo(Integer value) {
            addCriterion("digit_with <>", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithGreaterThan(Integer value) {
            addCriterion("digit_with >", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithGreaterThanOrEqualTo(Integer value) {
            addCriterion("digit_with >=", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithLessThan(Integer value) {
            addCriterion("digit_with <", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithLessThanOrEqualTo(Integer value) {
            addCriterion("digit_with <=", value, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithIn(List<Integer> values) {
            addCriterion("digit_with in", values, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithNotIn(List<Integer> values) {
            addCriterion("digit_with not in", values, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithBetween(Integer value1, Integer value2) {
            addCriterion("digit_with between", value1, value2, "digitWith");
            return (Criteria) this;
        }

        public Criteria andDigitWithNotBetween(Integer value1, Integer value2) {
            addCriterion("digit_with not between", value1, value2, "digitWith");
            return (Criteria) this;
        }

        public Criteria andNextSeqIsNull() {
            addCriterion("next_seq is null");
            return (Criteria) this;
        }

        public Criteria andNextSeqIsNotNull() {
            addCriterion("next_seq is not null");
            return (Criteria) this;
        }

        public Criteria andNextSeqEqualTo(Integer value) {
            addCriterion("next_seq =", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqNotEqualTo(Integer value) {
            addCriterion("next_seq <>", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqGreaterThan(Integer value) {
            addCriterion("next_seq >", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqGreaterThanOrEqualTo(Integer value) {
            addCriterion("next_seq >=", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqLessThan(Integer value) {
            addCriterion("next_seq <", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqLessThanOrEqualTo(Integer value) {
            addCriterion("next_seq <=", value, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqIn(List<Integer> values) {
            addCriterion("next_seq in", values, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqNotIn(List<Integer> values) {
            addCriterion("next_seq not in", values, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqBetween(Integer value1, Integer value2) {
            addCriterion("next_seq between", value1, value2, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andNextSeqNotBetween(Integer value1, Integer value2) {
            addCriterion("next_seq not between", value1, value2, "nextSeq");
            return (Criteria) this;
        }

        public Criteria andBufferStepIsNull() {
            addCriterion("buffer_step is null");
            return (Criteria) this;
        }

        public Criteria andBufferStepIsNotNull() {
            addCriterion("buffer_step is not null");
            return (Criteria) this;
        }

        public Criteria andBufferStepEqualTo(Integer value) {
            addCriterion("buffer_step =", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepNotEqualTo(Integer value) {
            addCriterion("buffer_step <>", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepGreaterThan(Integer value) {
            addCriterion("buffer_step >", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepGreaterThanOrEqualTo(Integer value) {
            addCriterion("buffer_step >=", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepLessThan(Integer value) {
            addCriterion("buffer_step <", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepLessThanOrEqualTo(Integer value) {
            addCriterion("buffer_step <=", value, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepIn(List<Integer> values) {
            addCriterion("buffer_step in", values, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepNotIn(List<Integer> values) {
            addCriterion("buffer_step not in", values, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepBetween(Integer value1, Integer value2) {
            addCriterion("buffer_step between", value1, value2, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andBufferStepNotBetween(Integer value1, Integer value2) {
            addCriterion("buffer_step not between", value1, value2, "bufferStep");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andCTimeIsNull() {
            addCriterion("c_time is null");
            return (Criteria) this;
        }

        public Criteria andCTimeIsNotNull() {
            addCriterion("c_time is not null");
            return (Criteria) this;
        }

        public Criteria andCTimeEqualTo(Date value) {
            addCriterion("c_time =", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeNotEqualTo(Date value) {
            addCriterion("c_time <>", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeGreaterThan(Date value) {
            addCriterion("c_time >", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("c_time >=", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeLessThan(Date value) {
            addCriterion("c_time <", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeLessThanOrEqualTo(Date value) {
            addCriterion("c_time <=", value, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeIn(List<Date> values) {
            addCriterion("c_time in", values, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeNotIn(List<Date> values) {
            addCriterion("c_time not in", values, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeBetween(Date value1, Date value2) {
            addCriterion("c_time between", value1, value2, "cTime");
            return (Criteria) this;
        }

        public Criteria andCTimeNotBetween(Date value1, Date value2) {
            addCriterion("c_time not between", value1, value2, "cTime");
            return (Criteria) this;
        }

        public Criteria andUTimeIsNull() {
            addCriterion("u_time is null");
            return (Criteria) this;
        }

        public Criteria andUTimeIsNotNull() {
            addCriterion("u_time is not null");
            return (Criteria) this;
        }

        public Criteria andUTimeEqualTo(Date value) {
            addCriterion("u_time =", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeNotEqualTo(Date value) {
            addCriterion("u_time <>", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeGreaterThan(Date value) {
            addCriterion("u_time >", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("u_time >=", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeLessThan(Date value) {
            addCriterion("u_time <", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeLessThanOrEqualTo(Date value) {
            addCriterion("u_time <=", value, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeIn(List<Date> values) {
            addCriterion("u_time in", values, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeNotIn(List<Date> values) {
            addCriterion("u_time not in", values, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeBetween(Date value1, Date value2) {
            addCriterion("u_time between", value1, value2, "uTime");
            return (Criteria) this;
        }

        public Criteria andUTimeNotBetween(Date value1, Date value2) {
            addCriterion("u_time not between", value1, value2, "uTime");
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