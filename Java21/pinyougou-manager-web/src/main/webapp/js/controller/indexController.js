app.controller("indexController", function ($scope, loginService) {

  // 获取登陆账户的用户名
  $scope.showLoginName = function () {
    loginService.loginName().success(
        function (response) {
          $scope.loginName = response.loginName;

        });
  }

});