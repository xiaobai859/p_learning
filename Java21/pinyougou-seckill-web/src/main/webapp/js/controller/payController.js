app.controller("payController", function ($scope, $location, payService) {

  // 生成二维码
  $scope.createNative = function () {
    payService.createNative().success(
        function (response) {

          //显示订单号和金额
          $scope.money = (response.total_fee / 100).toFixed(2);
          console.log($scope.money);
          $scope.out_trade_no = response.out_trade_no;

          //生成二维码
          var temp = {
            element: document.getElementById('qrious'),
            size: 250,
            value: response.code_url,
            level: 'H'
          };
          var qr = new QRious(temp);

          queryPayStatus();//调用查询

        }
    );
  }

  // 查询支付状态
  queryPayStatus = function () {
    payService.queryPayStatus($scope.out_trade_no).success(
        function (response) {
          if (response.success) {
            location.href = "paysuccess.html#?money=" + $scope.money;
          } else {
            if (response.message == "二维码超时") {
              // $scope.createNative(); // 重新生成二维码
              alert("二维码超时");
            } else {
              location.href = "payfail.html";
            }
          }
        }
    );
  }

  $scope.getMoney = function () {
    return $location.search()["money"];
  }

});