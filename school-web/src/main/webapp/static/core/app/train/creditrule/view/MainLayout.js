Ext.define("core.train.creditrule.view.MainLayout", {
    extend: "core.base.view.BasePanel",
    alias: "widget.creditrule.mainlayout",
    /** 引入必须的文件 */
    requires: [ 
        'core.train.creditrule.controller.MainController',
        //'core.train.creditrule.model.MainGridModel',
        //'core.train.creditrule.store.MainGridStore', 
        //'core.train.creditrule.view.MainGrid',
        //'core.train.creditrule.view.MainQueryPanel',
        "core.train.creditrule.view.DetailLayout",
    ],    
    /** 关联此视图控制器 */
    controller: 'creditrule.mainController',
    /** 页面代码定义 */
    funCode: "creditrule_main",
    detCode: "creditrule_detail",
    detLayout: "creditrule.detaillayout",
    /*标注这个视图控制器的别名，以此提供给window处使用*/
    otherController:'creditrule.otherController',
    layout:'border',
    border:false,
    funData: {
        action: comm.get("baseUrl") + "/TrainCreditsrule", //请求Action
        whereSql: "", //表格查询条件
        orderSql: "", //表格排序条件
        filter: "",
        pkName: "uuid",
        width: 800,
        height: 350,
        defaultObj: {},
    },

    /*设置最小宽度，并且自动滚动*/
    minWidth:1200,
    scrollable:true,

    items: [{
        xtype: "creditrule.maingrid",
        region: "center"
    }]
})
