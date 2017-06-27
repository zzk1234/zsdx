package com.zd.school.plartform.system.service.Impl;

import java.awt.geom.Area;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.omg.CORBA.DATA_CONVERSION;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zd.core.model.extjs.QueryResult;
import com.zd.core.service.BaseServiceImpl;
import com.zd.core.util.BeanUtils;
import com.zd.core.util.DBContextHolder;
import com.zd.core.util.DateUtil;
import com.zd.core.util.SortListUtil;
import com.zd.core.util.StringUtils;
import com.zd.school.plartform.baseset.model.BaseJob;
import com.zd.school.plartform.baseset.model.BaseOrg;
import com.zd.school.plartform.baseset.service.BaseOrgService;
import com.zd.school.plartform.system.dao.SysUserDao;
import com.zd.school.plartform.system.model.CardUserInfoToUP;
import com.zd.school.plartform.system.model.SysRole;
import com.zd.school.plartform.system.model.SysUser;
import com.zd.school.plartform.system.model.SysUserToUP;
import com.zd.school.plartform.system.service.SysRoleService;
import com.zd.school.plartform.system.service.SysUserService;
import com.zd.school.teacher.teacherinfo.model.TeaTeacherbase;
import com.zd.school.teacher.teacherinfo.service.TeaTeacherbaseService;

/**
 * 
 * ClassName: BaseTUserServiceImpl Function: TODO ADD FUNCTION. Reason: TODO ADD
 * REASON(可选). Description: 用户管理实体Service接口实现类. date: 2016-07-17
 *
 * @author luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
@Service
@Transactional
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService {

	@Resource
	public void setBaseTUserDao(SysUserDao dao) {
		this.dao = dao;
	}

	@Resource
	private SysRoleService roleService; // 角色数据服务接口

	@Resource
	private BaseOrgService orgService; // 部门数据服务接口

	@Resource
	private TeaTeacherbaseService teacherService;

	@Override
	public SysUser doAddUser(SysUser entity, SysUser currentUser) throws Exception, InvocationTargetException {

		String userPwd = entity.getUserPwd();
		userPwd = new Sha256Hash(userPwd).toHex();
		// String roleId = entity.getRoleId();

		SysUser saveEntity = new SysUser();
		BeanUtils.copyPropertiesExceptNull(entity, saveEntity);

		// 处理用户所属的角色
		/*
		 * if (roleId.length() > 0) { String[] roleList = roleId.split(",");
		 * Set<SysRole> isUserRoles = entity.getSysRoles(); for (String ids :
		 * roleList) { SysRole role = roleService.get(ids);
		 * isUserRoles.add(role); }
		 * 
		 * entity.setSysRoles(isUserRoles); }
		 */
		entity.setUserPwd(userPwd);
		entity.setIssystem(1);
		entity.setIsHidden("0");
		entity.setCreateUser(currentUser.getXm()); // 创建人
		// 持久化到数据库
		entity = this.merge(entity);

		return entity;
	}

	@Override
	public SysUser doUpdateUser(SysUser entity, SysUser currentUser) throws Exception, InvocationTargetException {

		// 先拿到已持久化的实体
		SysUser perEntity = this.get(entity.getUuid());

		Set<SysRole> isUserRoles = perEntity.getSysRoles();
		/*Set<BaseOrg> userDept = perEntity.getUserDepts();*/

		// 将entity中不为空的字段动态加入到perEntity中去。
		BeanUtils.copyPropertiesExceptNull(perEntity, entity);

		perEntity.setUpdateTime(new Date()); // 设置修改时间
		perEntity.setUpdateUser(currentUser.getXm()); // 设置修改人的中文名
		perEntity.setSysRoles(isUserRoles);
		/*perEntity.setUserDepts(userDept);*/
		// entity = thisService.merge(perEntity);// 执行修改方法

		// 处理用户所属的角色
		/*
		 * if (roleId.length() > 0) { String[] roleList = roleId.split(",");
		 * Set<SysRole> isUserRoles = entity.getSysRoles(); //先清除所有的角色
		 * isUserRoles.removeAll(isUserRoles); for (String ids : roleList) {
		 * SysRole role = roleService.get(ids); isUserRoles.add(role); }
		 * 
		 * perEntity.setSysRoles(isUserRoles); }
		 */
		// 持久化到数据库
		entity = this.merge(perEntity);

		return entity;
	}

	@Override
	public Boolean deleteUserRole(String userId, String delRoleIds, SysUser currentUser) {
		Boolean delReurn = false;
		// 获取当前用户的信息
		SysUser theUser = this.get(userId);
		Set<SysRole> theUserRole = theUser.getSysRoles();

		String[] delId = delRoleIds.split(",");
		List<SysRole> delRoles = roleService.queryByProerties("uuid", delId);

		theUserRole.removeAll(delRoles);

		theUser.setSysRoles(theUserRole);

		this.merge(theUser);

		delReurn = true;
		// TODO Auto-generated method stub
		return delReurn;
	}

	@Override
	public Boolean addUserRole(String userId, String addRoleIds, SysUser currentUser) {

		Boolean addResult = false;
		// 获取当前用户的信息
		SysUser theUser = this.get(userId);
		Set<SysRole> theUserRole = theUser.getSysRoles();

		String[] addId = addRoleIds.split(",");
		List<SysRole> addRoles = roleService.queryByProerties("uuid", addId);

		theUserRole.addAll(addRoles);

		theUser.setSysRoles(theUserRole);

		this.merge(theUser);

		addResult = true;

		return addResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public QueryResult<SysUser> getDeptUser(Integer start, Integer limit, String sort, String filter, Boolean isDelete,
			String userIds, SysUser currentUser) {

		String sortSql = StringUtils.convertSortToSql(sort);
		String userId = userIds;
		StringBuffer hql = new StringBuffer();
		String countHql = "";
		String filterSql = "";
		if (StringUtils.isEmpty(userIds)) {
			return null;
		} else {
			if (!StringUtils.isEmpty(filter)) {
				filterSql = StringUtils.convertFilterToSql(filter);
			}
/*			BaseOrg org = orgService.get(deptId);
			deptIds = org.getTreeIds();
			hql.append(" from SysUser as o inner join fetch o.userDepts as r where r.treeIds like '" + deptIds
					+ "%' and r.isDelete=0 ");
			if (isDelete)
				hql.append(" and o.isDelete=0 ");
			hql.append(filterSql);
			hql.append(" order by  o.jobCode ");
			QueryResult<SysUser> qr = this.doQueryResult(hql.toString(), start, limit);*/
			hql.append(" from SysUser where uuid in("+userId+")");
			if (isDelete)
				hql.append(" and isDelete=0 ");
			hql.append(filterSql);
			QueryResult<SysUser> qr = this.doQueryResult(hql.toString(), start, limit);
			
			return qr;
		}
	}

	/**
	 * 
	 * batchSetDept:批量设置用户的所属部门.
	 *
	 * @author luoyibo
	 * @param deptId
	 * @param userIds
	 * @param cuurentUser
	 * @return Boolean
	 * @throws @since
	 *             JDK 1.8
	 */
	// @Override
	// public Boolean batchSetDept(String deptId, String userIds, SysUser
	// cuurentUser) {
	// Boolean reResult = false;
	// String[] delId = userIds.split(",");
	// List<SysUser> listUser = this.queryByProerties("uuid", delId);
	// for (SysUser sysUser : listUser) {
	// Set<BaseOrg> userDept = sysUser.getUserDepts();
	// BaseOrg org = orgService.get(deptId);
	// BaseOrg tempOrg = orgService.get("058b21fe-b37f-41c9-ad71-091f97201ff8");
	// userDept.remove(tempOrg);
	// userDept.add(org);
	//
	// sysUser.setUserDepts(userDept);
	// sysUser.setUpdateTime(new Date());
	// sysUser.setUpdateUser(cuurentUser.getXm());
	//
	// this.merge(sysUser);
	// reResult = true;
	// }
	// return reResult;
	// }

	@Override
	public List<SysUser> getUserByRoleName(String roleName) {
		/*
		 * String sql =
		 * "SELECT USER_ID FROM SYS_T_USER WHERE USER_ID IN(SELECT USER_ID FROM SYS_T_ROLEUSER WHERE ROLE_ID IN(SELECT ROLE_ID FROM SYS_T_ROLE WHERE ROLE_NAME='"
		 * + roleName + "'))"; List<Object[]> list = this.ObjectQuerySql(sql);
		 * List<SysUser> users = new ArrayList<SysUser>(); for (int i = 0; i <
		 * list.size(); i++) { String userid = list.get(i) + "";
		 * users.add(this.get(userid)); }
		 */
		String hql = "from SysUser as u inner join fetch u.sysRoles as r where r.roleName='" + roleName
				+ "' and r.isDelete=0 and u.isDelete=0";
		return this.doQuery(hql);
	}

	@Override
	public Boolean doDeleteUser(String delIds, String orgId, SysUser currentUser) {
		String[] ids = delIds.split(",");
		boolean flag = false;
/*		for (String id : ids) {
			SysUser user = this.get(id);
			BaseOrg org = orgService.get(orgId);
			Set<BaseOrg> userDept = user.getUserDepts();
			userDept.remove(org);

			user.setUpdateTime(new Date());
			user.setUpdateUser(currentUser.getXm());
			user.setUserDepts(userDept);

			this.merge(user);

			flag = true;
		}*/
		// TODO Auto-generated method stub
		return flag;
	}

	@Override
	public QueryResult<SysUser> getUserByRoleId(String roleId) {
		QueryResult<SysUser> qr = new QueryResult<SysUser>();
		String hql = "from SysUser as u inner join fetch u.sysRoles as r where r.uuid='" + roleId
				+ "' and r.isDelete=0 and u.isDelete=0 order by u.jobCode asc ";
		List<SysUser> list = this.doQuery(hql);

		SortListUtil<SysUser> sortJob = new SortListUtil<SysUser>();
		sortJob.Sort(list, "jobCode", "String");
		qr.setResultList(list);
		qr.setTotalCount((long) list.size());

		return qr;
		// List<SysUser> newList = new ArrayList<SysUser>();
		// if (list.size() > 0) {
		// StringBuffer sbJobName = new StringBuffer();
		// for (SysUser user : list) {
		// TeaTeacherbase teacherbase = teacherService.get(user.getUuid());
		// String jobInfo = teacherService.getTeacherJobs(teacherbase);
		// String[] strings = jobInfo.split(",");
		// user.setJobId(strings[0]);
		// user.setJobName(strings[1]);
		//
		// String deptInfo = teacherService.getTeacherDepts(teacherbase);
		// strings = deptInfo.split(",");
		// user.setDeptName(strings[1]);
		// newList.add(user);
		// }
		// qr.setResultList(newList);
		// qr.setTotalCount((long) list.size());
		// return qr;
		// } else
		// return null;
	}

	@Override
	public int syncUserInfoToUP(SysUserToUP sysUserInfo, String userId) {
		int row = 0;
		try {
			// 1.查询该数据源中的此用户的信息
			String sql = "select top 1 UserId as userId,convert(varchar,EmployeeID) as employeeId,DepartmentID as departmentId,"
					+ " convert(varchar(36),EmployeeName) as employeeName,"
					+ " employeeStrId,sid,convert(varchar(1),sexId) sexId,identifier " + " from Tc_Employee "
					+ " where UserId='" + userId + "'";

			List<SysUserToUP> upUserInfos = this.doQuerySqlObject(sql, SysUserToUP.class);

			// 2.判断用户信息该作哪种处理
			if (upUserInfos.isEmpty()) { // 若UP没有此数据，则增加
				if (sysUserInfo != null ) {
					String sqlInsert = "insert into Tc_Employee(UserId,DepartmentID,EmployeeName,EmployeeStrID,SID,EmployeePWD,SexID,identifier,cardid,CardTypeID,EmployeeStatusID) "
							+ "values('" + sysUserInfo.getUserId() + "','" + sysUserInfo.getDepartmentId() + "','"
							+ sysUserInfo.getEmployeeName() + "'," + "'" + sysUserInfo.getEmployeeStrId() + "','"
							+ sysUserInfo.getSid() + "','" + sysUserInfo.getEmployeePwd() + "','"
							+ sysUserInfo.getSexId() + "','" + sysUserInfo.getIdentifier() + "',0,1,24)";

					row = this.executeSql(sqlInsert);
				}
			} else { // 若存在，则判断是修改还是删除
				SysUserToUP upUserInfo = upUserInfos.get(0);

				if (sysUserInfo == null) { // 没有此人数据，则删除
					String sqlDelete = "update Tc_Employee set EmployeeStatusID='26' where UserId='" + userId + "'";// 逻辑删除
					row = this.executeSql(sqlDelete);

				} else { // 若数据都存在，则判断是否有修改
					/* web平台中不维护卡片信息，故注释掉
					if (!sysUserInfo.getCardState().equals("0")) { // 预设：CardState!=0;卡片状态，在web系统中强行设置为退卡状态
						String sqlDelete = " delete from TC_Card_Bags where EMPLOYEEID='" + upUserInfo.getEmployeeId()
								+ "'"; // 物理删除
						// this.executeSql(sqlDelete);

						sqlDelete += " delete from TC_Card where EMPLOYEEID='" + upUserInfo.getEmployeeId() + "'"; // 物理删除
						// this.executeSql(sqlDelete);

						sqlDelete += " delete from Tc_Employee where EMPLOYEEID='" + upUserInfo.getEmployeeId() + "'"; // 物理删除

						row = this.executeSql(sqlDelete);

					} else 
						*/
						if (!sysUserInfo.equals(upUserInfo)) { // 对比部分数据是否一致
						String sqlUpdate = " update Tc_Employee set DepartmentID='" + sysUserInfo.getDepartmentId()
								+ "'," + "EmployeeName='" + sysUserInfo.getEmployeeName() + "',EmployeeStrID='"
								+ sysUserInfo.getEmployeeStrId() + "'," + "SexID='"
								+ sysUserInfo.getSexId() + "',identifier='" + sysUserInfo.getIdentifier()
								+ "' where UserId='" + userId + "'";

						row = this.executeSql(sqlUpdate);
					}
				}
			}

		} catch (Exception e) {
			row = -1;
		}

		return row;
	}

	@Override
	public int syncUserInfoToAllUP(List<SysUserToUP> userInfos) {
		int row = 0;
		try {
			// 1.查询该数据源中的此用户的信息
			String sql = "select UserId as userId,convert(varchar,EmployeeID) as employeeId,DepartmentID as departmentId,"
					+ " convert(varchar(36),EmployeeName) as employeeName,"
					+ " employeeStrId,sid,convert(varchar(1),sexId) sexId,identifier "
					+ " from Tc_Employee order by userId asc";

			List<SysUserToUP> upUserInfos = this.doQuerySqlObject(sql, SysUserToUP.class);

			// 循环对比
			SysUserToUP currentUser = null;
			SysUserToUP upUser = null;
			boolean isExist = false;
			StringBuffer sqlSb=new StringBuffer();
			for (int i = 0; i < userInfos.size(); i++) {
				currentUser = userInfos.get(i);
				isExist = false;
				
				for (int j = 0; j < upUserInfos.size(); j++) {
					upUser = upUserInfos.get(j);
					if (currentUser.getUserId().equals(upUser.getUserId())) {
						// 执行代码
						isExist = true;
						if (currentUser.getIsDelete() == 1) {
							// sqlDelete = "delete from Tc_Employee where
							// UserId='" + UserId + "'"; 物理删除
							
							sqlSb.append(" update Tc_Employee set EmployeeStatusID='26' where UserId='"
									+ currentUser.getUserId() + "'");// 逻辑删除

						} 
						/* web平台中不维护卡片信息，故注释掉
						else if (!currentUser.getCardState().equals("0")) {// 预设：CardState!=0;卡片状态，在web系统中强行设置为退卡状态
							
//							String sqlDelete = "delete from TC_Card_Bags where EMPLOYEEID='" + upUser.getEmployeeId()
//									+ "'"; // 物理删除
//							
//							sqlDelete += " delete from TC_Card where EMPLOYEEID='" + upUser.getEmployeeId() + "'"; // 物理删除					
//
//							sqlDelete += " delete from Tc_Employee where EMPLOYEEID='" + upUser.getEmployeeId() + "'"; // 物理删除
//							
//							this.executeSql(sqlDelete);
							
							sqlSb.append(" delete from TC_Card_Bags where EMPLOYEEID='" + upUser.getEmployeeId()+ "'");
							sqlSb.append(" delete from TC_Card where EMPLOYEEID='" + upUser.getEmployeeId() + "'");
							sqlSb.append(" delete from Tc_Employee where EMPLOYEEID='" + upUser.getEmployeeId() + "'");
							
						} */					
						else if (!currentUser.equals(upUser)) { // 对比数据（一部分需要判断的数据）是否一致
							String sqlUpdate = " update Tc_Employee set DepartmentID='" + currentUser.getDepartmentId()
									+ "'," + "EmployeeName='" + currentUser.getEmployeeName() + "',EmployeeStrID='"
									+ currentUser.getEmployeeStrId() + "',"
									+ "SexID='" + currentUser.getSexId() + "',identifier='"
									+ currentUser.getIdentifier() + "',EmployeeStatusID='24' where UserId='" + currentUser.getUserId() + "'";

							//this.executeSql(sqlUpdate);
							
							sqlSb.append(sqlUpdate);
						}

						upUserInfos.remove(j);
						break; // 跳出

					}
				}
				
				//若上面的循环无法找到对应的人员，表明UP中不存在此用户
				if (!isExist && currentUser.getIsDelete() == 0) {												
					String sqlInsert = "insert into Tc_Employee(UserId,DepartmentID,EmployeeName,EmployeeStrID,SID,EmployeePWD,SexID,identifier,cardid,CardTypeID,EmployeeStatusID,PositionId) "
							+ "values('" + currentUser.getUserId() + "','" + currentUser.getDepartmentId() + "','"
							+ currentUser.getEmployeeName() + "'," + "'" + currentUser.getEmployeeStrId() + "','"
							+ currentUser.getSid() + "','" + currentUser.getEmployeePwd() + "','"
							+ currentUser.getSexId() + "','" + currentUser.getIdentifier() + "',0,1,24,19)";
					
					sqlSb.append(sqlInsert);
					//this.executeSql(sqlInsert);					
				}
				
				//若积累的语句长度大于2000（大约50条语句左右），则执行
				if(sqlSb.length()>2000){
					row+=this.executeSql(sqlSb.toString());	
					sqlSb.setLength(0);	//清空
				}
			}		
			
			//最后执行一次
			if(sqlSb.length()>0)
				row+=this.executeSql(sqlSb.toString());	
			
			// 剩下的，表明不存在平台的库中，进行删除
			/* 暂时不开放
			for (int k = 0; k < upUserInfos.size(); k++) {
				upUser = userInfos.get(k);
				
				//sqlDelete="update Tc_Employee set EmployeeStatusID='26' where UserId='" + UserId + "'";//逻辑删除
				//this.executeSql(sqlDelete);
				
				String sqlDelete = "delete from TC_Card_Bags where EMPLOYEEID='" + upUser.getEmployeeId() + "'";	//物理删除	
			
				sqlDelete += " delete from TC_Card where EMPLOYEEID='" + upUser.getEmployeeId() + "'";	//物理删除					
				
				sqlDelete += "delete from Tc_Employee where EMPLOYEEID='" + upUser.getEmployeeId() + "'";	//物理删除	
				
				this.executeSql(sqlDelete);														
								
			}
			*/
			
		} catch (Exception e) {
			//捕获了异常后，要手动进行回滚；		还需要进行验证测试是否完全正确。
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			
			row = -1;
		}

		return row;
	}

	@Override
	public int syncAllCardInfoFromUp(List<CardUserInfoToUP> upCardUserInfos) {
		int row = 0;
		try {
			// 1.查询web平台的发卡信息
			String sql = "select convert(varchar,FACT_NUMB) as factNumb,USE_STATE as useState,"
					+ " USER_ID as userId,convert(varchar,UP_CARD_ID) as upCardId "
					+ " from CARD_T_USEINFO where ISDELETE=0"
					+ " order by upCardId asc";

			List<CardUserInfoToUP> webCardUserInfos = this.doQuerySqlObject(sql, CardUserInfoToUP.class);
		
			String updateTime = null;
			
			// 循环对比
			CardUserInfoToUP webCardUser = null;
			CardUserInfoToUP upCardUser = null;
			boolean isExist = false;
			StringBuffer sqlSb=new StringBuffer();
			String sqlStr="";
			for (int i = 0; i < upCardUserInfos.size(); i++) {
				upCardUser = upCardUserInfos.get(i);
				sqlStr="";
				isExist = false;
				
				for (int j = 0; j < webCardUserInfos.size(); j++) {
					webCardUser = webCardUserInfos.get(j);
					//若web库中存在此发卡信息
					if (upCardUser.getUpCardId().equals(webCardUser.getUpCardId())) {
						// 执行代码
						isExist = true;					
						if (!upCardUser.equals(webCardUser)) { // 对比数据（一部分需要判断的数据）是否一致
							updateTime = DateUtil.formatDateTime(new Date());	
							
							sqlStr = "update CARD_T_USEINFO set "
									+ "	FACT_NUMB='"+upCardUser.getFactNumb()+"',USE_STATE='"+upCardUser.getUseState()+"',"
									+ "	USER_ID='"+upCardUser.getUserId()+"',UPDATE_TIME=CONVERT(datetime,'" + updateTime+ "')"
									+ " where UP_CARD_ID='"+upCardUser.getUpCardId()+"'";
												
							sqlSb.append(sqlStr+"  ");
						}
							
						webCardUserInfos.remove(j);
						break; // 跳出
					}
				}
				
				//若上面的循环无法找到对应的卡片信息，表明UP中不存在此卡片信息
				if (!isExist) {						
					updateTime = DateUtil.formatDateTime(new Date());	
					
					sqlStr = "insert into CARD_T_USEINFO(CARD_ID,CREATE_TIME,CREATE_USER,"
							+ "ISDELETE,FACT_NUMB,USE_STATE,USER_ID,UP_CARD_ID)"
							+ " values ('"+UUID.randomUUID().toString()+"',CONVERT(datetime,'" + updateTime+ "'),'超级管理员',"
									+ "0,'"+upCardUser.getFactNumb()+"',"+upCardUser.getUseState()+","
									+ "'"+upCardUser.getUserId()+"','"+upCardUser.getUpCardId()+"')";
					
					sqlSb.append(sqlStr+"  ");
				
				}
				
				//若积累的语句长度大于3000（大约50条语句左右），则执行
				if(sqlSb.length()>3000){
					row+=this.executeSql(sqlSb.toString());	
					sqlSb.setLength(0);	//清空
				}
			}		
			
			//最后执行一次
			if(sqlSb.length()>0)
				row+=this.executeSql(sqlSb.toString());	
			
		} catch (Exception e) {
			row = -1;
		}

		return row;
	}
}