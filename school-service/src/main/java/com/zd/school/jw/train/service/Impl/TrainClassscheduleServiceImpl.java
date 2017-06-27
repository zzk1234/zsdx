package com.zd.school.jw.train.service.Impl;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zd.core.model.ImportNotInfo;
import com.zd.core.model.extjs.QueryResult;
import com.zd.core.service.BaseServiceImpl;
import com.zd.core.util.BeanUtils;
import com.zd.school.jw.model.app.CourseEvalApp;
import com.zd.school.jw.train.dao.TrainClassscheduleDao;
import com.zd.school.jw.train.model.TrainClass;
import com.zd.school.jw.train.model.TrainClassschedule;
import com.zd.school.jw.train.model.TrainCourseinfo;
import com.zd.school.jw.train.model.TrainIndicatorStand;
import com.zd.school.jw.train.model.TrainTeacher;
import com.zd.school.jw.train.model.vo.TrainClassCourseEval;
import com.zd.school.jw.train.service.TrainClassService;
import com.zd.school.jw.train.service.TrainClassscheduleService;
import com.zd.school.jw.train.service.TrainCoursecategoryService;
import com.zd.school.jw.train.service.TrainCourseinfoService;
import com.zd.school.jw.train.service.TrainIndicatorStandService;
import com.zd.school.jw.train.service.TrainTeacherService;
import com.zd.school.plartform.baseset.model.BaseDicitem;
import com.zd.school.plartform.baseset.service.BaseDicitemService;
import com.zd.school.plartform.system.model.SysUser;

/**
 * 
 * ClassName: TrainClassscheduleServiceImpl Function: ADD FUNCTION. Reason: ADD
 * REASON(可选). Description: 班级课程日历(TRAIN_T_CLASSSCHEDULE)实体Service接口实现类. date:
 * 2017-03-07
 *
 * @author luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
@Service
@Transactional
public class TrainClassscheduleServiceImpl extends BaseServiceImpl<TrainClassschedule>
		implements TrainClassscheduleService {

	@Resource
	public void setTrainClassscheduleDao(TrainClassscheduleDao dao) {
		this.dao = dao;
	}

	@Resource
	private TrainClassService trainClassService;
	
	@Resource
	private BaseDicitemService dicitemService;

	@Resource
	private TrainTeacherService teacherService;

	@Resource
	private TrainCourseinfoService trainCourseinfoService;

	@Resource
	private TrainCoursecategoryService coursecategoryService;

	@Resource
	private TrainIndicatorStandService standService;

	private static Logger logger = Logger.getLogger(TrainClassscheduleServiceImpl.class);

	@Override
	public QueryResult<TrainClassschedule> list(Integer start, Integer limit, String sort, String filter,
			Boolean isDelete) {
		QueryResult<TrainClassschedule> qResult = this.doPaginationQuery(start, limit, sort, filter, isDelete);
		return qResult;
	}

	/**
	 * 根据主键逻辑删除数据
	 * New:删除后，更新班级的isuse值
	 * @param ids
	 *            要删除数据的主键
	 * @param currentUser
	 *            当前操作的用户
	 * @return 操作成功返回true，否则返回false
	 */
	@Override
	public Boolean doLogicDeleteByIds(String classId,String ids, SysUser currentUser) {
		Boolean delResult = false;
		try {
			Object[] conditionValue = ids.split(",");
			String[] propertyName = { "isDelete", "updateUser", "updateTime" };
			Object[] propertyValue = { 1, currentUser.getXm(), new Date() };
			this.updateByProperties("uuid", conditionValue, propertyName, propertyValue);
			delResult = true;
			
			//设置班级的状态
			TrainClass trainClass = trainClassService.get(classId);
			Integer isuse=trainClass.getIsuse();
			if(isuse!=null&&isuse!=0){	//当班级已经提交过一次之后，每次修改都设置为2
				trainClass.setIsuse(2);
				trainClass.setUpdateTime(new Date()); // 设置修改时间
				trainClass.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
				trainClassService.update(trainClass);
			}	
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
	public TrainClassschedule doUpdateEntity(TrainClassschedule entity, SysUser currentUser) {
		// 先拿到已持久化的实体
		TrainClassschedule saveEntity = this.get(entity.getUuid());
		try {
			BeanUtils.copyProperties(saveEntity, entity);
			saveEntity.setUpdateTime(new Date()); // 设置修改时间
			saveEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
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
	 * NEW:更新班级状态
	 * NEW：当班级提交之后，再此导入或添加课程，则设置isdelete状态为2
	 * @param entity
	 *            传入的要更新的实体对象
	 * @param currentUser
	 *            当前操作用户
	 * @return
	 */
	@Override
	public TrainClassschedule doAddEntity(TrainClassschedule entity, String teachType,SysUser currentUser) {
		TrainClassschedule saveEntity = new TrainClassschedule();
		try {
			List<String> excludedProp = new ArrayList<>();
			excludedProp.add("uuid");
			BeanUtils.copyProperties(saveEntity, entity, excludedProp);
			saveEntity.setCreateUser(currentUser.getXm()); // 设置修改人的中文名
			
			//设置班级的状态
			TrainClass trainClass = trainClassService.get(entity.getClassId());
			Integer isuse=trainClass.getIsuse();
			if(isuse!=null&&isuse!=0){	//当班级已经提交过一次之后，每次修改都设置为2
				saveEntity.setIsDelete(2);	
				
				trainClass.setIsuse(2);
				trainClass.setUpdateTime(new Date()); // 设置修改时间
				trainClass.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
				trainClassService.update(trainClass);
			}			
			
			// 查询课程表中是否存在 课程名、教师名一致的课程
			TrainCourseinfo trainCourseInfo = trainCourseinfoService.getByProerties(
					new String[] { "courseName", "mainTeacherId", "isDelete" },
					new Object[] { saveEntity.getCourseName(), saveEntity.getMainTeacherId(), 0 });
			

			// 如果存在，把课程id和教师id直接存放到当前班级课程中
			// 否则，创建新的课程，并查找教师id
			if (trainCourseInfo == null) {
				// 查询未分类的id
				String hql = "select uuid from TrainCoursecategory a where a.isDelete=? and a.nodeText=?";
				String categoryId = coursecategoryService.getForValue(hql, 0, "未分类");
			
				// 否则存入到课程库
				trainCourseInfo = new TrainCourseinfo();
				trainCourseInfo.setCategoryId(categoryId);
				trainCourseInfo.setTeachType(teachType);		//当课程不存在，并创建的时候，才会设置教学形式
				trainCourseInfo.setCourseName(entity.getCourseName());
				trainCourseInfo.setMainTeacherId(entity.getMainTeacherId());
				trainCourseInfo.setMainTeacherName(entity.getMainTeacherName());
			
				trainCourseinfoService.merge(trainCourseInfo);
			}
			
			saveEntity.setCourseId(trainCourseInfo.getUuid());
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

	
	@Override
	public int doUpdateRoomInfo(String roomId, String roomName, String ids, SysUser currentUser) {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {

			String hqlUpdate = "update TrainClassschedule t set t.roomId='" + roomId + "',t.scheduleAddress='"
					+ roomName + "'," + "	t.updateUser='" + currentUser.getXm() + "',t.updateTime='"
					+ sdf.format(new Date()) + "' " + "where t.isDelete=0 and t.uuid in ('" + ids.replace(",", "','")
					+ "')";
			this.executeHql(hqlUpdate);
			result = 1;

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			result = -1;
		}

		return result;
	}

	@Override
	public int doCancelRoomInfo(String ids, SysUser currentUser) {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {

			String hqlUpdate = "update TrainClassschedule t set t.roomId=NULL,t.scheduleAddress=NULL,"
					+ "	t.updateUser='" + currentUser.getXm() + "',t.updateTime='" + sdf.format(new Date()) + "' "
					+ "where t.uuid in ('" + ids.replace(",", "','") + "')";
			this.executeHql(hqlUpdate);
			result = 1;

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			result = -1;
		}

		return result;
	}
	
	/**
	 * New:导入后，更新班级的isuse值
	 * NEW：当班级提交之后，再此导入或添加课程，则设置isdelete状态为2
	 */
	@Override
	public List<ImportNotInfo> doImportData(List<List<Object>> importData, String classId, SysUser currentUser) {
		// TODO Auto-generated method stub

		List<ImportNotInfo> listNotExit = new ArrayList<>();
		SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String createUser = currentUser.getXm();

		ImportNotInfo notExits = null;
		Integer notCount = 1;

		// 所有的教学形式字典项
		Map<String, String> mapTeachType = new HashMap<>();
		String hql = " from BaseDicitem where dicCode='TEACHTYPE'";
		List<BaseDicitem> listTeachType = dicitemService.doQuery(hql);
		for (BaseDicitem baseDicitem : listTeachType) {
			mapTeachType.put(baseDicitem.getItemName(), baseDicitem.getItemCode());
		}

		// 查询未分类的id
		hql = "select uuid from TrainCoursecategory a where a.isDelete=? and a.nodeText=?";
		String categoryId = coursecategoryService.getForValue(hql, 0, "未分类");

		/**
		 * 格式 第一行为列头【日期 开始时间 结束时间 教学形式 课程名称 主讲老师 是否评价 授课模式】 第二行开始为数据 【2017年6月13日
		 * 10:00 12:00 专题讲座 学习贯彻习近平总书记对广东工作重要批示精神 刘忠友 是 否】
		 */
		//设置班级的状态
		TrainClass trainClass = trainClassService.get(classId);
		Integer isuse=trainClass.getIsuse();
		Integer isDelete=0;
		if(isuse!=null&&isuse!=0){	//当班级已经提交过一次之后，每次修改都设置为2
			isDelete=2;
			trainClass.setIsuse(2);
			trainClass.setUpdateTime(new Date()); // 设置修改时间
			trainClass.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
		}
		String doResult = "";
		String title = "";
		String errorLevel = "";
		boolean isError=false;
		String beginTime = null;
		String endTime = null;
		String teacheName = null;
		String courseName = null;
		String courseMode = null;
		TrainCourseinfo trainCourseInfo = null;
		for (int i = 0; i < importData.size(); i++) {

			try {

				List<Object> lo = importData.get(i);
				beginTime = String.valueOf(lo.get(0)) + " " + String.valueOf(lo.get(1));
				endTime = String.valueOf(lo.get(0)) + " " + String.valueOf(lo.get(2));

				courseName = String.valueOf(lo.get(4));
				teacheName = String.valueOf(lo.get(5));
				courseMode = String.valueOf(lo.get(7));
				
				title = courseName;
				doResult="导入成功";	// 默认是成功
				isError=false;
				
				TrainClassschedule tcs = new TrainClassschedule();
				tcs.setClassId(classId);
				tcs.setBeginTime(dateTimeSdf.parse(beginTime));
				tcs.setEndTime(dateTimeSdf.parse(endTime));
				tcs.setCourseName(courseName);
				tcs.setMainTeacherName(teacheName);
				tcs.setIsEval(String.valueOf(lo.get(6)).equals("是") ? 1 : 0);
				tcs.setIsDelete(isDelete);	//设置isdelete为特定的值。
				
				// 查询课程表中是否存在 课程名、教师名一致的课程
				trainCourseInfo = trainCourseinfoService.getByProerties(
						new String[] { "courseName", "mainTeacherName", "isDelete" },
						new Object[] { courseName, teacheName, 0 });
				//hql = "from TrainCourseinfo a where a.isDelete=? and a.courseName=? and a.mainTeacherName=?";
				//trainCourseInfo = trainCourseinfoService.getForValue(hql, 0, courseName, teacheName);

				// 查询是否是团队授课模式 1：单一模式 2：群组模式
				/*
				if (courseMode.equals("是")) {
					tcs.setCourseMode((short) 2);
				} else {
					tcs.setCourseMode((short) 1);
				}
				*/

				// 如果存在，把课程id和教师id直接存放到当前班级课程中
				// 否则，创建新的课程，并查找教师id
				if (trainCourseInfo == null) {
					// 否则存入到课程库
					trainCourseInfo = new TrainCourseinfo();
					trainCourseInfo.setCategoryId(categoryId);
					trainCourseInfo.setTeachType(mapTeachType.get(lo.get(3)));
					trainCourseInfo.setCourseName(courseName);
					trainCourseInfo.setMainTeacherName(teacheName);

					// 查询是否是团队授课模式 1：单一模式 2：群组模式
					//if (courseMode.equals("是")) {
					// 理论上会出现多个老师，所以要拆分，存入老师id
					String teaStr[] = teacheName.split(",");
					String teaIds = "";
					for (int j = 0; j < teaStr.length; j++) {
						hql = "from TrainTeacher a where a.isDelete=? and a.xm=?";
						List<TrainTeacher> trainTeachers = teacherService.getForValues(hql, 0, teaStr[j]);
						if (trainTeachers.size() > 1) {
							// 若存在多个同名老师，则记录报错信息
							errorLevel = "错误";								
							doResult = "异常信息：系统中存在同名的老师 " + teaStr[j] + "；请在班级课程管理处进行手动添加！";
							teaIds = null;
							isError = true;
							break;
						} else if (trainTeachers.size() == 1) {
							teaIds += trainTeachers.get(0).getUuid() + ",";
						} else {
							// 如果老师没找到id，就存放空字符串
							teaIds += ",";
						}
					}
					if (teaIds != null && !teaIds.equals("")) {
						teaIds = teaIds.substring(0, teaIds.length() - 1);
						trainCourseInfo.setMainTeacherId(teaIds);
					}

//					} else {
//						// 理论是单个教师，所以直接查找通过教师名称查找即可
//						hql = "from TrainTeacher a where a.isDelete=? and a.xm=?";
//						List<TrainTeacher> trainTeachers = teacherService.getForValues(hql, -1, teacheName);
//						if (trainTeachers.size() > 1) {
//							// 若存在多个同名老师，则记录报错信息
//							errorLevel = "错误";						
//							doResult = "异常信息：系统中存在同名的老师 " + teacheName + "；请在班级课程管理处进行手动调整！";
//							isError = true;
//						} else if (trainTeachers.size() == 1) {
//							trainCourseInfo.setMainTeacherId(trainTeachers.get(0).getUuid());
//						}
//					}
					
					if(isError==false)
						trainCourseinfoService.merge(trainCourseInfo);
				}
					
				//当没发生错误的时候，才会添加入库
				if(isError==false){
					tcs.setCourseId(trainCourseInfo.getUuid());
					tcs.setMainTeacherId(trainCourseInfo.getMainTeacherId());
	
					this.merge(tcs);
				}
			
			} catch (Exception e) {
				// return null;
				errorLevel = "错误";
				doResult = "导入失败；异常信息：" + e.getMessage();
			}

			if (!"导入成功".equals(doResult)) {
				// List<Map<String, Object>>
				notExits = new ImportNotInfo();
				notExits.setOrderIndex(notCount);
				notExits.setTitle(title);
				notExits.setErrorLevel(errorLevel);
				notExits.setErrorInfo(doResult);

				listNotExit.add(notExits);
				notCount++;
			}

		}
		
		//如果两个容器的大小一样，表明没有导入数据,否则导入了
		if(importData.size()!=listNotExit.size()){
			trainClassService.update(trainClass);
		}
		
		return listNotExit;
	}

	@Override
	public CourseEvalApp getCourseEvalStand(String scheduleId) {
		List<TrainIndicatorStand> evalStand = standService.getCourseEvalStand();
		String sql = "SELECT classId,classCategory,className,courseDate,courseTime,convert(varchar(10),verySatisfaction) as verySatisfaction"
				+ ",convert(varchar(10),satisfaction) as satisfaction,ranking,teacherId,teacherName,courseId,courseName,classScheduleId," +
				" teachTypeName FROM TRAIN_V_CLASSCOURSEEVAL where classScheduleId=''{0}''";
		sql = MessageFormat.format(sql,scheduleId);
		CourseEvalApp entity = new CourseEvalApp();
		List<TrainClassCourseEval> evalCourse = this.doQuerySqlObject(sql, TrainClassCourseEval.class);
		entity.setSuccess(true);
		entity.setMessage("获取评价标准成功");
		entity.setEvalCourse(evalCourse.get(0));
		entity.setEvalStand(evalStand);
		return entity;
	}
}