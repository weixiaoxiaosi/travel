app.service("cartService",function ($http) {

    /**
     * 查询购物车列表
     * @returns {*}
     */
    this.findCartList=function () {
        return $http.get("cart/findCartList.do")
    }
    /**
     * //添加商品到购物车
     * @param itemId
     * @param num
     * @returns {*}
     */
    this.addGoodsToCartList=function (itemId,num) {

        return $http.get("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }


})