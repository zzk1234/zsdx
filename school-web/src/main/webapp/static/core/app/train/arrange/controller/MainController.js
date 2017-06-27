Ext.define("core.train.arrange.controller.MainController", {
    extend: "Ext.app.ViewController",
    alias: 'controller.arrange.mainController',
    mixins: {

        suppleUtil: "core.util.SuppleUtil",
        messageUtil: "core.util.MessageUtil",
        formUtil: "core.util.FormUtil",
        gridActionUtil: "core.util.GridActionUtil",
        dateUtil: 'core.util.DateUtil'

    },
    init: function() {
        /*control事件声明代码，可以写在这里
        this.control({

        });
        */
    },
    control: {
        "basegrid[xtype=arrange.maingrid]": {
            itemclick: function(grid, record, item, index, e, eOpts) {
                var basePanel = grid.up("basepanel");
                var funCode = basePanel.funCode;
                var baseGrid = basePanel.down("basegrid[funCode=" +
                    funCode + "]");
                var records = baseGrid.getSelectionModel().getSelection();

                var btnArrangeRoom = baseGrid.down("button[ref=gridArrangeRoom_Tab]");
                var btnArrangeSite = baseGrid.down("button[ref=gridArrangeSite_Tab]");
                var btnArrange = baseGrid.down("button[ref=gridArrange]");

                if (records.length == 0) {
                    if (btnArrangeRoom)
                        btnArrangeRoom.setDisabled(true);
                    if (btnArrangeSite)
                        btnArrangeSite.setDisabled(true);
                    if (btnArrange)
                        btnArrange.setDisabled(true);

                } else if (records.length == 1) {
                    
                    // if(record.get("isarrange")==1&&record.get("isuse")==2){
                    //     if (btnArrangeRoom)
                    //         btnArrangeRoom.setDisabled(true);
                    //     if (btnArrangeSite)
                    //         btnArrangeSite.setDisabled(true);
                    //     if (btnArrange)
                    //         btnArrange.setDisabled(true);                    
                    // }else{
                        if (btnArrangeRoom)
                            btnArrangeRoom.setDisabled(false);
                        if (btnArrangeSite)
                            btnArrangeSite.setDisabled(false);
                        if (btnArrange)
                            btnArrange.setDisabled(false);
                    //}

                } else {
                    if (btnArrangeRoom)
                        btnArrangeRoom.setDisabled(true);
                    if (btnArrangeSite)
                        btnArrangeSite.setDisabled(true);
                    if (btnArrange)
                        btnArrange.setDisabled(true);
                }
                    //console.log(1231);
                
            }
        },
        "basegrid[xtype=arrange.maingrid] button[ref=gridArrange]": {
            beforeclick: function(btn) {
                var self = this;

                //得到组件
                var baseGrid = btn.up("basegrid");

                var records = baseGrid.getSelectionModel().getSelection();
                if (records.length != 1) {
                    self.Warning("请选择数据!");
                    return;
                }

                var classId = records[0].get("uuid");
                var className = records[0].get("className");;

                if (!classId) {
                    self.Warning("信息有误，请选择班级！");
                    return false;
                }
                if(records[0].get("isuse")==2){
                    self.Warning("此班级修改后未提交，请等待提交后再安排！");
                    return false;
                }

                Ext.MessageBox.confirm('温馨提示', '<p>执行此操作后将会自动同步相关数据！</p><p style="color:red;font-size:14px;font-weight: 400;">你确定要执行安排操作吗？</p>', function(btn, text) {
                    if (btn == 'yes') {
                        Ext.Msg.wait('正在执行中,请稍后...', '温馨提示');
                        self.asyncAjax({
                            url: comm.get("baseUrl")  + "/TrainClass/doClassArrange",
                            params: {
                                classId:classId                             
                            },
                            timeout:1000*60*60, //1个小时
                            //回调代码必须写在里面
                            success: function(response) {
                                var data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                                                           
                                if(data.success){ 
                                    baseGrid.getStore().load();                                
                                    Ext.Msg.hide();
                                    self.Info(data.obj);
                                }else{                        
                                    Ext.Msg.hide();
                                    self.Error(data.obj);
                                }
                            },
                            failure: function(response) {                        
                                Ext.Msg.hide();
                                Ext.Msg.alert('请求失败', '错误信息：\n' + response.responseText);
                            }
                        });  
                    }
                });

        
                return false;
            }
        },
        
        /**
         * 导出住宿信息
         */
        "basegrid[xtype=arrange.maingrid] button[ref=gridExportRoom]": {
            beforeclick: function (btn) {
                var self = this;
                //得到组件
                var baseGrid = btn.up("basegrid");
                var funCode = baseGrid.funCode;
                var basePanel = baseGrid.up("basepanel[funCode=" + funCode + "]");
                //得到配置信息
                var funData = basePanel.funData;
                var pkName = funData.pkName;
                //得到选中数据
                var records = baseGrid.getSelectionModel().getSelection();
                if(records.length!=1){
                    self.Warning("请选择一个班级进行导出操作！");
                    return;
                }

                var rec=records[0];

                var title = "确定要导出【"+rec.get("className")+"】班级住宿安排信息吗？";
            
                var ids = new Array();
                var pkValue = rec.get(pkName);
                ids.push(pkValue);            
                // if (records.length > 0) {
                //     title = "将导出所选师资的信息";
                //     Ext.each(records, function (rec) {
                //         var pkValue = rec.get(pkName);
                //         ids.push(pkValue);
                //     });
                // }

                Ext.Msg.confirm('提示', title, function (btn, text) {
                    if (btn == "yes") {
                        Ext.Msg.wait('正在导出中,请稍后...', '温馨提示');
                        //window.location.href = comm.get('baseUrl') + "/TrainClass/exportExcel?ids=" + ids.join(",");
                        var component=Ext.create('Ext.Component', {
                            title: 'HelloWorld',
                            width: 0,
                            height:0,
                            hidden:true,
                            html: '<iframe src="' + comm.get('baseUrl') + '/TrainClass/exportRoomExcel?ids=' + ids.join(",") + '"></iframe>',
                            renderTo: Ext.getBody()
                        });
                        
                       
                        var time=function(){
                            self.syncAjax({
                                url: comm.get('baseUrl') + '/TrainClass/checkExportRoomEnd',
                                timeout: 1000*60*30,        //半个小时         
                                //回调代码必须写在里面
                                success: function(response) {
                                    data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                                    if(data.success){
                                        Ext.Msg.hide();
                                        self.msgbox(data.obj);
                                        component.destroy();                                
                                    }else{                                    
                                        if(data.obj==0){    //当为此值，则表明导出失败
                                            Ext.Msg.hide();
                                            self.Error("导出失败，请重试或联系管理员！");
                                            component.destroy();                                        
                                        }else{
                                            setTimeout(function(){time()},1000);
                                        }
                                    }               
                                },
                                failure: function(response) {
                                    Ext.Msg.hide();
                                    Ext.Msg.alert('请求失败', '错误信息：\n' + response.responseText);
                                    component.destroy();
                                }
                            });
                        }
                        setTimeout(function(){time()},1000);    //延迟1秒执行
                    }
                });
                return false;
            }
        },
        /**
         * 导出场地信息
         */
        "basegrid[xtype=arrange.maingrid] button[ref=gridExportSite]": {
            beforeclick: function (btn) {
                var self = this;
                //得到组件
                var baseGrid = btn.up("basegrid");
                var funCode = baseGrid.funCode;
                var basePanel = baseGrid.up("basepanel[funCode=" + funCode + "]");
                //得到配置信息
                var funData = basePanel.funData;
                var pkName = funData.pkName;
                //得到选中数据
                var records = baseGrid.getSelectionModel().getSelection();
                if(records.length!=1){
                    self.Warning("请选择一个班级进行导出操作！");
                    return;
                }

                var rec=records[0];

                var title = "确定要导出【"+rec.get("className")+"】班级课程场地安排信息吗？";
            
                var ids = new Array();
                var pkValue = rec.get(pkName);
                ids.push(pkValue);            
                // if (records.length > 0) {
                //     title = "将导出所选师资的信息";
                //     Ext.each(records, function (rec) {
                //         var pkValue = rec.get(pkName);
                //         ids.push(pkValue);
                //     });
                // }

                Ext.Msg.confirm('提示', title, function (btn, text) {
                    if (btn == "yes") {
                        Ext.Msg.wait('正在导出中,请稍后...', '温馨提示');
                        //window.location.href = comm.get('baseUrl') + "/TrainClass/exportExcel?ids=" + ids.join(",");
                        var component=Ext.create('Ext.Component', {
                            title: 'HelloWorld',
                            width: 0,
                            height:0,
                            hidden:true,
                            html: '<iframe src="' + comm.get('baseUrl') + '/TrainClass/exportSiteExcel?ids=' + ids.join(",") + '"></iframe>',
                            renderTo: Ext.getBody()
                        });
                        
                       
                        var time=function(){
                            self.syncAjax({
                                url: comm.get('baseUrl') + '/TrainClass/checkExportSiteEnd',
                                timeout: 1000*60*30,        //半个小时         
                                //回调代码必须写在里面
                                success: function(response) {
                                    data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                                    if(data.success){
                                        Ext.Msg.hide();
                                        self.msgbox(data.obj);
                                        component.destroy();                                
                                    }else{                                    
                                        if(data.obj==0){    //当为此值，则表明导出失败
                                            Ext.Msg.hide();
                                            self.Error("导出失败，请重试或联系管理员！");
                                            component.destroy();                                        
                                        }else{
                                            setTimeout(function(){time()},1000);
                                        }
                                    }               
                                },
                                failure: function(response) {
                                    Ext.Msg.hide();
                                    Ext.Msg.alert('请求失败', '错误信息：\n' + response.responseText);
                                    component.destroy();
                                }
                            });
                        }
                        setTimeout(function(){time()},1000);    //延迟1秒执行
                    }
                });
                return false;
            }
        },

        "basegrid[xtype=arrange.maingrid]  button[ref=gridArrangeRoom_Tab]": {
            beforeclick: function(btn) {                
                this.doArrangeRoomDetail_Tab(btn, "edit");

                return false;
            }
        },
        "basegrid[xtype=arrange.maingrid]  button[ref=gridArrangeSite_Tab]": {
            beforeclick: function(btn) {

                this.doArrangeSiteDetail_Tab(btn, "edit");

                return false;
            }
        },
        
        "basegrid[xtype=arrange.maingrid]  actioncolumn": {
            gridArrangeRoomClick_Tab: function(data) {

                var baseGrid = data.view;
                var record = data.record;

                this.doArrangeRoomDetail_Tab(null, "edit", baseGrid, record);

                return false;
            },
            gridArrangeSiteClick_Tab: function(data) {

                var baseGrid = data.view;
                var record = data.record;

                this.doArrangeSiteDetail_Tab(null, "edit", baseGrid, record);

                return false;
            },
            detailClick_Tab: function(data) {

                var baseGrid = data.view;
                var record = data.record;

                this.doClassDetail_Tab(null, "detail", baseGrid, record);

                return false
            },
            gridBindCardClick_Tab:function(data){
                var baseGrid = data.view;
                var record = data.record;

                this.doBindCardDetail_Tab(null, "edit", baseGrid, record);

                return false
            }
        }        

    },

    doArrangeRoomDetail_Tab: function(btn, cmd, grid, record) {

        var self = this;
        var baseGrid;
        var recordData;

        if (btn) {
            baseGrid = btn.up("basegrid");
        } else {
            baseGrid = grid;
            recordData = record.data;
        }

        //得到组件
        var funCode = baseGrid.funCode;
        var basePanel = baseGrid.up("basepanel[funCode=" + funCode +"]");
        var tabPanel=baseGrid.up("tabpanel[xtype=app-main]");

        //得到配置信息
        var funData = basePanel.funData;
        var detCode = "arrange_roomDetail";   //修改此funCode，方便用于捕获window的确定按钮
        var detLayout = basePanel.detLayout;
        var defaultObj = funData.defaultObj;
                
        //关键：window的视图控制器
        var otherController = basePanel.otherController;
        if (!otherController)
            otherController = '';

        //处理特殊默认值
        var insertObj = {};
        var popFunData = Ext.apply(funData, {
            grid: baseGrid
        });


        //根据cmd操作类型，来设置不同的值
        var tabTitle = "住宿安排"; 
        //设置tab页的itemId
        var tabItemId=funCode+"_gridRoomDetail";     //命名规则：funCode+'_ref名称',确保不重复
        var pkValue= null;
        var operType="detail";

        switch (cmd) {
            case "edit":

                if (btn) {
                    var rescords = baseGrid.getSelectionModel().getSelection();
                    if (rescords.length != 1) {
                        self.msgbox("请选择一条数据！");
                        return;
                    }
                    recordData = rescords[0].data;
                }

                if(recordData.isuse==2){
                    self.Warning("此班级修改后未提交，请等待提交后再安排！");
                    return false;
                }

                insertObj = recordData;
                tabTitle = "住宿安排";
                tabItemId=funCode+"_gridRoomEdit"; 

                //获取主键值
                var pkName = funData.pkName;
                pkValue= recordData[pkName];
                    
                break;
            case "detail":

                if (btn) {
                    var rescords = baseGrid.getSelectionModel().getSelection();
                    if (rescords.length != 1) {
                        self.msgbox("请选择一条数据！");
                        return;
                    }
                    recordData = rescords[0].data;
                }

                insertObj = recordData;
                tabTitle = "查看住宿安排";
                tabItemId=funCode+"_gridRoomDetail"+insertObj.classNumb; 
                break;
        }

        //获取tabItem；若不存在，则表示要新建tab页，否则直接打开
        var tabItem=tabPanel.getComponent(tabItemId);
        if(!tabItem){
    
            tabItem=Ext.create({
                xtype:'container',
                title: tabTitle,
                //iconCls: 'x-fa fa-clipboard',
                scrollable :true, 
                itemId:tabItemId,
                itemPKV:pkValue,      //保存主键值
                layout:'fit', 
            });
            tabPanel.add(tabItem); 

            //延迟放入到tab中
            setTimeout(function(){
                //创建组件
                var item=Ext.widget("baseformtab",{
                    operType:operType,                            
                    controller:otherController,         //指定重写事件的控制器
                    funCode:funCode,                    //指定mainLayout的funcode
                    detCode:detCode,                    //指定detailLayout的funcode
                    tabItemId:tabItemId,                //指定tab页的itemId
                    insertObj:insertObj,                //保存一些需要默认值，提供给提交事件中使用
                    funData: popFunData,                //保存funData数据，提供给提交事件中使用
                    items:[{
                        xtype:detLayout,
                        funCode: detCode, 
                        items: [{
                            xtype: "arrange.roomdetailform",
                            funCode: detCode                  
                        }]
                    }]
                }); 
                tabItem.add(item);  
                
                var objDetForm = item.down("baseform[funCode=" + detCode + "]");
                var formDeptObj = objDetForm.getForm();              

                self.setFormValue(formDeptObj, insertObj);

                //查询班级的学员信息
                self.asyncAjax({
                    url: comm.get("baseUrl")  + "/TrainClasstrainee/getClassRoomTrainees",
                    params: {
                        classId: insertObj.uuid,
                        page:1,
                        start:0,
                        limit:-1    //-1表示不分页
                    },
                    //回调代码必须写在里面
                    success: function(response) {
                        var data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));

                        var rows=data.rows;
                        //console.log(rows);
                        if(rows!=undefined){  //若存在rows数据，则表明请求正常
                            //获取班级学员列表信息
                            var arrangeRoomGrid=item.down("grid[ref=arrangeRoomGrid]");                   
                            arrangeRoomGrid.getStore().loadData(rows);

                        }else{
                            self.Error(data.obj);
                        }
                    }
                });            

            },30);
                           
        }else if(tabItem.itemPKV&&tabItem.itemPKV!=pkValue){     //判断是否点击的是同一条数据
            self.Warning("您当前已经打开了一个编辑窗口了！");
            return;
        }

        tabPanel.setActiveTab( tabItem);
    },


    doArrangeSiteDetail_Tab: function(btn, cmd, grid, record) {

        var self = this;
        var baseGrid;
        var recordData;

        if (btn) {
            baseGrid = btn.up("basegrid");
        } else {
            baseGrid = grid;
            recordData = record.data;
        }
        //得到组件
        var funCode = baseGrid.funCode;
        var basePanel = baseGrid.up("basepanel[funCode=" + funCode +"]");
        var tabPanel=baseGrid.up("tabpanel[xtype=app-main]");

        //得到配置信息
        var funData = basePanel.funData;
        var detCode = "arrange_siteDetail";   //修改此funCode，方便用于捕获window的确定按钮
        var detLayout = basePanel.detLayout;
        var defaultObj = funData.defaultObj;
                
        //关键：window的视图控制器
        var otherController = basePanel.otherController;
        if (!otherController)
            otherController = '';

        //处理特殊默认值
        var insertObj = {};
        var popFunData = Ext.apply(funData, {
            grid: baseGrid
        });

        //根据cmd操作类型，来设置不同的值
        var tabTitle = "场地安排"; 
        //设置tab页的itemId
        var tabItemId=funCode+"_gridSiteDetail";     //命名规则：funCode+'_ref名称',确保不重复
        var pkValue= null;
        var operType="detail";

        switch (cmd) {
            case "edit":

                if (btn) {
                    var rescords = baseGrid.getSelectionModel().getSelection();
                    if (rescords.length != 1) {
                        self.msgbox("请选择一条数据！");
                        return;
                    }
                    recordData = rescords[0].data;
                }

                if(recordData.isuse==2){
                    self.Warning("此班级修改后未提交，请等待提交后再安排！");
                    return false;
                }

                insertObj = recordData;
                tabTitle = "场地安排";
                tabItemId=funCode+"_gridSiteEdit"; 

                //获取主键值
                var pkName = funData.pkName;
                pkValue= recordData[pkName];

                break;
            case "detail":

                if (btn) {
                    var rescords = baseGrid.getSelectionModel().getSelection();
                    if (rescords.length != 1) {
                        self.msgbox("请选择一条数据！");
                        return;
                    }
                    recordData = rescords[0].data;
                }

                insertObj = recordData;
                tabTitle = "查看场地安排";
                tabItemId=funCode+"_gridSiteEdit"+insertObj.classNumb; 
                break;
        }

        //获取tabItem；若不存在，则表示要新建tab页，否则直接打开
        var tabItem=tabPanel.getComponent(tabItemId);
        if(!tabItem){
    
            tabItem=Ext.create({
                xtype:'container',
                title: tabTitle,
                //iconCls: 'x-fa fa-clipboard',
                scrollable :true, 
                itemId:tabItemId,
                itemPKV:pkValue,      //保存主键值
                layout:'fit', 
            });
            tabPanel.add(tabItem); 

            //延迟放入到tab中
            setTimeout(function(){
                //创建组件
                var item=Ext.widget("baseformtab",{
                    operType:operType,                            
                    controller:otherController,         //指定重写事件的控制器
                    funCode:funCode,                    //指定mainLayout的funcode
                    detCode:detCode,                    //指定detailLayout的funcode
                    tabItemId:tabItemId,                //指定tab页的itemId
                    insertObj:insertObj,                //保存一些需要默认值，提供给提交事件中使用
                    funData: popFunData,                //保存funData数据，提供给提交事件中使用
                    items:[{
                        xtype:detLayout,
                        funCode: detCode, 
                        items: [{
                            xtype: "arrange.sitedetailform"
                        }]
                    }]
                }); 
                tabItem.add(item);  

                var objDetForm = item.down("baseform[funCode=" + detCode + "]");
                var formDeptObj = objDetForm.getForm();
                self.setFormValue(formDeptObj, insertObj);
        
                var arrangeRoomGrid=item.down("grid[ref=arrangeSiteGrid]");    
                arrangeRoomGrid.getStore().getProxy().extraParams.filter= "[{'type':'string','comparison':'=','value':'"+ insertObj.uuid+"','field':'classId'}]";
                arrangeRoomGrid.getStore().load();
                
            },30);
                           
        }else if(tabItem.itemPKV&&tabItem.itemPKV!=pkValue){     //判断是否点击的是同一条数据
            self.Warning("您当前已经打开了一个编辑窗口了！");
            return;
        }

        tabPanel.setActiveTab( tabItem);
        
    },


    doClassDetail_Tab:function(btn, cmd, grid, record) {

        var self = this;
        var baseGrid;
        var recordData;

        if (btn) {
            baseGrid = btn.up("basegrid");
        } else {
            baseGrid = grid;
            recordData = record.data;
        }


        //得到组件
        var funCode = baseGrid.funCode;
        var basePanel = baseGrid.up("basepanel[funCode=" + funCode +"]");
        var tabPanel=baseGrid.up("tabpanel[xtype=app-main]");

        //得到配置信息
        var funData = basePanel.funData;
        var detCode =  basePanel.detCode;  
        var detLayout = basePanel.detLayout;
        var defaultObj = funData.defaultObj;
                
        //关键：window的视图控制器
        var otherController = basePanel.otherController;
        if (!otherController)
            otherController = '';

        //处理特殊默认值
        var insertObj = self.getDefaultValue(defaultObj);
        var popFunData = Ext.apply(funData, {
            grid: baseGrid
        });

        //本方法只提供班级详情页使用
        var tabTitle = funData.tabConfig.detailTitle;
        //设置tab页的itemId
        var tabItemId=funCode+"_gridAdd";     //命名规则：funCode+'_ref名称',确保不重复
        var pkValue= null;
        var operType = "detail";    // 只显示关闭按钮
               
        if (btn) {
            var rescords = baseGrid.getSelectionModel().getSelection();
            if (rescords.length != 1) {
                self.msgbox("请选择一条数据！");
                return;
            }
            recordData = rescords[0].data;
        }

        insertObj = recordData;
        tabItemId=funCode+"_gridDetail"+insertObj.classNumb;    //详细界面可以打开多个
        items=[{
            xtype:detLayout,
            defaults:null,
            margin:'0 0 0 10',
            items:[{
                xtype:'arrange.classdetailhtmlpanel'
            }]
        }];
        


        //获取tabItem；若不存在，则表示要新建tab页，否则直接打开
        var tabItem=tabPanel.getComponent(tabItemId);
        if(!tabItem){
    
            tabItem=Ext.create({
                xtype:'container',
                title: tabTitle,
                //iconCls: 'x-fa fa-clipboard',
                scrollable :true, 
                itemId:tabItemId,
                itemPKV:pkValue,      //保存主键值
                layout:'fit', 
            });
            tabPanel.add(tabItem); 

            //延迟放入到tab中
            setTimeout(function(){
                //创建组件
                var item=Ext.widget("baseformtab",{
                    operType:operType,                            
                    controller:otherController,         //指定重写事件的控制器
                    funCode:funCode,                    //指定mainLayout的funcode
                    detCode:detCode,                    //指定detailLayout的funcode
                    tabItemId:tabItemId,                //指定tab页的itemId
                    insertObj:insertObj,                //保存一些需要默认值，提供给提交事件中使用
                    funData: popFunData,                //保存funData数据，提供给提交事件中使用
                    items:items
                }); 
                tabItem.add(item);  
               
                //读取班级信息
                //读取学员信息
                //读取课程信息        
                self.asyncAjax({
                    url: comm.get("baseUrl")  + "/TrainClass/getClassAllInfo",
                    params: {
                        classId: insertObj.uuid
                    },
                    //回调代码必须写在里面
                    success: function(response) {
                        var data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                        if(data.success){
                            var obj=data.obj;
                            var classInfoContainer=tabItem.down("container[ref=classInfo]");
                            classInfoContainer.setData(obj.classInfo);

                            var classTraineeInfoContainer=tabItem.down("container[ref=classTraineeInfo]");
                            classTraineeInfoContainer.setData(obj.classTrainee);

                            var classCourseInfoContainer=tabItem.down("container[ref=classCourseInfo]");
                            classCourseInfoContainer.setData(obj.classCourse);
                            
                            var classFoodInfoContainer=tabItem.down("container[ref=classFoodInfo]");                                        
                            var classFoodObj={};
                            classFoodObj.dinnerType=obj.classInfo.dinnerType;
                            if(obj.classInfo.dinnerType==1)
                                classFoodObj.dinnerTypeName="围餐";
                            else if(obj.classInfo.dinnerType==2)
                                classFoodObj.dinnerTypeName="自助餐";
                            else 
                                classFoodObj.dinnerTypeName="快餐";
                            classFoodObj.avgNumber=obj.classInfo.avgNumber;
                            classFoodObj.breakfastStand=obj.classInfo.breakfastStand;
                            classFoodObj.breakfastCount=obj.classInfo.breakfastCount;
                            classFoodObj.lunchStand=obj.classInfo.lunchStand;
                            classFoodObj.lunchCount=obj.classInfo.lunchCount;
                            classFoodObj.dinnerStand=obj.classInfo.dinnerStand;
                            classFoodObj.dinnerCount=obj.classInfo.dinnerCount;
                            //默认为快餐，则显示学员数据   
                            if(obj.classInfo.dinnerType!="1" && obj.classInfo.dinnerType!="2"){                 

                                //查询班级的就餐信息
                                self.asyncAjax({
                                    url: comm.get("baseUrl")  + "/TrainClasstrainee/getClassFoodTrainees",
                                    params: {
                                        classId: insertObj.uuid,
                                        page:1,
                                        start:0,
                                        limit:-1    //-1表示不分页
                                    },
                                    //回调代码必须写在里面
                                    success: function(response) {
                                        var data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                                        var rows=data.rows;
                                        if(rows!=undefined){ //若存在rows数据，则表明请求正常
                                            classFoodObj.rows=rows;                                   
                                            classFoodInfoContainer.setData(classFoodObj);  
                                        }else{
                                            self.Error(data.obj);
                                        }
                                    }
                                });  
                            }else{
                                classFoodInfoContainer.setData(classFoodObj);   
                            }      
                                
                                                         
                            //查询班级的住宿信息
                            self.asyncAjax({
                                url: comm.get("baseUrl")  + "/TrainClasstrainee/getClassRoomTrainees",
                                params: {
                                    classId: insertObj.uuid,
                                    page:1,
                                    start:0,
                                    limit:-1    //-1表示不分页
                                },
                                //回调代码必须写在里面
                                success: function(response) {
                                    var data = Ext.decode(Ext.valueFrom(response.responseText, '{}'));
                                    var rows=data.rows;
                                    //console.log(rows);
                                    if(rows!=undefined){  //若存在rows数据，则表明请求正常
                                        var classRoomInfoContainer=tabItem.down("container[ref=classRoomInfo]");  
                                        classRoomInfoContainer.setData(rows);     
                                    }else{
                                        self.Error(data.obj);
                                    }
                                }
                            });  
                        }else{
                            self.Error(data.obj);
                        }
                       
                    }
                });                                    
                
               

            },30);
                           
        }else if(tabItem.itemPKV&&tabItem.itemPKV!=pkValue){     //判断是否点击的是同一条数据
            self.Warning("您当前已经打开了一个编辑窗口了！");
            return;
        }

        tabPanel.setActiveTab( tabItem);
        
        
    },
});
