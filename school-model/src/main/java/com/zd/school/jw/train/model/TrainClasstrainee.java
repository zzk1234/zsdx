package com.zd.school.jw.train.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zd.core.annotation.FieldInfo;
import com.zd.core.model.BaseEntity;
import com.zd.core.util.DateSerializer;
import org.hibernate.annotations.Formula;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ClassName: TrainClasstrainee
 * Function: TODO ADD FUNCTION.
 * Reason: TODO ADD REASON(可选).
 * Description: 班级学员信息(TRAIN_T_CLASSTRAINEE)实体类.
 * date: 2017-03-07
 *
 * @author luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */

@Entity
@Table(name = "TRAIN_T_CLASSTRAINEE")
@AttributeOverride(name = "uuid", column = @Column(name = "CLASS_TRAINEE_ID", length = 36, nullable = false))
public class TrainClasstrainee extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @FieldInfo(name = "班级ID")
    @Column(name = "CLASS_ID", length = 36, nullable = false)
    private String classId;

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    @FieldInfo(name = "学员ID")
    @Column(name = "TRAINEE_ID", length = 36, nullable = true)
    private String traineeId;

    public void setTraineeId(String traineeId) {
        this.traineeId = traineeId;
    }

    public String getTraineeId() {
        return traineeId;
    }

    @FieldInfo(name = "姓名")
    @Column(name = "XM", length = 64, nullable = true)
    private String xm;

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getXm() {
        return xm;
    }

    @FieldInfo(name = "性别 XBM字典")
    @Column(name = "XBM", length = 1, nullable = true)
    private String xbm;

    public void setXbm(String xbm) {
        this.xbm = xbm;
    }

    public String getXbm() {
        return xbm;
    }

    @FieldInfo(name = "移动电话")
    @Column(name = "MOBILE_PHONE", length = 36, nullable = true)
    private String mobilePhone;

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    @FieldInfo(name = "身份证件号")
    @Column(name = "SFZJH", length = 20, nullable = true)
    private String sfzjh;

    public void setSfzjh(String sfzjh) {
        this.sfzjh = sfzjh;
    }

    public String getSfzjh() {
        return sfzjh;
    }

    @FieldInfo(name = "所在单位")
    @Column(name = "WORK_UNIT", length = 128, nullable = true)
    private String workUnit;

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getWorkUnit() {
        return workUnit;
    }


    @FieldInfo(name = "职务")
    @Column(name = "POSITION", length = 128, nullable = true)
    private String position;

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    @FieldInfo(name = "行政级别 HEADSHIPLEVEL字典")
    @Column(name = "HEADSHIP_LEVEL", length = 16, nullable = true)
    private String headshipLevel;

    public void setHeadshipLevel(String headshipLevel) {
        this.headshipLevel = headshipLevel;
    }

    public String getHeadshipLevel() {
        return headshipLevel;
    }


    @FieldInfo(name = "应考勤次数")
    @Column(name = "ATTENT_COUNT", length = 5, nullable = true)
    private Short attentCount;

    public void setAttentCount(Short attentCount) {
        this.attentCount = attentCount;
    }

    public Short getAttentCount() {
        return attentCount;
    }

    @FieldInfo(name = "迟到次数")
    @Column(name = "LATE_COUNT", length = 5, nullable = true)
    private Short lateCount;

    public void setLateCount(Short lateCount) {
        this.lateCount = lateCount;
    }

    public Short getLateCount() {
        return lateCount;
    }

    @FieldInfo(name = "早退次数")
    @Column(name = "EARLY_COUNT", length = 5, nullable = true)
    private Short earlyCount;

    public void setEarlyCount(Short earlyCount) {
        this.earlyCount = earlyCount;
    }

    public Short getEarlyCount() {
        return earlyCount;
    }

    @FieldInfo(name = "旷课次数")
    @Column(name = "ABSENCE_COUNT", length = 5, nullable = true)
    private Short absenceCount;

    public void setAbsenceCount(Short absenceCount) {
        this.absenceCount = absenceCount;
    }

    public Short getAbsenceCount() {
        return absenceCount;
    }

    @FieldInfo(name = "应得学分")
    @Column(name = "TOTAL_CREDIT", length = 8, nullable = true)
    private BigDecimal totalCredit;

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    @FieldInfo(name = "迟到扣除")
    @Column(name = "LATE_CREDIT", length = 8, nullable = true)
    private BigDecimal lateCredit;

    public void setLateCredit(BigDecimal lateCredit) {
        this.lateCredit = lateCredit;
    }

    public BigDecimal getLateCredit() {
        return lateCredit;
    }

    @FieldInfo(name = "早退扣除")
    @Column(name = "EARLAY_CREDIT", length = 8, nullable = true)
    private BigDecimal earlayCredit;

    public void setEarlayCredit(BigDecimal earlayCredit) {
        this.earlayCredit = earlayCredit;
    }

    public BigDecimal getEarlayCredit() {
        return earlayCredit;
    }

    @FieldInfo(name = "旷课扣除")
    @Column(name = "ABSENCE_CREDIT", length = 8, nullable = true)
    private BigDecimal absenceCredit;

    public void setAbsenceCredit(BigDecimal absenceCredit) {
        this.absenceCredit = absenceCredit;
    }

    public BigDecimal getAbsenceCredit() {
        return absenceCredit;
    }

    @FieldInfo(name = "实际学分")
    @Column(name = "REAL＿CREDIT", length = 8, nullable = true)
    private BigDecimal realRredit;

    public BigDecimal getRealRredit() {
        return realRredit;
    }

    public void setRealRredit(BigDecimal realRredit) {
        this.realRredit = realRredit;
    }

    @FieldInfo(name = "是否早餐")
    @Column(name = "BREAKFAST", nullable = true)
    private Integer breakfast;

    public Integer getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }

    @FieldInfo(name = "是否午餐")
    @Column(name = "LUNCH", nullable = true)
    private Integer lunch = 1;    //默认为1

    public Integer getLunch() {
        return lunch;
    }

    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }

    @FieldInfo(name = "是否晚餐")
    @Column(name = "DINNER", nullable = true)
    private Integer dinner;

    public Integer getDinner() {
        return dinner;
    }

    public void setDinner(Integer dinner) {
        this.dinner = dinner;
    }

    @FieldInfo(name = "是否午休")
    @Column(name = "SIESTA", nullable = true)
    private Integer siesta = 1;    //默认为1

    public Integer getSiesta() {
        return siesta;
    }

    public void setSiesta(Integer siesta) {
        this.siesta = siesta;
    }

    @FieldInfo(name = "是否晚宿")
    @Column(name = "SLEEP", nullable = true)
    private Integer sleep;

    public Integer getSleep() {
        return sleep;
    }

    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }


    @FieldInfo(name = "房间ID")
    @Column(name = "ROOM_ID", length = 36, nullable = true)
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @FieldInfo(name = "房间名称")
    @Column(name = "ROOM_NAME", length = 32, nullable = true)
    private String roomName;

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }


    public TrainClasstrainee() {
        super();
        // TODO Auto-generated constructor stub
    }


    /**
     * 以下为不需要持久化到数据库中的字段,根据项目的需要手工增加
     *
     * @Transient
     * @FieldInfo(name = "")
     * private String field1;
     */
    @FieldInfo(name = "班级名称")
    @Formula("(SELECT a.CLASS_NAME FROM TRAIN_T_CLASS a where a.CLASS_ID=CLASS_ID)")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @FieldInfo(name = "班级类型")
    @Formula("(SELECT a.ITEM_NAME FROM BASE_T_DICITEM a WHERE a.DIC_ID=(SELECT b.DIC_ID FROM BASE_T_DIC b WHERE b.DIC_CODE='ZXXBJLX') AND a.ITEM_CODE=(SELECT c.CLASS_CATEGORY FROM TRAIN_T_CLASS c WHERE c.CLASS_ID=CLASS_ID))")
    private String classCategory;

    public String getClassCategory() {
        return classCategory;
    }

    public void setClassCategory(String classCategory) {
        this.classCategory = classCategory;
    }

    @FieldInfo(name = "班主任")
    @Formula("(SELECT a.BZR_NAME FROM TRAIN_T_CLASS a where a.CLASS_ID=CLASS_ID)")
    private String bzrName;

    public String getBzrName() {
        return bzrName;
    }

    public void setBzrName(String bzrName) {
        this.bzrName = bzrName;
    }

    @FieldInfo(name = "开始日期")
    @Formula("(SELECT a.BEGIN_DATE FROM TRAIN_T_CLASS a where a.CLASS_ID=CLASS_ID)")
    @JsonSerialize(using=DateSerializer.class)
    private Date beginDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @FieldInfo(name = "结束日期")
    @Formula("(SELECT a.END_DATE FROM TRAIN_T_CLASS a where a.CLASS_ID=CLASS_ID)")
    @JsonSerialize(using=DateSerializer.class)
    private Date endDate;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TrainClasstrainee(String uuid) {
        super(uuid);
        // TODO Auto-generated constructor stub
    }

    public TrainClasstrainee(String uuid, String xm, String xbm, Integer breakfast, Integer lunch, Integer dinner,Integer isDelete) {
        super(uuid);
        this.xm = xm;
        this.xbm = xbm;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;     
        this.setIsDelete(isDelete);
    }

    public TrainClasstrainee(String uuid, String xm, String xbm, Integer siesta, Integer sleep, String roomId, String roomName,Integer isDelete) {
        super(uuid);       
        this.xm = xm;
        this.xbm = xbm;
        this.siesta = siesta;
        this.sleep = sleep;
        this.roomId = roomId;
        this.roomName = roomName;
        this.setIsDelete(isDelete);
    }

}