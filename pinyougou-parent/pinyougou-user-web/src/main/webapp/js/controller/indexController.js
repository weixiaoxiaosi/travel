app.controller("indexController",function ($scope, loginService) {
    $scope.show=function () {
        loginService.showName().success(function (response) {
            $scope.loginName=response.loginName;
        })
    }
})