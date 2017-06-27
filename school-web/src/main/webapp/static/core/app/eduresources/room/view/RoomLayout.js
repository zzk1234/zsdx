Ext.define("core.eduresources.room.view.RoomLayout", {
    extend: "core.base.view.BasePanel",
    alias: 'widget.room.roomdetaillayout',
    funCode: "room_roomdetail",
    funData: {
        action: comm.get('baseUrl') + "/BuildRoominfo", //请求Action
        whereSql: "", //表格查询条件
        whereSql: "", //表格查询条件
        orderSql: "", //表格排序条件
        pkName: "uuid",
        defaultObj: {
            orderIndex: 1,
            roomType:'3'
            
        }
    },
    layout: 'fit',
    bodyPadding: 2,
    items: [{
        xtype: "room.RoomForm"
    }]
})