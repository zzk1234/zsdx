/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('core.main.model.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main.mainModel',

    data: {
        name: '学员综合服务系统',
        currentUser:'管理员',

       //currentDateWeek:Ext.Date.format(new Date(), 'Y年n月j日 D'),
       	onlineNum:66,

        // 系统菜单的定义，这个菜单可以是从后台通过ajax传过来的  
		systemMenu:'null',

        //我的桌面菜单
        myDeskMenu:[{
            "parent": "ROOT",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/index_shiziguanli.png",
            "level": 2,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,2e0300b2-12e8-4fed-98d6-65ba7fc5ae4b",
            "menuTarget": "teacher.mainlayout,core.train.teacher.controller.MainController",
            "children": [],
            "menuCode": "TRAINTEACHER",
            "orderIndex": 1,
            "menuType": "FUNC",
            "smallIcon": "icon_shiziguanli",
            "id": "2e0300b2-12e8-4fed-98d6-65ba7fc5ae4b",
            "text": "师资管理",
            "iconCls": "",
            "issystem": 1
        },
        {
            "parent": "32f4ad13-8e49-4a58-b0d7-d27e4f71101b",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/index_kechengguanli.png",
            "level": 3,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,32f4ad13-8e49-4a58-b0d7-d27e4f71101b,5d032d88-8ee1-4e27-bb5c-a31c9aa63997",
            "menuTarget": "course.mainlayout,core.train.course.controller.MainController",
            "children": [],
            "menuCode": "COURSELIST",
            "orderIndex": 2,
            "menuType": "FUNC",
            "smallIcon": "icon_kechengguanli",
            "id": "5d032d88-8ee1-4e27-bb5c-a31c9aa63997",
            "text": "课程列表",
            "iconCls": "",
            "issystem": 1
        },
        {
            "parent": "ROOT",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/index_xueyuanguanli.png",
            "level": 2,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,a75e038f-da6f-4353-b92c-22c8888a6d39",
            "menuTarget": "trainee.mainlayout,core.train.trainee.controller.MainController",
            "children": [],
            "menuCode": "TRAINEE",
            "orderIndex": 3,
            "menuType": "FUNC",
            "smallIcon": "icon_xueyuanguanli",
            "id": "a75e038f-da6f-4353-b92c-22c8888a6d39",
            "text": "学员管理",
            "iconCls": "",
            "issystem": 1
        },
        {
            "parent": "db1a7814-9928-42cf-911a-4924ccc48d75",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/index_banjiguanli.png",
            "level": 2,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,717531c8-0437-4dbc-a36d-81109bb2de9c",
            "menuTarget": "class.mainlayout,core.train.class.controller.MainController",
            "children": [],
            "menuCode": "TRAINCLASS",
            "orderIndex": 1,
            "menuType": "FUNC",
            "smallIcon": "icon_banjiguanli",
            "id": "717531c8-0437-4dbc-a36d-81109bb2de9c",
            "text": "班级管理",
            "iconCls": "",
            "issystem": 1
        },
        {
            "parent": "db1a7814-9928-42cf-911a-4924ccc48d75",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/peixunanpai.png",
            "level": 2,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,5d65c9b9-424c-48f0-a1de-834e88d370af",
            "menuTarget": "arrange.mainlayout,core.train.arrange.controller.MainController",
            "children": [],
            "menuCode": "TRAINARRANGE",
            "orderIndex": 2,
            "menuType": "FUNC",
            "smallIcon": "icon_peixunanpai",
            "id": "5d65c9b9-424c-48f0-a1de-834e88d370af",
            "text": "培训安排",
            "iconCls": "",
            "issystem": 1
        },
        {
            "parent": "db1a7814-9928-42cf-911a-4924ccc48d75",
            "menuLeaf": "LEAF",
            "bigIcon": "/static/core/resources/images/icon/index_kechenkaoqin.png",
            "level": 2,
            "nodeCode": "",
            "leaf": true,
            "isHidden": "0",
            "treeid": "db1a7814-9928-42cf-911a-4924ccc48d75,22b334e9-9685-41fb-8baa-a48abf577769",
            "menuTarget": "coursechkresult.mainlayout,core.train.coursechkresult.controller.MainController",
            "children": [],
            "menuCode": "TRAINATTEND",
            "orderIndex": 3,
            "menuType": "FUNC",
            "smallIcon": "icon_kechenkaoqin",
            "id": "22b334e9-9685-41fb-8baa-a48abf577769",
            "text": "培训考勤",
            "iconCls": "",
            "issystem": 1
        }],

        menuType:{
            value:'mainmenu'
        },
        headerType:{
            value:'mainbigheader'
        }        
    },

    //根据data值来计算 获取数据
    formulas:{
        isMainMenu:function(get){
            return get('menuType.value')=='mainmenu';
        },

        isMainMenuTree:function(get){
            return get('menuType.value')=='mainmenutree';
        },
        isMainBigHeader:function(get){
            return get('headerType.value')=='mainbigheader';
        },

        isMainSmallHeader:function(get){
            return get('headerType.value')=='mainsmallheader';
        },

        currentDateWeek:function(get){
        	return Ext.Date.format(new Date(), 'Y年n月j日 D');
        },

    },

    //TODO - add data, formulas and/or methods to support your view
    // 根据data.systemMenu生成菜单条和菜单按钮下面使用的菜单数据  
	getMenus : function() {  
	    var items = [];  
	    var menuData = this.get('systemMenu'); // 取得定义好的菜单数据  
	    Ext.Array.each(menuData, function(group) { // 遍历菜单项的数组  
            var submenu = [];  
            // 对每一个菜单项，遍历菜单条的数组  
            Ext.Array.each(group.items, function(menuitem) {  
                        submenu.push({  
                                    mainmenu : 'true',  
                                    moduleName : menuitem.module,  
                                    text : menuitem.text,  
                                    iconCls : menuitem.iconCls,                              
                                    handler : 'onMainMenuClick' // MainController中的事件处理程序  
                                })  
                    })  
            var item = {  
                text : group.text,  
                menu : submenu,  
                iconCls : group.iconCls
            };  
            items.push(item);  
        })  
	    return items;  
	}  
});
