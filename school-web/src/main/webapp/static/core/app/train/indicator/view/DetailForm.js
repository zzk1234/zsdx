Ext.define("core.train.indicator.view.DetailForm", {
    extend: "core.base.view.BaseForm",
    alias: "widget.indicator.detailform",
    xtype: 'indicatordeailform',
    layout: "form", //从上往下布局
    autoHeight: true,
    frame: false,
    //bodyPadding: '10 15 10 5',
    fieldDefaults: { // 统一设置表单字段默认属性
        labelSeparator: "：", // 分隔符
        msgTarget: "qtip",
        labelWidth: 120,
        labelAlign: "right"
    },
    items: [{
        fieldLabel: "主键",
        name: "uuid",
        xtype: "textfield",
        hidden: true
    }, {
        xtype: 'fieldset',
        title: '指标信息',
        defaultType: 'textfield',
        border:1,
        style: {
            backgroundColor: '#f5f5f5',
            fontSize:'14px',
            fontFamily: '微软雅黑',
            fontWeight:800,
            color:'#C3190C',
            borderColor: '#C6CBD6',
            borderStyle: 'solid'
        },
        defaults: {anchor: '100%'},
        layout: 'column',
        items: [{
            beforeLabelTextTpl: comm.get("required"),
            allowBlank: false,
            columnWidth:0.4,
            blankText: "指标名称不能为空",
            fieldLabel: "指标名称",
            name: "indicatorName",
            xtype: "textfield",
            emptyText: "请输入指标名称",
            maxLength: 64,
            maxLengthText: "最多64个字符,汉字占2个字符"
        }, {
            fieldLabel: "评价类型",
            width:800,
            //name: "indicatorObject",
            xtype: 'radiogroup',
            ref:'indicatorObject',
/*            columns: 1,
            vertical: true,*/
            items: [
                { boxLabel: '课程评价', name: 'indicatorObject', inputValue: '1', checked: true },
                { boxLabel: '管理评价', name: 'indicatorObject', inputValue: '2' },
                { boxLabel: '课程/管理评价', name: 'indicatorObject', inputValue: '3' }
            ]
/*            xtype: "textfield",
            emptyText: "请输入评估对象",
            maxLength: 128,
            maxLengthText: "最多128个字符,汉字占2个字符"*/
        }]
    },{
        xtype: 'fieldset',
        title: '指标评价标准',
        border:1,
        style: {
            fontSize:'14px',
            fontFamily: '微软雅黑',
            fontWeight:800,
            color:'#C3190C',
            borderColor: '#C6CBD6',
            borderStyle: 'solid'
        },
        items:[{
            xtype:'indicator.standgrid',
            height:600
        }]
    }
        /*{
         xtype: "container",
         layout: "column",
         labelAlign: "right",
         items: [{
         beforeLabelTextTpl: comm.get("required"),
         allowBlank: false,
         blankText: "指标名称不能为空",
         fieldLabel: "指标名称",
         columnWidth: 1,
         name: "indicatorName",
         xtype: "textfield",
         emptyText: "请输入指标名称",
         maxLength: 64,
         maxLengthText: "最多64个字符,汉字占2个字符"
         }]
         }, {
         xtype: "container",
         layout: "column",
         labelAlign: "right",
         items: [{
         fieldLabel: "评估对象",
         columnWidth: 1,
         name: "position",
         xtype: "textfield",
         emptyText: "请输入评估对象",
         maxLength: 128,
         maxLengthText: "最多128个字符,汉字占2个字符"
         }]
         }, {
         xtype: "container",
         layout: "column",
         items: [{
         height: 600,
         columnWidth: 1,
         xtype: "indicator.standgrid",

         }]
         }*/]
});
               
