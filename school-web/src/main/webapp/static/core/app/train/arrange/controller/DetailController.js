/**
    ( *非必须，只要需要使用时，才创建他 )
    此视图控制器，提供于DeatilLayout范围内的界面组件注册事件
*/
Ext.define("core.train.arrange.controller.DetailController", {
    extend: "Ext.app.ViewController",
    alias: 'controller.arrange.detailController',
    mixins: {

        suppleUtil: "core.util.SuppleUtil",
        messageUtil: "core.util.MessageUtil",
        formUtil: "core.util.FormUtil",
        gridActionUtil: "core.util.GridActionUtil",
        dateUtil: 'core.util.DateUtil'

    },
    init: function() {
        /*执行一些初始化的代码*/
        //console.log("初始化 detail controler");
    },
    /** 该视图内的组件事件注册 */
    control: {    
        "grid[ref=arrangeRoomGrid]  button[ref=gridSetRoom]": {
            beforeclick: function(btn) {
                
                var self = this;
            
                var baseGrid = btn.up("grid[ref=arrangeRoomGrid]");
                
                var basetab = btn.up('baseformtab');
                var funCode = basetab.funCode;      //mainLayout的funcode
                var detCode = basetab.detCode;      //detailLayout的funcode

                var basePanel = basetab.down("basepanel[funCode=" + detCode + "]");            
                var objForm = basePanel.down("baseform[funCode=" + detCode + "]");
                var formObj = objForm.getForm();
                var classId = formObj.findField("uuid").getValue();
                //var className = formObj.findField("className").getValue();

            
                var records = baseGrid.getSelectionModel().getSelection();
                if (records.length == 0) {
                    self.msgbox("请选择需要设置房间的学员！");
                    return false;
                }

                var ids=[];
                var xbm=0;
                for(var i in records) {
                    var xbmTemp= records[i].get("xbm");       
                    if(xbm!=0&&xbmTemp!=xbm){
                        self.Warning("性别必须一致！");
                        return false;
                    }
                    xbm=xbmTemp;

                    ids.push(records[i].get("uuid"));
                };

                            
                //关键：window的视图控制器
                var otherController ='arrange.otherController';
            
                var insertObj = {
                    classId:classId, 
                    ids:ids,
                    xbm:xbm
                };

                var popFunData = Ext.apply(basePanel.funData, {
                    grid: baseGrid
                });

                var width = 1200;
                var height = 600;      

                var iconCls = 'x-fa fa-plus-circle';
                var operType = "edit";
                var title = "选择宿舍";                        

                var win = Ext.create('core.base.view.BaseFormWin', {
                    title: title,
                    iconCls: iconCls,
                    operType: operType,
                    width: width,
                    height: height,
                    controller: otherController,
                    funData: popFunData,
                    funCode: "arrangeroom_detail",    //修改此funCode，方便用于捕获window的确定按钮
                    insertObj: insertObj,
                    items: [{
                        xtype:'room.mainlayout',
                        bodyPadding:5,
                        items: [{
                            collapsible: true,
                            split: true,
                            xtype: "room.areagrid",                      
                            tbar:null,
                            region: "west",
                            style:{
                                border: '1px solid #ddd'
                            },
                            width:450
                        }, {
                            xtype: "room.RoomGrid",                          
                            tbar:null,
                            style:{
                                border: '1px solid #ddd'
                            },
                            selModel: {
                                type: "checkboxmodel",   
                                headerWidth:50,    //设置这个值为50。 但columns中的defaults中设置宽度，会影响他
                                mode:'single', 
                            },
                            region: "center"
                        }]
                    }]
                });
                win.show();                
                return false;
            }
        },
    }
});
