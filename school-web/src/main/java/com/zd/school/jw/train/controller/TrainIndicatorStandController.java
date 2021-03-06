package com.zd.school.jw.train.controller;

import com.zd.core.constant.Constant;
import com.zd.core.controller.core.FrameWorkController;
import com.zd.core.model.extjs.QueryResult;
import com.zd.core.util.ModelUtil;
import com.zd.core.util.StringUtils;
import com.zd.school.jw.train.model.TrainIndicatorStand;
import com.zd.school.jw.train.service.TrainIndicatorStandService;
import com.zd.school.plartform.system.model.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by luoyibo on 2017-06-18.
 */
@Controller
@RequestMapping("/TrainIndicatorStand")
public class TrainIndicatorStandController extends FrameWorkController<TrainIndicatorStand> implements Constant {
    @Resource
    private TrainIndicatorStandService thisService; // service层接口

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
    public void list(@ModelAttribute TrainIndicatorStand entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String strData = ""; // 返回给js的数据
        Integer start = super.start(request);
        Integer limit = super.limit(request);
        String sort = super.sort(request);
        String filter = super.filter(request);
        QueryResult<TrainIndicatorStand> qResult = thisService.list(start, limit, sort, filter,true);
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
    public void doAdd(TrainIndicatorStand entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException, IllegalAccessException, InvocationTargetException {

        //此处为放在入库前的一些检查的代码，如唯一校验等

        //获取当前操作用户
        SysUser currentUser = getCurrentSysUser();
        try {
/*             String indicatorName = entity.getIndicatorName();
            String hql1 = " o.isDelete='0' ";
           if (thisService.IsFieldExist("indicatorName", indicatorName, "-1", hql1)) {
                writeJSON(response, jsonBuilder.returnFailureJson("\"指标名称不能重复！\""));
                return;
            }*/

            entity = thisService.doAddEntity(entity, currentUser);// 执行增加方法
            if (ModelUtil.isNotNull(entity))
                writeJSON(response, jsonBuilder.returnSuccessJson(jsonBuilder.toJson(entity)));
            else
                writeJSON(response, jsonBuilder.returnFailureJson("'数据增加失败,详情见错误日志'"));
        } catch (Exception e) {
            writeJSON(response, jsonBuilder.returnFailureJson("'数据增加失败,详情见错误日志'"));
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
    public void doUpdates(TrainIndicatorStand entity, HttpServletRequest request, HttpServletResponse response)
            throws IOException, IllegalAccessException, InvocationTargetException {

        //入库前检查代码

        //获取当前的操作用户
        SysUser currentUser = getCurrentSysUser();
        try {
/*            String indicatorName = entity.getIndicatorName();
            String hql1 = " o.isDelete='0' ";
            if (thisService.IsFieldExist("indicatorName", indicatorName, entity.getUuid(), hql1)) {
                writeJSON(response, jsonBuilder.returnFailureJson("\"指标名称不能重复！\""));
                return;
            }*/

            entity = thisService.doUpdateEntity(entity, currentUser);// 执行修改方法
            if (ModelUtil.isNotNull(entity))
                writeJSON(response, jsonBuilder.returnSuccessJson(jsonBuilder.toJson(entity)));
            else
                writeJSON(response, jsonBuilder.returnFailureJson("'数据修改失败,详情见错误日志'"));
        } catch (Exception e) {
            writeJSON(response, jsonBuilder.returnFailureJson("'数据修改失败,详情见错误日志'"));
        }
    }

/*    @RequestMapping("/doStartUsing")
    public void doStartUsing(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String Ids = request.getParameter("ids");
        if (StringUtils.isEmpty(Ids)) {
            writeJSON(response, jsonBuilder.returnSuccessJson("'没有传入需要启用的规则'"));
            return;
        } else {
            SysUser currentUser = getCurrentSysUser();
            try {
                Object[] conditionValue = Ids.split(",");
                String[] propertyName = { "startUsing", "updateUser", "updateTime" };
                Object[] propertyValue = { Short.parseShort("1"), currentUser.getXm(), new Date() };
                thisService.updateByProperties("uuid", conditionValue, propertyName, propertyValue);

                writeJSON(response, jsonBuilder.returnSuccessJson("'启用指标成功'"));

            } catch (Exception e) {
                writeJSON(response, jsonBuilder.returnFailureJson("'启用指标失败,详情见错误日志'"));
            }
        }
    }*/
}
