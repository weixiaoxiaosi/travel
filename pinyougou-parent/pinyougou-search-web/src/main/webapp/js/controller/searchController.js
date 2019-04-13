app.controller("searchController", function ($scope,$location ,searchService) {

    /**
     *
     * @type {{keywords: string, category: string, brand: string, spec: {},
     *      price: string, pageNo: number, pageSize: number}}
     *
     */

    $scope.searchMap = {
        'keywords': '', 'category': '', 'brand': '', 'spec': {}, 'price': '',
        'pageNo': 1, 'pageSize': 40,'sortField':'','sort':''
    }


    //加载查询字符串
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            //搜索返回的结果
            $scope.resultMap = response;

            buildPageLabel();
        })
    }
    /*
        * 分页标签实现思路（最多显示5页）:{
            如果总页数 <= 5{
            显示所有页码
             }
            如果总页数 > 5{
            显示当前页为中心的5个页码[8   9   10   11   12]
            如果当前页码 <= 3{
            显示前5页[1   2   3   4   5]
            }
            如果当前页码 >= (总页数-2)[总共100页,当前页99]{
            显示后5页[96   97   98   99   100]
            }
            }
        }*/

    buildPageLabel = function () {
        //总共有多少页
        $scope.pageLable = [];

        var firstPage = 1;//开始页码
        var lastPage = $scope.resultMap.totalPageas;//结束页码

        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        //总页数大于5式,计算页面标签逻辑
        if ($scope.resultMap.totalPages > 5) {

            $scope.firstDot=false;//前面没有点

            //如果当前页在前三页
            if ($scope.searchMap.pageNo <= 3) {

                lastPage = 5;
            } else if ($scope.searchMap.pageNo >= ($scope.resultMap.totalPages - 3)) {
                firstPage = $scope.resultMap.totalPages - 4;
                lastPage =$scope.resultMap.totalPages;
                $scope.lastDot=false;//后边没点
            } else {

                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

            }

        }else {
            $scope.firstDot=false;//前面没有点
            $scope.lastDot=false;//后边没有点

        }

        for (var i = firstPage; i <= lastPage; i++) {

            $scope.pageLable.push(i)

        }

    }


    $scope.queryByPage = function (pageNo) {

        //输入框返回值需要转化成int类型才能有效
        pageNo = parseInt(pageNo)

        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {

            alert("请正确输入页码");
            return;

        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }

   /* /!**
     * 判断当前页是否是第一页
     * @returns {boolean}
     *!/
    $scope.isTopPage=function(){

        if ($scope.searchMap.pageNo == 1){
            return true;
        }else {
            return false;
        }
    }

    /!**
     * 判断当前页是否是最后一页
     * @returns {boolean}
     *!/

    $scope.isEndPage=function(){

        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }*/

    /**
     * 添加搜索项
     * @param key
     * @param value
     */
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        $scope.search();//执行搜索
    }

    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    }



    //设置排序
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }
    
    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0 ;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >=0){
                return true;
            }
        }
        return false;
    }


})
