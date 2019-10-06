// 购物车前端控制层
app.controller("cartController", function ($scope, cartService) {

  // 查询购物车列表
  $scope.findCartList = function () {
    cartService.findCartList().success(
        function (resopnse) {
          $scope.cartList = resopnse;
          $scope.totalValue = cartService.sum($scope.cartList); // 获得累计数量和累计金额
        }
    );
  }

  // 添加商品到购物车
  $scope.addGoodsToCartList = function (itemId, num) {
    cartService.addGoodsToCartList(itemId, num).success(
        function (response) {
          if (response.success) {
            $scope.findCartList(); // 添加成功，刷新购物车列表					
          } else {
            alert(response.message);
          }
        }
    );
  }

  // 查询收货地址列表
  $scope.findAddressList = function () {
    cartService.findAddressList().success(
        function (response) {
          $scope.addressList = response;
          // 设置默认收货地址
          for (var i = 0; i < $scope.addressList.length; i++) {
            if ($scope.addressList[i].isDefault == "1") {
              $scope.address = $scope.addressList[i];
              break;
            }
          }
        }
    );
  }

  // 地址选中
  $scope.selectAddress = function (address) {
    $scope.address = address;
  }

  // 判断是否选中
  $scope.isSelectedAddress = function (address) {
    if ($scope.address == address) {
      return true;
    } else {
      return false;
    }
  }

  $scope.addAddress = {};
  // 添加收货地址
  $scope.saveAddress = function () {
    console.log($scope.addAddress);
    cartService.addAddress($scope.addAddress).success(
        function (response) {
          if (response.success) { // 添加成功刷新购物车列表
            $scope.findAddressList();
          } else {
            alert(response.message);
          }
        }
    );
  }

  $scope.order = {paymentType: '1'};	//order 订单信息 保存到订单表中
  //选择支付方式
  $scope.selectPayType = function (type) {
    $scope.order.paymentType = type;
  }

  // 提交订单
  $scope.submitOrder = function () {

    $scope.order.receiverAreaName = $scope.address.address;//地址
    $scope.order.receiverMobile = $scope.address.mobile;//手机
    $scope.order.receiver = $scope.address.contact;//联系人

    cartService.submitOrder($scope.order).success(
        function (response) {
          if (response.success) { // 成功页面跳转
            // alert(response.message);
            if ($scope.order.paymentType == "1") {
              location.href = "pay.html";
            } else {
              location.href = "paysuccess.html";
            }
          } else { // 失败 弹出提示
            alert(response.message);
          }
        }
    );
  }

});