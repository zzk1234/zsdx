package com.zd.school.jw.train.service.Impl;

import com.zd.core.model.extjs.QueryResult;
import com.zd.core.service.BaseServiceImpl;
import com.zd.core.util.BeanUtils;
import com.zd.core.util.DateUtil;
import com.zd.core.util.JsonBuilder;
import com.zd.core.util.StringUtils;
import com.zd.school.jw.model.app.ClassEvalApp;
import com.zd.school.jw.train.dao.TrainClassDao;
import com.zd.school.jw.train.model.*;
import com.zd.school.jw.train.model.vo.TrainClassEval;
import com.zd.school.jw.train.service.*;
import com.zd.school.plartform.baseset.model.BaseOrgToUP;
import com.zd.school.plartform.baseset.service.BaseOrgService;
import com.zd.school.plartform.system.model.SysUser;
import com.zd.school.plartform.system.model.SysUserToUP;
import com.zd.school.plartform.system.service.SysUserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * ClassName: TrainClassServiceImpl Function: ADD FUNCTION. Reason: ADD
 * REASON(可选). Description: 培训开班信息(TRAIN_T_CLASS)实体Service接口实现类. date:
 * 2017-03-07
 *
 * @author luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
@Service
@Transactional
public class TrainClassServiceImpl extends BaseServiceImpl<TrainClass> implements TrainClassService {

	@Resource
	public void setTrainClassDao(TrainClassDao dao) {
		this.dao = dao;
	}

	@Resource
	private TrainTeacherService teacherService;

	@Resource
	private TrainClasstraineeService trainClasstraineeService;

	@Resource
	private TrainClassscheduleService trainClassscheduleService;

	@Resource
	BaseOrgService baseOrgService;

	@Resource
	SysUserService sysUserService;

	@Resource
	private TrainIndicatorStandService standService;

	private static Logger logger = Logger.getLogger(TrainClassServiceImpl.class);

	@Override
	public QueryResult<TrainClass> list(Integer start, Integer limit, String sort, String filter, Boolean isDelete) {
		QueryResult<TrainClass> qResult = this.doPaginationQuery(start, limit, sort, filter, isDelete);
		return qResult;
	}

	/**
	 * 根据主键逻辑删除数据
	 * 
	 * @param ids
	 *            要删除数据的主键
	 * @param currentUser
	 *            当前操作的用户
	 * @return 操作成功返回true，否则返回false
	 */
	@Override
	public Boolean doLogicDeleteByIds(String ids, SysUser currentUser) {
		Boolean delResult = false;
		try {
			Object[] conditionValue = ids.split(",");
			String[] propertyName = { "isDelete", "updateUser", "updateTime" };
			Object[] propertyValue = { 1, currentUser.getXm(), new Date() };
			this.updateByProperties("uuid", conditionValue, propertyName, propertyValue);
			delResult = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			delResult = false;
		}
		return delResult;
	}

	/**
	 * 根据传入的实体对象更新数据库中相应的数据
	 * 
	 * @param entity
	 *            传入的要更新的实体对象
	 * @param currentUser
	 *            当前操作用户
	 * @return
	 */
	@Override
	public TrainClass doUpdateEntity(TrainClass entity, SysUser currentUser) {
		// 先拿到已持久化的实体
		TrainClass saveEntity = this.get(entity.getUuid());
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			List<String> excludedProp = new ArrayList<>();
			excludedProp.add("dinnerType");
			excludedProp.add("avgNumber");
			excludedProp.add("breakfastStand");
			excludedProp.add("breakfastCount");
			excludedProp.add("lunchStand");
			excludedProp.add("lunchCount");
			excludedProp.add("dinnerStand");
			excludedProp.add("dinnerCount");
			excludedProp.add("isuse");
			excludedProp.add("isarrange");
			excludedProp.add("isEval");
			excludedProp.add("traineeCount");
			excludedProp.add("evalCount");
			BeanUtils.copyPropertiesExceptNull(saveEntity, entity, excludedProp);
			saveEntity.setUpdateTime(new Date()); // 设置修改时间
			saveEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名

			Integer isuse = saveEntity.getIsuse();
			if (isuse != null && isuse != 0) // 当班级已经提交过一次之后，每次修改都设置为2
				saveEntity.setIsuse(2);

			// 设置班级编号(问题很严峻）
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(saveEntity.getBeginDate());
			// 1.生成年
			int classYear = calendar.get(calendar.YEAR); // 2017
			// 2.拼接：年+类型
			String classNumbStr = classYear + saveEntity.getClassCategory();

			// 年份、类型变化后，编号也变化
			if (saveEntity.getClassNumb() == null || saveEntity.getClassNumb().length() < classNumbStr.length()) {
				String codeAndOrder = this.getClassCodeAndOrderIndex(classNumbStr);
				String[] temp = codeAndOrder.split(",");
				saveEntity.setClassNumb(temp[0]);
				saveEntity.setOrderIndex(Integer.valueOf(temp[1]));
			} else {
				String oldClassNumbStr = saveEntity.getClassNumb().substring(0, classNumbStr.length());
				if (!oldClassNumbStr.equals(classNumbStr)) {
					String codeAndOrder = this.getClassCodeAndOrderIndex(classNumbStr);
					String[] temp = codeAndOrder.split(",");
					saveEntity.setClassNumb(temp[0]);
					saveEntity.setOrderIndex(Integer.valueOf(temp[1]));
				}
			}

			entity = this.merge(saveEntity);// 执行修改方法

			return entity;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 将传入的实体对象持久化到数据
	 * 
	 * @param entity
	 *            传入的要更新的实体对象
	 * @param currentUser
	 *            当前操作用户
	 * @return
	 */
	@Override
	public TrainClass doAddEntity(TrainClass entity, SysUser currentUser) {
		TrainClass saveEntity = new TrainClass();
		try {
			List<String> excludedProp = new ArrayList<>();
			excludedProp.add("uuid");
			BeanUtils.copyPropertiesExceptNull(saveEntity, entity, excludedProp);
			// saveEntity.setCreateUser(currentUser.getXm()); // 设置修改人的中文名
			saveEntity.setCreateUser(currentUser.getUuid());

			// 设置班级编号
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(saveEntity.getBeginDate());
			// 1.生成年
			int classYear = calendar.get(calendar.YEAR); // 2017
			// 2.拼接：年+类型
			String classNumbStr = classYear + saveEntity.getClassCategory();

			String codeAndOrder = this.getClassCodeAndOrderIndex(classNumbStr);
			String[] temp = codeAndOrder.split(",");
			saveEntity.setClassNumb(temp[0]);
			saveEntity.setOrderIndex(Integer.valueOf(temp[1]));

			entity = this.merge(saveEntity);// 执行修改方法

			// if(traineeId != null ){
			// String[] traineeIds = traineeId.split(",");
			// String[] traineeXbms = traineeXbm.split(",");
			// String[] traineeNames = traineeName.split(",");
			// String[] traineePhones = traineePhone.split(",");
			// String[] traineeSfzjhs = traineeSfzjh.split(",");
			// for (int i = 0; i < traineeIds.length; i++) {
			// if (!traineeIds[i].trim().equals("")) {
			// TrainClasstrainee tct = new TrainClasstrainee();
			// tct.setClassId(entity.getUuid());
			// tct.setTraineeId(traineeIds[i]);
			// tct.setXm(traineeNames[i]);
			// tct.setXbm(traineeXbms[i]);
			// tct.setMobilePhone(traineePhones[i]);
			// tct.setSfzjh(traineeSfzjhs[i]);
			// trainClasstraineeService.merge(tct);
			// }
			// }
			// }

			return entity;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public HashMap<String, String> getClassInfo(String id) {
		// TODO Auto-generated method stub
		HashMap<String, String> returnDate = new HashMap<>();

		StringBuffer classTraineeIds = new StringBuffer();
		StringBuffer traineeIds = new StringBuffer();
		StringBuffer traineeNames = new StringBuffer();
		StringBuffer traineeXbms = new StringBuffer();
		StringBuffer traineeSfzjhs = new StringBuffer();
		StringBuffer traineeMobilePhones = new StringBuffer();
		StringBuffer courseIds = new StringBuffer();
		StringBuffer courseNames = new StringBuffer();

		// 班级学员信息
		List<TrainClasstrainee> trainees = trainClasstraineeService
				.queryByProerties(new String[] { "classId", "isDelete" }, new Object[] { id, 0 });
		for (TrainClasstrainee trainee : trainees) {
			classTraineeIds.append(trainee.getUuid() + ",");
			traineeIds.append(trainee.getTraineeId() + ",");
			traineeNames.append(trainee.getXm() + ",");
			traineeXbms.append(trainee.getXbm() + ",");
			traineeSfzjhs.append(trainee.getSfzjh() + ",");
			traineeMobilePhones.append(trainee.getMobilePhone() + ",");
		}
		if (traineeIds.length() > 0) {
			classTraineeIds.deleteCharAt(classTraineeIds.length() - 1);
			traineeIds.deleteCharAt(traineeIds.length() - 1);
			traineeNames.deleteCharAt(traineeNames.length() - 1);
			traineeXbms.deleteCharAt(traineeXbms.length() - 1);
			traineeSfzjhs.deleteCharAt(traineeSfzjhs.length() - 1);
			traineeMobilePhones.deleteCharAt(traineeMobilePhones.length() - 1);
		}

		// 班级课程信息
		List<TrainClassschedule> courses = trainClassscheduleService
				.queryByProerties(new String[] { "classId", "isDelete" }, new Object[] { id, 0 });
		for (TrainClassschedule course : courses) {
			courseIds.append(course.getUuid() + ",");
			courseNames.append(course.getCourseName() + ",");
		}
		if (courseIds.length() > 0) {
			courseIds.deleteCharAt(courseIds.length() - 1);
			courseNames.deleteCharAt(courseNames.length() - 1);
		}

		returnDate.put("classTraineeId", classTraineeIds.toString());
		returnDate.put("traineeId", traineeIds.toString());
		returnDate.put("traineeName", traineeNames.toString());
		returnDate.put("traineeXbm", traineeXbms.toString());
		returnDate.put("traineeSfzjh", traineeSfzjhs.toString());
		returnDate.put("traineeMobilePhone", traineeMobilePhones.toString());
		returnDate.put("courseId", courseIds.toString());
		returnDate.put("courseName", courseNames.toString());

		return returnDate;
	}

	@Override
	public int doEditClassFood(TrainClass entity, String classFoodInfo, SysUser currentUser) {
		int result = 0;

		try {
			// 先拿到已持久化的实体
			TrainClass saveEntity = this.get(entity.getUuid());

			saveEntity.setDinnerType(entity.getDinnerType());
			saveEntity.setAvgNumber(entity.getAvgNumber());
			saveEntity.setBreakfastStand(entity.getBreakfastStand());
			saveEntity.setBreakfastCount(entity.getBreakfastCount());
			saveEntity.setLunchStand(entity.getLunchStand());
			saveEntity.setLunchCount(entity.getLunchCount());
			saveEntity.setDinnerStand(entity.getDinnerStand());
			saveEntity.setDinnerCount(entity.getDinnerCount());
			// BeanUtils.copyPropertiesExceptNull(saveEntity, entity);
			// //entity会出现默认值，导致赋值不正确

			Integer isuse = saveEntity.getIsuse();
			if (isuse != null && isuse != 0) // 当班级已经提交过一次之后，每次修改都设置为2
				saveEntity.setIsuse(2);

			saveEntity.setUpdateTime(new Date()); // 设置修改时间
			saveEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
			this.update(saveEntity);

			if (saveEntity.getDinnerType() == 3) {
				// 把班级学员的数据，还原为json对象
				@SuppressWarnings("unchecked")
				List<TrainClasstrainee> classTraineeFoodInfos = (List<TrainClasstrainee>) JsonBuilder.getInstance()
						.fromJsonArray(classFoodInfo, TrainClasstrainee.class);

				for (TrainClasstrainee stp : classTraineeFoodInfos) {
					// 先拿到已持久化的实体
					TrainClasstrainee saveTraineeEntity = trainClasstraineeService.get(stp.getUuid());

					BeanUtils.copyPropertiesExceptNull(saveTraineeEntity, stp);
					saveTraineeEntity.setUpdateTime(new Date()); // 设置修改时间
					saveTraineeEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
					trainClasstraineeService.update(saveTraineeEntity);// 执行修改方法

				}
			} else {
				// 清除班级学员的早 中 晚餐的数据
				String hql = "update TrainClasstrainee set breakfast=0,lunch=0,dinner=0 where classId='"
						+ saveEntity.getUuid() + "'";
				this.executeHql(hql);
			}
			result = 1;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 不应该捕获unchecked异常，否则不会自动回滚； 若要捕获只能手动回滚 或 抛出运行时异常 catch (Exception e) {
		 * // TODO: handle exception logger.error(e.getMessage()); result = 0; }
		 */

		return result;
	}

	@Override
	public int doEditClassRoom(TrainClass entity, String classRoomInfo, SysUser currentUser) {
		int result = 0;

		try {
			// 先拿到已持久化的实体
			TrainClass saveEntity = this.get(entity.getUuid());
			Integer isuse = saveEntity.getIsuse();
			if (isuse != null && isuse != 0) // 当班级已经提交过一次之后，每次修改都设置为2
				saveEntity.setIsuse(2);

			saveEntity.setUpdateTime(new Date()); // 设置修改时间
			saveEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
			this.update(saveEntity);

			@SuppressWarnings("unchecked")
			List<TrainClasstrainee> classTraineeRoomInfos = (List<TrainClasstrainee>) JsonBuilder.getInstance()
					.fromJsonArray(classRoomInfo, TrainClasstrainee.class);

			for (TrainClasstrainee stp : classTraineeRoomInfos) {
				// 先拿到已持久化的实体
				TrainClasstrainee saveTraineeEntity = trainClasstraineeService.get(stp.getUuid());

				BeanUtils.copyPropertiesExceptNull(saveTraineeEntity, stp);
				saveTraineeEntity.setUpdateTime(new Date()); // 设置修改时间
				saveTraineeEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
				trainClasstraineeService.update(saveTraineeEntity);// 执行修改方法
			}

			result = 1;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 不应该捕获unchecked异常，否则不会自动回滚； 若要捕获只能手动回滚 或 抛出运行时异常 catch (Exception e) {
		 * // TODO: handle exception logger.error(e.getMessage()); result = 0; }
		 */

		return result;
	}

	/**
	 * 同步某个班级部门下的所有学员数据 若班级部门不存在，则创建
	 */
	@Override
	public int syncTraineeClassInfoToAllUP(String departmentId, TrainClass trainClass, List<SysUserToUP> userInfos) {

		int result = 0;

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(trainClass.getBeginDate());

			// 1.生成父部门
			String firstDeptId = "Train" + calendar.get(calendar.YEAR); // Train2017
			String SecondDeptId = "Train" + calendar.get(calendar.YEAR) + "-" + (calendar.get(calendar.MONTH) + 1); // Train2017-5

			// 2.年度部门
			// 查询年度部门是否存在
			String sql = "select COUNT(1) from TC_Department where DepartmentID='" + firstDeptId + "'";
			int count = this.getForValueToSql(sql);
			if (count == 0) {
				String sql1 = "select MAX(layerorder) from TC_Department where ParentDepartmentID='001'";
				String firstLayerorder = this.getForValueToSql(sql1);
				firstLayerorder = firstLayerorder == null ? "0" : firstLayerorder;

				BaseOrgToUP firstDeptToUP = new BaseOrgToUP();
				firstDeptToUP.setDepartmentId(firstDeptId);
				firstDeptToUP.setParentDepartmentId("001");
				firstDeptToUP.setDepartmentName(calendar.get(calendar.YEAR) + "年度");
				firstDeptToUP.setLayer("1"); // 第1+1个层级
				firstDeptToUP.setLayerorder(String.valueOf(Integer.parseInt(firstLayerorder) + 1));

				baseOrgService.syncDeptInfoToUP(firstDeptToUP, firstDeptId);
			}

			// 3.月度部门
			// 查询月度部门是否存在
			sql = "select COUNT(1) from TC_Department where DepartmentID='" + SecondDeptId + "'";
			count = this.getForValueToSql(sql);
			if (count == 0) {
				String sql1 = "select MAX(layerorder) from TC_Department where ParentDepartmentID='" + firstDeptId
						+ "'";
				String secondLayerorder = this.getForValueToSql(sql1);
				secondLayerorder = secondLayerorder == null ? "0" : secondLayerorder;

				BaseOrgToUP secondDeptToUP = new BaseOrgToUP();
				secondDeptToUP.setDepartmentId(SecondDeptId);
				secondDeptToUP.setParentDepartmentId(firstDeptId);
				secondDeptToUP.setDepartmentName(
						calendar.get(calendar.YEAR) + "年" + (calendar.get(calendar.MONTH) + 1) + "月");
				secondDeptToUP.setLayer("2"); // 第2+1个层级
				secondDeptToUP.setLayerorder(String.valueOf(Integer.parseInt(secondLayerorder) + 1));

				baseOrgService.syncDeptInfoToUP(secondDeptToUP, SecondDeptId);
			}

			// 4.班级部门
			// 查询班级部门是否存在
			sql = "select COUNT(1) from TC_Department where DepartmentID='" + departmentId + "'";
			count = this.getForValueToSql(sql);
			// if (count == 0) {
			// 现在每次同步，都要更新一下班级信息【当班级信息被修改时也要进行同步】
			String sql1 = "select MAX(layerorder) from TC_Department where ParentDepartmentID='" + SecondDeptId + "'";
			String classLayerorder = this.getForValueToSql(sql1);
			classLayerorder = classLayerorder == null ? "0" : classLayerorder;

			BaseOrgToUP classDeptToUP = new BaseOrgToUP();
			classDeptToUP.setDepartmentId(departmentId);
			classDeptToUP.setParentDepartmentId(SecondDeptId);
			classDeptToUP.setDepartmentName(trainClass.getClassName());
			classDeptToUP.setLayer("3"); // 第3+1个层级
			classDeptToUP.setLayerorder(String.valueOf(Integer.parseInt(classLayerorder) + 1));

			baseOrgService.syncDeptInfoToUP(classDeptToUP, departmentId);

			// }

			// 5.同步班级学员
			result = sysUserService.syncUserInfoToAllUP(userInfos);

		} catch (Exception e) {
			// TODO: handle exception
			// 捕获了异常后，要手动进行回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			result = -1;
		}

		return result;
	}

	@Override
	public List<TrainTeacher> getClassBzrList(String classId) {
		TrainClass classinfo = this.get(classId);
		Object[] propValue = classinfo.getBzrId().split(",");
		List<TrainTeacher> list = teacherService.queryByProerties("uuid", propValue);

		return list;
	}

	private String getClassCodeAndOrderIndex(String classNumbStr) {
		Integer orderIndex = Integer.valueOf(1);
		String courseCode = "";

		// 得到排序号
		String hql = "select max(orderIndex) from TrainClass where classNumb like '" + classNumbStr + "%' ";
		// if (!uuid.equals("-1"))
		// hql += " and uuid!='" + uuid + "'";
		Object index = this.getForValue(hql);
		if (index != null) {
			orderIndex = (Integer) index + 1;
		} else
			orderIndex = 1;

		// 生成编号
		courseCode = classNumbStr + StringUtils.addString(orderIndex.toString(), "0", 3, "L");

		return courseCode + "," + orderIndex.toString();
	}

	@Override
	public ClassEvalApp getClassEvalStand(String classId) {

		ClassEvalApp entity = new ClassEvalApp();

		try {
			String sql = "SELECT  classId,classCategory,className,beginDate,endDate,trainDays,holdUnit,undertaker,trainees,convert(varchar(10),verySatisfaction) as verySatisfaction"
					+ ",convert(varchar(10),satisfaction) as satisfaction,bzr,mobile FROM TRAIN_V_CLASSEVAL where classId=''{0}''";
			sql = MessageFormat.format(sql, classId);
			List<TrainClassEval> list = this.doQuerySqlObject(sql, TrainClassEval.class);
			List<TrainIndicatorStand> stand = standService.getClassEvalStand();
			entity.setEvalClass(list.get(0));
			entity.setEvalStand(stand);
			entity.setMessage("获取班级评价标准成功");
			entity.setSuccess(true);

			return entity;
		} catch (Exception e) {
			entity.setEvalClass(null);
			entity.setEvalStand(null);
			entity.setMessage(MessageFormat.format("获取班级评价标准失败，失败原因:{0}", e.getMessage()));
			entity.setSuccess(false);

			return entity;
		}
	}

	@Override
	public int syncClassTraineeFoodsToUP(TrainClass trainClass, List<TrainClasstrainee> traineeFoods) {
		int result = 0;

		try {
			String beginDate = DateUtil.formatDate(trainClass.getBeginDate());
			String endDate = DateUtil.formatDate(trainClass.getEndDate());
			String currentDate = DateUtil.formatDateTime(new Date());

			// 1.创建就餐类型（早餐类、午餐类、晚餐类、早午餐类、早晚餐类、午晚餐类、早午晚餐类）
			String[] MealIds = createMealType(trainClass);

			// 2.遍历学员，查询学员

			Integer breakFast = 0, lunch = 0, dinner = 0;
			String MealId = null;
			
			StringBuffer executeSb = new StringBuffer();
			
			String insertSql = "insert into XF_MealCouponSet(MealCouponTypeID,EmployeeID,StartDate,EndDate,CreateDate,MealId)"
					+ "values(%d,%s,'%s','%s','%s','%s');";
	
			String updateSql = "update XF_MealCouponSet MealCouponTypeID=0,MealId='%s' where EmployeeID='%s';";
			
			String deleteSql = "delete from XF_MealCouponDetail where EmployeeID='%s' ; delete from XF_MealCouponSet where EmployeeID='%s'";

			for (int i = 0; i < traineeFoods.size(); i++) {
				TrainClasstrainee trainee = traineeFoods.get(i);
				// 判断学员就餐的类型
				breakFast = trainee.getBreakfast();
				breakFast = breakFast == null ? 0 : breakFast;
				lunch = trainee.getLunch();
				lunch = lunch == null ? 0 : lunch;
				dinner = trainee.getDinner();
				dinner = dinner == null ? 0 : dinner;
				MealId = null;

				// 查询该学员的餐券信息
				String selectSql = "select top 1 EmployeeID,MealId from XF_MealCouponSet where EmployeeID="
						+ "(select top 1 EmployeeID from TC_Employee where UserId='" + trainee.getUuid() + "')";
				List<Map<String, Object>> MealInfo = this.getForValuesToSql(selectSql);

				// 判断学员的状态
				if (trainee.getIsDelete() == 1) {
					if (MealInfo.size() > 0) {
						// 删除学员的餐券
						executeSb.append(String.format(deleteSql, MealInfo.get(0).get("EmployeeID"),
								MealInfo.get(0).get("EmployeeID")));
					}

				} else {

					if (breakFast == 1 && lunch == 1 && dinner == 1) { // 早午晚餐类
						MealId = MealIds[6];
					} else if (lunch == 1 && dinner == 1) { // 午晚餐类
						MealId = MealIds[5];
					} else if (breakFast == 1 && dinner == 1) { // 早晚餐类
						MealId = MealIds[4];
					} else if (breakFast == 1 && lunch == 1) { // 早午餐类
						MealId = MealIds[3];
					} else if (dinner == 1) { // 晚餐类
						MealId = MealIds[2];
					} else if (lunch == 1) { // 午餐类
						MealId = MealIds[1];
					} else if (breakFast == 1) { // 早餐类
						MealId = MealIds[0];
					}

					if (MealId != null) { // 有订餐
						if (MealInfo.size() > 0) {
							if (!MealInfo.get(0).get("MealId").equals(MealId)) { // 若前后的订餐不一致，则更新
								executeSb.append(String.format(updateSql, MealId , MealInfo.get(0).get("EmployeeID")));
							}
						} else {
							executeSb.append(String.format(insertSql, 0,
									"(select top 1 EmployeeID from TC_Employee where UserId='"
											+ traineeFoods.get(i).getUuid() + "')",
									beginDate, endDate, currentDate, MealId));
						}

					} else { // 没有订餐
						if (MealInfo.size() > 0) {
							// 删除学员的餐券
							executeSb.append(String.format(deleteSql, MealInfo.get(0).get("EmployeeID"),
									MealInfo.get(0).get("EmployeeID")));
						}
					}
				}

				// 每20条语句执行一次插入
				if ((i + 1) % 20 == 0) {				
					result += this.executeSql(executeSb.toString());
					executeSb.setLength(0); // 清空
				}

			}

			// 最后执行一次
			if (executeSb.length() > 0)
				result += this.executeSql(executeSb.toString());
		
			// 提交数据
			// TransactionAspectSupport.currentTransactionStatus().flush();
			// 更新餐券类型（若直接在上边的语句中直接使用子查询去查询类型ID，会报错，因为事物原因，类型并未提交入库，所以查询不到）
			// 继而，在插入了学员就餐数据之后，使用下面这个语句进行一次性的更新就餐id。
			updateSql = "update XF_MealCouponSet set MealCouponTypeID =" + " isnull((select top 1 MealCouponTypeID from"
					+ "		XF_MealCouponType where MealId=XF_MealCouponSet.MealId),0) "
					+ " where MealCouponTypeID=0 and MealId is not null";
			this.executeSql(updateSql);
		} catch (Exception e) {
			result = -1;
			// TODO: handle exception
			// 捕获了异常后，要手动进行回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// 或者抛出运行时异常
			// throw new RuntimeException();
		}

		return result;
	}

	/**
	 * 创建UP库的班级就餐类型（7种） 注：此代码只会生成一次，即安排之后，班级基础信息不能修改
	 * 
	 * @param trainClass
	 * @return 返回存储了就餐类型MealId
	 */
	private String[] createMealType(TrainClass trainClass) {

		String beginDate = DateUtil.formatDateTime(trainClass.getBeginDate());
		String endDate = DateUtil.formatDateTime(trainClass.getEndDate());
		String currentDate = DateUtil.formatDateTime(new Date());
		String className = trainClass.getClassName();
		String classId = trainClass.getUuid();
		BigDecimal breakFastStand = trainClass.getBreakfastStand();
		BigDecimal lunchStand = trainClass.getLunchStand();
		BigDecimal dinnerStand = trainClass.getDinnerStand();

		String uuid1 = null; // 早餐类
		String uuid2 = null; // 午餐类
		String uuid3 = null; // 晚餐类
		String uuid4 = null; // 早午餐类
		String uuid5 = null; // 早晚餐类
		String uuid6 = null; // 午晚餐类
		String uuid7 = null; // 早午晚餐类

		// 先查询up库中是否存在此班级的相关餐券类型
		String selectSql = "select MealId,MealTypeID1,MealTypeID2,MealTypeID3 from XF_MealCouponType where ClassId='"
				+ classId + "'";

		String insertSql = "insert into XF_MealCouponType(MealCouponName,DateType,MealTypeID1,MealAmount1,MealTypeID2,"
				+ "MealAmount2,MealTypeID3,MealAmount3,MealTypeID4,MealAmount4,Flag,MultipleUse,"
				+ "StartDate,EndDate,CreateDate,MealId,ClassId) values('%s',%d,%d,%f,%d,%f,%d,%f,%d,%f,%d,%d,'%s','%s','%s','%s','%s')";

		// String updateSql= "update XF_MealCouponType set
		// MealCouponName='%s',MealAmount1=%d,MealAmount2=%d,MealAmount3=%d
		// where MealId='%s'";

		List<Map<String, Object>> lists = this.getForValuesToSql(selectSql);
		if (lists.size() > 0) {
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> map = lists.get(i);
				if (map.get("MealTypeID1").equals(true)) {
					if (map.get("MealTypeID2").equals(true) && map.get("MealTypeID3").equals(true)) { // 早午晚餐
						uuid7 = String.valueOf(map.get("MealId"));
					} else if (map.get("MealTypeID2").equals(true) && map.get("MealTypeID3").equals(false)) { // 早午餐
						uuid4 = String.valueOf(map.get("MealId"));
					} else if (map.get("MealTypeID2").equals(false) && map.get("MealTypeID3").equals(true)) { // 早晚餐
						uuid5 = String.valueOf(map.get("MealId"));
					} else { // 早餐
						uuid1 = String.valueOf(map.get("MealId"));
					}
				} else if (map.get("MealTypeID2").equals(true)) {
					if (map.get("MealTypeID3").equals(true)) { // 午晚餐
						uuid6 = String.valueOf(map.get("MealId"));
					} else { // 午餐
						uuid2 = String.valueOf(map.get("MealId"));
					}
				} else if (map.get("MealTypeID3").equals(true)) {
					uuid3 = String.valueOf(map.get("MealId"));
				}
			}

		} else {

			// 1.创建就餐类型（早餐类、午餐类、晚餐类、早午餐类、早晚餐类、午晚餐类、早午晚餐类）
			uuid1 = UUID.randomUUID().toString();
			uuid2 = UUID.randomUUID().toString();
			uuid3 = UUID.randomUUID().toString();
			uuid4 = UUID.randomUUID().toString();
			uuid5 = UUID.randomUUID().toString();
			uuid6 = UUID.randomUUID().toString();
			uuid7 = UUID.randomUUID().toString();

			StringBuffer sb = new StringBuffer();
			sb.append(String.format(insertSql, className + "-早餐类", 50102, 1, breakFastStand, 0, 0.00, 0, 0.00, 0, 0.00,
					1, 0, beginDate, endDate, currentDate, uuid1, classId));
			sb.append(String.format(insertSql, className + "-午餐类", 50102, 0, 0.00, 1, lunchStand, 0, 0.00, 0, 0.00, 1,
					0, beginDate, endDate, currentDate, uuid2, classId));
			sb.append(String.format(insertSql, className + "-晚餐类", 50102, 0, 0.00, 0, 0.00, 1, dinnerStand, 0, 0.00, 1,
					0, beginDate, endDate, currentDate, uuid3, classId));
			sb.append(String.format(insertSql, className + "-早午餐类", 50102, 1, breakFastStand, 1, lunchStand, 0, 0.00, 0,
					0.00, 1, 0, beginDate, endDate, currentDate, uuid4, classId));
			sb.append(String.format(insertSql, className + "-早晚餐类", 50102, 1, breakFastStand, 0, 0.00, 1, dinnerStand,
					0, 0.00, 1, 0, beginDate, endDate, currentDate, uuid5, classId));
			sb.append(String.format(insertSql, className + "-午晚餐类", 50102, 0, 0.00, 1, lunchStand, 1, dinnerStand, 0,
					0.00, 1, 0, beginDate, endDate, currentDate, uuid6, classId));
			sb.append(String.format(insertSql, className + "-早午晚餐类", 50102, 1, breakFastStand, 1, lunchStand, 1,
					dinnerStand, 0, 0.00, 1, 0, beginDate, endDate, currentDate, uuid7, classId));

			this.executeSql(sb.toString());

		}

		return new String[] { uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7 };
	}
}