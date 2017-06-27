
package com.zd.school.jw.train.controller;

import com.zd.core.constant.Constant;
import com.zd.core.controller.core.FrameWorkController;
import com.zd.core.model.extjs.QueryResult;
import com.zd.core.util.JsonBuilder;
import com.zd.core.util.ModelUtil;
import com.zd.core.util.StringUtils;
import com.zd.school.jw.train.model.TrainCoursecategory;
import com.zd.school.jw.train.model.TrainCoursecategoryTree;
import com.zd.school.jw.train.service.TrainCoursecategoryService;
import com.zd.school.plartform.system.model.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * ClassName: TrainCoursecategoryController
 * Function:  ADD FUNCTION. 
 * Reason:  ADD REASON(可选). 
 * Description: 课程分类信息(TRAIN_T_COURSECATEGORY)实体Controller.
 * date: 2017-03-07
 *
 * @author  luoyibo 创建文件
 * @version 0.1
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/TrainCoursecategory")
public class TrainCoursecategoryController extends FrameWorkController<TrainCoursecategory> implements Constant {

    @Resource
    TrainCoursecategoryService thisService; // service层接口
    
    @RequestMapping("/treelist")
    public void getTreeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String strData = "";
        String whereSql = request.getParameter("whereSql");
        String orderSql = request.getParameter("orderSql");

        SysUser currentUser = getCurrentSysUser();
        List<TrainCoursecategoryTree> lists = new ArrayList<TrainCoursecategoryTree>();
       
        lists = thisService.getTreeList(whereSql, orderSql);
        

        strData = JsonBuilder.getInstance().buildList(lists, "checked");// 处理数据
        writeJSON(response, strData);// 返回数据
    }
    
    /**
      * @Title: list
      * @Description: 查询数据列表
      * @param entity 实体类
      * @param request
      * @param response
      * @throws IOException    设定参数
      * @return void    返回类型
     */
    @RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET,
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public void list(@ModelAttribute TrainCoursecategory entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String strData = ""; // 返回给js的数据
		Integer start = super.start(request);
		Integer limit = super.limit(request);
		String sort = super.sort(request);
		String filter = super.filter(request);
        QueryResult<TrainCoursecategory> qResult = thisService.list(start, limit, sort, filter,true);
        strData = jsonBuilder.buildObjListToJson(qResult.getTotalCount(), qResult.getResultList(), true);// 处理数据
        writeJSON(response, strData);// 返回数据
    }

	/**
	 *
	 * @param entity
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
    @RequestMapping("/doadd")
    public void doAdd(TrainCoursecategory entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException, IllegalAccessException, InvocationTargetException {
        
		//此处为放在入库前的一些检查的代码，如唯一校验等
		
		//获取当前操作用户
		SysUser currentUser = getCurrentSysUser();
		try {
			
		    String categoryCode = entity.getNodeCode();
		    String categoryName = entity.getNodeText();
		      
			String hql1 = " o.isDelete='0' ";
/*	        if (thisService.IsFieldExist("categoryCode", categoryCode, "-1", hql1)) {
	            writeJSON(response, jsonBuilder.returnFailureJson("\"课程分类编码不能重复！\""));
	            return;
	        }*/
	        if (thisService.IsFieldExist("nodeText", categoryName, "-1", hql1)) {
	            writeJSON(response, jsonBuilder.returnFailureJson("\"课程分类名称不能重复！\""));
	            return;
	        }	        	        
	        
			entity = thisService.doAddEntity(entity, currentUser);// 执行增加方法
			if (ModelUtil.isNotNull(entity))
				writeJSON(response, jsonBuilder.returnSuccessJson(jsonBuilder.toJson(entity)));
			else
				writeJSON(response, jsonBuilder.returnFailureJson("\"数据增加失败,详情见错误日志\""));
		} catch (Exception e) {
			writeJSON(response, jsonBuilder.returnFailureJson("\"数据增加失败,详情见错误日志\""));
		}
    }

    /**
      * 
      * @Title: doDelete
      * @Description: 逻辑删除指定的数据
      * @param request
      * @param response
      * @return void    返回类型
      * @throws IOException  抛出异常
     */
    @RequestMapping("/dodelete")
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String delIds = request.getParameter("ids");
        if (StringUtils.isEmpty(delIds)) {
            writeJSON(response, jsonBuilder.returnSuccessJson("'没有传入删除主键'"));
            return;
        } else {
			SysUser currentUser = getCurrentSysUser();
			try {
				boolean flag = thisService.doLogicDeleteByIds(delIds, currentUser);
				if (flag) {
					writeJSON(response, jsonBuilder.returnSuccessJson("'删除成功'"));
				} else {
					writeJSON(response, jsonBuilder.returnFailureJson("'删除失败,详情见错误日志'"));
				}
			} catch (Exception e) {
				writeJSON(response, jsonBuilder.returnFailureJson("'删除失败,详情见错误日志'"));
			}
        }
    }

	/**
	 *
	 * @param entity
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
    @RequestMapping("/doupdate")
    public void doUpdates(TrainCoursecategory entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException, IllegalAccessException, InvocationTargetException {
		
		//入库前检查代码
		
		//获取当前的操作用户
		SysUser currentUser = getCurrentSysUser();
		try {
			entity = thisService.doUpdateEntity(entity, currentUser);// 执行修改方法
			if (ModelUtil.isNotNull(entity))
				writeJSON(response, jsonBuilder.returnSuccessJson(jsonBuilder.toJson(entity)));
			else
				writeJSON(response, jsonBuilder.returnFailureJson("'数据修改失败,详情见错误日志'"));
		} catch (Exception e) {
			writeJSON(response, jsonBuilder.returnFailureJson("'数据修改失败,详情见错误日志'"));
		}
    }

    @RequestMapping("/changeorder")
	public void doChangeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String strData = "";
    	String ids = request.getParameter("ids");
		String orders = request.getParameter("order");
		SysUser currrentUser = getCurrentSysUser();

		try {
			thisService.doChangeOrder(ids,orders,currrentUser);
			writeJSON(response, jsonBuilder.returnSuccessJson("'调整顺序成功'"));
		} catch (Exception e){
			writeJSON(response, jsonBuilder.returnFailureJson("'调整顺序成功失败,详情见错误日志'"));
		}
	}
}
