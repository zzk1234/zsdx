package com.zd.school.jw.train.service;

import com.zd.core.model.extjs.QueryResult;
import com.zd.core.service.BaseService;
import com.zd.school.jw.train.model.TrainCourseevaldetail;
import com.zd.school.plartform.system.model.SysUser;

import java.util.List;


/**
 * 
 * ClassName: TrainCourseevaldetailService
 * Function: TODO ADD FUNCTION. 
 * Reason: TODO ADD REASON(可选). 
 * Description: 课程评价明细 (TRAIN_T_COURSEEVALDETAIL)实体Service接口类.
 * date: 2017-06-19
 *
 * @author  luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
 
public interface TrainCourseevaldetailService extends BaseService<TrainCourseevaldetail> {

	/**
	 * 数据列表
	 * 
	 * @param start
	 *            查询的起始记录数
	 * @param limit
	 *            每页的记录数
	 * @param sort
	 *            排序参数
	 * @param filter
	 *            查询过滤参数
	 * @param isDelete
	 *            为true表示只列出未删除的， 为false表示列出所有
	 * @return
	 */
    public QueryResult<TrainCourseevaldetail> list(Integer start, Integer limit, String sort, String filter, Boolean isDelete); 

	/**
	 * 根据主键逻辑删除数据
	 * 
	 * @param ids
	 *            要删除数据的主键
	 * @param currentUser
	 *            当前操作的用户
	 * @return 操作成功返回true，否则返回false
	 */
	public Boolean doLogicDeleteByIds(String ids, SysUser currentUser);

	/**
	 * 根据传入的实体对象更新数据库中相应的数据
	 * 
	 * @param entity
	 *            传入的要更新的实体对象
	 * @param currentUser
	 *            当前操作用户
	 * @return
	 */
	public TrainCourseevaldetail doUpdateEntity(TrainCourseevaldetail entity, SysUser currentUser);

	/**
	 * 将传入的实体对象持久化到数据
	 * 
	 * @param entity
	 *            传入的要更新的实体对象
	 * @param currentUser
	 *            当前操作用户
	 * @return
	 */
	public TrainCourseevaldetail doAddEntity(TrainCourseevaldetail entity, SysUser currentUser);

	/**
	 * 添加课程评价明细数据
	 * @param evals
	 * @param currentUser
	 */
	public void saveCourseEval(List<TrainCourseevaldetail>evals, SysUser currentUser);
}