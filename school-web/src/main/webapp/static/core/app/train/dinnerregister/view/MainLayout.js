Ext.define("core.train.dinnerregister.view.MainLayout", {
    extend: "core.base.view.BasePanel",
    alias: "widget.dinnerregister.mainlayout",
    /** 引入必须的文件 */
    requires: [
        'core.train.dinnerregister.controller.MainController'
    ],
    /** 关联此视图控制器 */
    controller: 'dinnerregister.mainController',

    /** 页面代码定义 */
    funCode: "dinnerregister_main",
    detCode: "dinnerregister_detail",
    detLayout: "dinnerregister.detaillayout",

    /*标注这个视图控制器的别名，以此提供给window处使用*/
    otherController: 'dinnerregister.otherController',

    layout: 'fit',
    border: false,
    funData: {
        action: comm.get("baseUrl") + "/TrainClassrealdinner", //请求Action
        whereSql: "", //表格查询条件
        orderSql: "", //表格排序条件
        filter: "",
        pkName: "uuid",
        width: 1000,     //定义弹出window的宽度
        height: 600,    //定义弹出window的高度
        defaultObj: {},
        tabConfig:{         //zzk：2017-6-1加入，用于对tab操作提供基本配置数据
            addTitle:'添加班级',
            editTitle:'编辑班级',
            detailTitle:'班级详情'
        }
    },

    /*设置最小宽度，并且自动滚动*/
    minWidth:1200,
    scrollable:true,

    items: [{
        xtype: "dinnerregister.mainform",   
    }]
});
