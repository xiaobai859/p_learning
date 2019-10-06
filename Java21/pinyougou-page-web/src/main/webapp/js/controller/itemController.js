app.controller("itemController", function ($scope, $http) {

  // $scope.num = 1; 页面init初始化了，这里无需设置

  $scope.specificationItems = {"": "", "": ""}; // 记录用户选择的规则

  // 购物车增减
  $scope.addNum = function (x) {

    $scope.num += x;
    if ($scope.num < 1) {
      $scope.num = 1;
    }
  }

  // 用户选择规格
  $scope.selectSpecification = function (key, value) {
    $scope.specificationItems[key] = value;
    searchSku(); // 读取sku
  }

  // 判断规格是否被选中
  $scope.isSelected = function (key, value) {
    if ($scope.specificationItems[key] == value) {
      return true;
    } else {
      return false;
    }
  }

  // 加载默认SKU
  $scope.loadSku = function () {
    $scope.sku = skuList[0];
    $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec)); // 深克隆
  }

  // 匹配对象是否相等
  matchObject = function (map1, map2) {

    for (var key in map1) {
      if (map1[key] != map2[key]) {
        return false;
      }
    }

    for (var key in map2) {
      if (map2[key] != map1[key]) {
        return false;
      }
    }

    return true;
  }

  // 根据规格查询SKU
  searchSku = function () {
    for (var i = 0; i < skuList.length; i++) {
      if (matchObject(skuList[i].spec, $scope.specificationItems)) {
        $scope.sku = skuList[i];
        return;
      }
    }
    $scope.sku = {id: 0, title: '------', price: 0};

  }

  // 添加购物车，
  $scope.addSkuToCart = function () {
    // alert("SKUID:" + $scope.sku.id);

    $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId=" +
        $scope.sku.id + "&num=" + $scope.num,
        {'withCredentials': true}).success(
        function (response) {
          if (response.success) {
            // 添加成功跳转到购物车页面
            location.href = "http://localhost:9107/cart.html";
          } else {
            alert(response.message);
          }

        }
    );

  }

})