app.controller("payController",function ($scope,$location, payServices) {
    //本地生成二维码
    $scope.createNative=function () {
        payServices.createNative().success(function (response) {
            //总金额金额
            $scope.money = (response.total_fee/100).toFixed(2);
            //订单号
            $scope.out_trade_no=response.out_trade_no;
            //二维码
            var q = new QRious({
                element:document.getElementById('qrious'),
                size:260,
                level:'Q',
                value:response.code_url
            })
            queryPayStatus(response.out_trade_no);//查询支付状态				
        })
    }
    //查询支付状态 
    queryPayStatus=function (out_trade_no) {
        payServices.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
                if (response.message=='二维码超时'){
                    $scope.createNative();//重新生成二维码
                }else {
                    location.href="payfail.html";
                }
            }
        })
    }
    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }

})