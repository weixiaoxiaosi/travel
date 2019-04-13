app.controller("cartController",function ($scope, cartService,addressService) {
    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;

            $scope.totalValue ={totalNum:0,totalMoney:0};
            //求合计数
            for (var i = 0; i < response.length;i++){
                var cart = response[i]
                for (var j = 0 ;j< cart.orderItemList.length;j++) {
                    $scope.totalValue.totalMoney += cart.orderItemList[j].totalFee;
                    $scope.totalValue.totalNum += cart.orderItemList[j].num;
                }
            }
        })
    }
    
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();//刷新列表
            }else{
                alert(response.message);//弹出错误提示
            }
        })
    }

    //获取地址列表
    $scope.findAddressList=function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addressList=response;

                //设置默认地址
                for (var i = 0 ; i <response.length ; i ++){
                    if (response[i].isDefault =='1'){
                        $scope.address=response[i];
                        break;
                    }
                } 
            }
        );
    };

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }

    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    //选择支付方式
    $scope.order={paymentType:'1'};
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
        
    }

    //保存订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
                //页面跳转
                if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                    window.location.href="pay.html"
                }else {//如果货到付款，跳转到提示页面
                    window.location.href="payfail.html"
                }
            }else {
                alert(response.message);   //也可以跳转到提示页面
            }
        })
    }

  //根据id查询地址回显编辑
    $scope.find0ne=function (id) {
        addressService.findOne(id).success(function (response) {
            $scope.address=response;
        })
    }

   $scope.delete=function (id) {
       addressService.delete(id).success(function (response) {
           alert(response.message)
       })
   }

   $scope.empty=function(){
       $scope.address={};
   }
    $scope.save=function () {
        var serviceObject;//服务层对象
        if($scope.address.id!=null){//如果有ID
            alert($scope.address.id)
            serviceObject=addressService.update( $scope.address); //修改
        }else{
            serviceObject=addressService.add( $scope.address );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findAddressList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.install=function (id) {
        addressService.install(id).success(function (response) {
            if(response.success){
                //重新查询
                $scope.findAddressList();//重新加载
            }else{
                alert(response.message);
            }
        })
    }
})