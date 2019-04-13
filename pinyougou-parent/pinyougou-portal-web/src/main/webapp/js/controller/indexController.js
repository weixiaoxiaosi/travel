app.controller("indexController",function ($scope,contentService) {
    $scope.contentList=[];
    contentService.findByCategoryId(1).success(function (response) {
        $scope.contentList[1]=response
    })

    $scope.search=function () {

        location.href="http://localhost:8086/search.html#?keywords="+$scope.keywords;

        alert($scope.keywords)
        
    }
})