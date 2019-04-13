app.service("addressService",function ($http) {

    //查询所有
    this.findAddressList=function () {
        return $http.get("address/findListByLoginUser.do");
    }

    //根据id查询
   this.findOne=function (id) {
        return $http.get('address/findOne.do?id='+id);
    }

    //删除
   this.delete=function (id) {
       return $http.get('address/delete.do?id='+id);
   }

    //增加
    this.add=function(address){
        return  $http.post('../address/add.do',address );
    }
    //修改
    this.update=function(address){
        return  $http.post('../address/update.do',address );
    }

    this.install=function (id) {
        return $http.get('address/install.do?id='+id);
    }
})