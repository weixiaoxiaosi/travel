app.service("seckillGoodsService",function ($http) {

    /**
     * 读取列表数据绑定到表单中
     * @returns {*}
     */
    this.findList=function () {
        return $http.get('seckillGoods/findList.do')
    }

    /**
     * 根据ID获取实体
     * @param id
     * @returns {*}
     */
    this.findOne=function (id) {
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }

    /**
     * 提交订单
     * @param id
     * @returns {*}
     */
    this.submitOrder=function (seckillId) {
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
})