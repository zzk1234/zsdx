package com.zd.school.oa.meeting.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zd.core.annotation.FieldInfo;
import com.zd.core.model.BaseEntity;
import com.zd.core.util.DateTimeSerializer;

/**
 * 
 * ClassName: OaMeetingcheckrule 
 * Function: TODO ADD FUNCTION. 
 * Reason: TODO ADD REASON(可选). 
 * Description: 会议考勤规则(OA_T_MEETINGCHECKRULE)实体类.
 * date: 2017-03-07
 *
 * @author  luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
 
@Entity
@Table(name = "OA_T_MEETINGCHECKRULE")
@AttributeOverride(name = "uuid", column = @Column(name = "RULE_ID", length = 36, nullable = false))
public class OaMeetingcheckrule extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @FieldInfo(name = "规则名称")
    @Column(name = "RULE_NAME", length = 36, nullable = false)
    private String ruleName;
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public String getRuleName() {
        return ruleName;
    }
        
    @FieldInfo(name = "签到提前分钟")
    @Column(name = "IN_BEFORE", length = 5, nullable = false)
    private Short inBefore;
    public void setInBefore(Short inBefore) {
        this.inBefore = inBefore;
    }
    public Short getInBefore() {
        return inBefore;
    }
        
    @FieldInfo(name = "迟到分钟")
    @Column(name = "BE_LATE", length = 5, nullable = false)
    private Short beLate;
    public void setBeLate(Short beLate) {
        this.beLate = beLate;
    }
    public Short getBeLate() {
        return beLate;
    }
        
    @FieldInfo(name = "缺勤分钟")
    @Column(name = "ABSENTEEISM", length = 5, nullable = false)
    private Short absenteeism;
    public void setAbsenteeism(Short absenteeism) {
        this.absenteeism = absenteeism;
    }
    public Short getAbsenteeism() {
        return absenteeism;
    }
        
    @FieldInfo(name = "是否需要签退 0-不需要 1-需要")
    @Column(name = "NEED_CHECKOUT", length = 5, nullable = false)
    private Short needCheckout;
    public void setNeedCheckout(Short needCheckout) {
        this.needCheckout = needCheckout;
    }
    public Short getNeedCheckout() {
        return needCheckout;
    }
        
    @FieldInfo(name = "签退提前分钟")
    @Column(name = "OUT_BEFORE", length = 5, nullable = true)
    private Short outBefore;
    public void setOutBefore(Short outBefore) {
        this.outBefore = outBefore;
    }
    public Short getOutBefore() {
        return outBefore;
    }
        
    @FieldInfo(name = "早退分钟")
    @Column(name = "LEAVE_EARLY", length = 5, nullable = true)
    private Short leaveEarly;
    public void setLeaveEarly(Short leaveEarly) {
        this.leaveEarly = leaveEarly;
    }
    public Short getLeaveEarly() {
        return leaveEarly;
    }
        
    @FieldInfo(name = "签退延迟分钟")
    @Column(name = "OUT_LATE", length = 5, nullable = true)
    private Short outLate;
    public void setOutLate(Short outLate) {
        this.outLate = outLate;
    }
    public Short getOutLate() {
        return outLate;
    }
        
    @FieldInfo(name = "规则说明")
    @Column(name = "RULE_DESC", length = 255, nullable = true)
    private String ruleDesc;
    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }
    public String getRuleDesc() {
        return ruleDesc;
    }
        
    @FieldInfo(name = "启用标识 0-不启用 1-启用")
    @Column(name = "START_USING", length = 5, nullable = false)
    private Short startUsing;
    public void setStartUsing(Short startUsing) {
        this.startUsing = startUsing;
    }
    public Short getStartUsing() {
        return startUsing;
    }
        

    /** 以下为不需要持久化到数据库中的字段,根据项目的需要手工增加 
    *@Transient
    *@FieldInfo(name = "")
    *private String field1;
    */
}