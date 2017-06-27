Ext.define("core.oa.meeting.meetinginfo.controller.MainController", {
    extend: "Ext.app.ViewController",
    alias: 'controller.meetinginfo.mainController',
    mixins: {
        suppleUtil: "core.util.SuppleUtil",
        messageUtil: "core.util.MessageUtil",
        formUtil: "core.util.FormUtil",
        gridActionUtil: "core.util.GridActionUtil",
        dateUtil: 'core.util.DateUtil'
    },
    init: function() {
    },
    control: {
        "panel[xtype=meetinginfo.maingrid] button[ref=sync]": {
            beforeclick: function(btn) {
            	 var resObj = this.ajax({
                     url: "/meetingsync" + "/meeting"
                 });
                 if (resObj.success) {
                	 this.msgbox("同步成功!");
                	 btn.up("basegrid").getStore().load();
                 }else{
                	 //self.msgbox("同步成功!");
                	 this.msgbox("同步失败！");
                 }
                 return false;
            }
        },
        
        //导入
        "basegrid[xtype=meetinginfo.maingrid] button[ref=gridImport]": {
            beforeclick: function (btn) {
                var self = this;

                //判断是否选择了班级，判断是添加新班级 或是 编辑班级

                //得到组件
                var baseGrid = btn.up("basegrid");

                var win = Ext.create('Ext.Window', {
                    title: "导入会议数据",
                    iconCls: 'x-fa fa-clipboard',
                    width: 400,
                    resizable: false,
                    constrain: true,
                    autoHeight: true,
                    modal: true,
                    controller: 'meetinginfo.otherController',
                    closeAction: 'close',
                    plain: true,
                    grid: baseGrid,
                    items: [{
                        xtype: "meetinginfo.meetingimportform"
                    }]
                });
                win.show();
                return false;
            }
        },
        
        /**
         * 导出
         */
        "basegrid[xtype=meetinginfo.maingrid] button[ref=gridExport]": {
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
                var title = "将导出所有的会议信息";
                var ids = new Array();
                if (records.length > 0) {
                    title = "将导出所选会议的信息";
                    Ext.each(records, function (rec) {
                        var pkValue = rec.get(pkName);
                        ids.push(pkValue);
                    });

                }
                Ext.Msg.confirm('提示', title, function (btn, text) {
                    if (btn == "yes") {
                        Ext.Msg.wait('正在导出中,请稍后...', '温馨提示');
                        window.location.href = comm.get('baseUrl') + "/OaMeeting/exportExcel?ids=" + ids.join(",");
                        /*                  Ext.create('Ext.panel.Panel', {
                         title: 'HelloWorld',
                         width: 200,
                         html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="' + comm.get('baseUrl') + '/TrainTeacher/exportExcel?ids=' + ids.join(",") + '"></iframe>',
                         renderTo: Ext.getBody()
                         });*/
                        // var task = new Ext.util.DelayedTask(function () {
                        //     Ext.Msg.hide();
                        // });
                        Ext.Ajax.request({
                            url: comm.get('baseUrl') + "/OaMeeting/exportExcel?ids=" + ids.join(","),
                            success: function (resp, opts) {
                                Ext.Ajax.request({
                                    url: comm.get('baseUrl') + '/OaMeeting/checkExportEnd',
                                    success: function (resp, opts) {
                                        //task.delay(1200);
                                        Ext.Msg.hide();
                                        Ext.Msg.alert("消息","导出数据成功");
                                    },
                                    failure: function (resp, opts) {
                                        task.delay(1200);
                                        var respText = Ext.util.JSON.decode(resp.responseText);
                                        Ext.Msg.alert('错误', respText.error);
                                    }
                                });
                            },
                            failure: function (resp, opts) {
                                task.delay(1200);
                                //var respText = Ext.util.JSON.decode(resp.responseText);
                                Ext.Msg.alert('错误');
                            }
                        });
                        
//                        Ext.Ajax.request({
//                            url: comm.get('baseUrl') + '/OaMeeting/checkExportEnd',
//                            success: function (resp, opts) {
//                                //task.delay(1200);
//                                Ext.Msg.hide();
//                            },
//                            failure: function (resp, opts) {
//                                task.delay(1200);
//                                var respText = Ext.util.JSON.decode(resp.responseText);
//                                Ext.Msg.alert('错误', respText.error);
//                            }
//                        });

                    }
                });
                return false;
            }
        },
    	
    
        "basegrid button[ref=gridAdd_Tab]": {
            beforeclick: function(btn) {
                console.log(btn);
                //return false;
            }
        },

        "basegrid button[ref=gridDetail_tab]": {
            beforeclick: function(btn) {
                console.log(btn);
                //return false;
            }
        },

        "basegrid button[ref=gridEdit_tab]": {
            beforeclick: function(btn) {
                console.log(btn);
                //return false;
            }
        },

        "basegrid  actioncolumn": {
            editClick: function(data) {
                console.log(data);

            },
            detailClick: function(data) {
                console.log(data);

            },
            deleteClick: function(data) {
                console.log(data);

            },
        }
    }   
});